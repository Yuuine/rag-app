package yuuine.ragapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import yuuine.ragapp.common.Result;
import yuuine.ragapp.dto.request.VectorAddRequest;
import yuuine.ragapp.dto.request.VectorAddResult;
import yuuine.ragapp.dto.response.RagIngestResponse;
import yuuine.ragapp.ragIngestService.RagIngestService;
import yuuine.ragapp.ragVectorService.RagVectorService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class AppController {

    private final RagIngestService ragIngestService;
    private final RagVectorService ragVectorService;

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
        System.out.println(chunks);

        return Result.success(vectorAddResult);
    }

}
