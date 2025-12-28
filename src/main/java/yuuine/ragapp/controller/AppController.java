package yuuine.ragapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yuuine.ragapp.docService.DocService;
import yuuine.ragapp.dto.common.Result;
import yuuine.ragapp.dto.request.InferenceRequest;
import yuuine.ragapp.dto.request.VectorAddRequest;
import yuuine.ragapp.dto.request.VectorAddResult;
import yuuine.ragapp.dto.response.DocList;
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
@Slf4j
public class AppController {

    private final RagIngestService ragIngestService;
    private final RagVectorService ragVectorService;
    private final RagInferenceService ragInferenceService;
    private final DocService docService;

    @PostMapping("/upload")
    public Result<Object> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {
        log.info("收到上传请求，文件数量: {}", files.size());

        try {
            // 1. 调用 rag-ingestion 服务，得到 chunk 结果
            RagIngestResponse ragIngestResponse = ragIngestService.upload(files);
            log.debug("文件解析完成，chunks数量: {}", ragIngestResponse.getChunks().size());

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
            log.debug("准备向量存储，chunks数量: {}", chunks.size());
            VectorAddResult vectorAddResult =
                    ragVectorService.add(chunks);

            // 持久化到 MySQL
            docService.saveDoc(
                    chunkResponses.get(0).getFileMd5(),
                    chunkResponses.get(0).getSource()
            );
            log.info("文件 MySQL 持久化完成");

            log.info("文件上传处理完成，成功: {}, 失败: {}",
                    vectorAddResult.getSuccessChunk(), vectorAddResult.getFailedChunk());

            return Result.success(vectorAddResult);
        } catch (Exception e) {
            log.error("上传处理失败", e);
            return Result.error("上传处理失败: " + e.getMessage());
        }
    }

    @GetMapping("/getDoc")
    public Result<Object> getDoc(
    ) {
        DocList docList = docService.getDoc();

        return Result.success(docList);
    }

    @PostMapping("/delete")
    public Result<Object> deleteDocuments(
            @RequestBody List<String> fileMd5s
    ) {
        if (fileMd5s == null || fileMd5s.isEmpty()) {
            return Result.error("fileMd5 列表不能为空");
        }
        docService.deleteDocuments(fileMd5s);
        return Result.success();
    }


    @PostMapping("/search")
    public Result<Object> search(
            @RequestBody InferenceRequest query
    ) {
        log.info("收到搜索请求，查询: {}", query.getQuery());

        try {
            // 1. 调用 rag-vector 服务，将搜索语句向量化处理，得到结果列表
            List<VectorSearchResult> vectorSearchResults
                    = ragVectorService.search(query);

            // 2. 调用 rag-inference 服务，将问题和得到的结果列表传入 LLM 模型进行推理
            RagInferenceResponse ragInferenceResponse =
                    ragInferenceService.inference(query, vectorSearchResults);
            log.info("-----------------------------------------------------------");

            // 3. 返回结果
            return Result.success(ragInferenceResponse.getAnswer());
        } catch (Exception e) {
            log.error("搜索处理失败，查询: {}", query.getQuery(), e);
            return Result.error("搜索处理失败: " + e.getMessage());
        }
    }

}
