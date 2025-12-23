package yuuine.ragapp.ragInferenceService.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import yuuine.ragapp.client.InferenceClient;
import yuuine.ragapp.config.RagPromptProperties;
import yuuine.ragapp.dto.request.InferenceRequest;
import yuuine.ragapp.dto.response.InferenceResponse;
import yuuine.ragapp.dto.response.RagInferenceResponse;
import yuuine.ragapp.ragInferenceService.RagInferenceService;
import yuuine.ragapp.ragVectorService.VectorSearchResult;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RagInferenceServiceImpl implements RagInferenceService {

    private final InferenceClient inferenceClient;
    private final RagPromptProperties ragPromptProperties;

    @Override
    public RagInferenceResponse inference(InferenceRequest appRequest,
                                          List<VectorSearchResult> vectorSearchResults) {
        try {
            // 构建上下文
            String context = buildContext(vectorSearchResults);

            // 从配置中获取系统提示词
            String systemPrompt = ragPromptProperties.getSystemPrompt();

            // 构建用户提示词
            String userPrompt = buildUserPrompt(context, appRequest.getQuery());

            // 组合最终提示词 - 将系统提示词和用户提示词合并
            String combinedPrompt = systemPrompt + "\n\n" + userPrompt;

            // 构造发送给推理服务的请求 - 使用合并后的提示词
            InferenceRequest inferenceReq = buildInferenceRequest(combinedPrompt);

            // 调用推理服务
            InferenceResponse inferenceResponse = inferenceClient.chat(inferenceReq);

            // 封装返回结果
            return buildResponse(appRequest, inferenceResponse, vectorSearchResults);

        } catch (Exception e) {
            log.error("推理服务调用失败", e);
            throw new RuntimeException("推理服务调用失败: " + e.getMessage(), e);
        }
    }

    private String buildContext(List<VectorSearchResult> vectorSearchResults) {
        if (vectorSearchResults == null || vectorSearchResults.isEmpty()) {
            return "";
        }

        return vectorSearchResults.stream()
                .sorted((a, b) -> Float.compare(b.getScore(), a.getScore()))  // 按分数降序
                .map(result -> "来源：" + result.getSource() +
                        "（块索引：" + result.getChunkIndex() + "）\n" +
                        result.getContent())
                .collect(Collectors.joining("\n\n"));
    }

    private String buildUserPrompt(String context, String query) {
        return """
                相关文档内容：
                %s
                
                用户问题：%s
                """.formatted(context.isEmpty() ? "（无相关文档）" : context, query);
    }

    private InferenceRequest buildInferenceRequest(String prompt) {
        InferenceRequest inferenceReq = new InferenceRequest();
        inferenceReq.setQuery(prompt); // 使用合并后的提示词作为查询
        return inferenceReq;
    }

    private RagInferenceResponse buildResponse(InferenceRequest appRequest,
                                               InferenceResponse inferenceResponse,
                                               List<VectorSearchResult> vectorSearchResults) {
        RagInferenceResponse response = new RagInferenceResponse();
        response.setQuery(appRequest.getQuery());
        response.setAnswer(inferenceResponse.getAnswer());

        // 构建引用信息
        List<RagInferenceResponse.Reference> references = buildReferences(vectorSearchResults);
        response.setReferences(references);

        return response;
    }

    private List<RagInferenceResponse.Reference> buildReferences(List<VectorSearchResult> vectorSearchResults) {
        if (vectorSearchResults == null || vectorSearchResults.isEmpty()) {
            return List.of();
        }

        return vectorSearchResults.stream()
                .map(v -> {
                    RagInferenceResponse.Reference ref = new RagInferenceResponse.Reference();
                    ref.setChunkId(v.getChunkId());
                    ref.setSource(v.getSource());
                    ref.setChunkIndex(v.getChunkIndex());
                    ref.setContent(v.getContent());
                    ref.setScore(v.getScore());
                    return ref;
                })
                .collect(Collectors.toList());
    }
}
