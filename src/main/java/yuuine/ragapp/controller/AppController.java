package yuuine.ragapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yuuine.ragapp.dto.common.Result;
import yuuine.ragapp.dto.request.InferenceRequest;
import yuuine.ragapp.dto.request.VectorAddRequest;
import yuuine.ragapp.dto.request.VectorAddResult;
import yuuine.ragapp.dto.response.RagInferenceResponse;
import yuuine.ragapp.dto.response.RagIngestResponse;
import yuuine.ragapp.ragInferenceService.RagInferenceService;
import yuuine.ragapp.ragIngestService.RagIngestService;
import yuuine.ragapp.ragVectorService.RagVectorService;
import yuuine.ragapp.ragVectorService.VectorSearchResult;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class AppController {

    private final RagIngestService ragIngestService;
    private final RagVectorService ragVectorService;
    private final RagInferenceService ragInferenceService;

    @PostMapping("/upload")
    public Result<Object> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {

        // 1. 调用 rag-ingestion 服务，得到 chunk 结果
        RagIngestResponse ragIngestResponse = ragIngestService.upload(files);

        // 2. 调用 rag-vector 服务，持久化 chunk
        List<RagIngestResponse.ChunkResponse> chunkResponses = ragIngestResponse.getChunks();
        //类型转换
        List<VectorAddRequest> chunks = chunkResponses.stream()
                .map(chunk -> new VectorAddRequest(
                        chunk.getChunkId(),
                        chunk.getFileMd5(),
                        chunk.getSource(),
                        chunk.getChunkIndex(),
                        chunk.getChunkText(),
                        chunk.getCharCount()
                ))
                .toList();
        VectorAddResult vectorAddResult =
                ragVectorService.add(chunks);

        return Result.success(vectorAddResult);
    }

    @PostMapping("/search")
    public Result<Object> search(
            @RequestBody InferenceRequest query
    ) {

        // 1. 调用 rag-vector 服务，将搜索语句向量化处理，得到结果列表
        List<VectorSearchResult> vectorSearchResults
                = ragVectorService.search(query);

        // 2. 调用 rag-inference 服务，将问题和得到的结果列表传入 LLM 模型进行推理
        RagInferenceResponse ragInferenceResponse =
                ragInferenceService.inference(query, vectorSearchResults);

        // 3. 返回结果

        return Result.success(ragInferenceResponse.getAnswer());
    }

}
