package yuuine.ragapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import yuuine.ragapp.common.Result;
import yuuine.ragapp.dto.response.RagIngestResponse;
import yuuine.ragapp.ragIngestService.RagIngestService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app")
public class AppController {

    private final RagIngestService ragIngestService;

    @PostMapping("/upload")
    public Result<Object> upload(
            @RequestParam("files") List<MultipartFile> files
    ) {

        // 1. 调用 rag-ingestion 服务，得到 chunk 结果
        RagIngestResponse ragIngestResponse = ragIngestService.upload(files);

        // 2. 调用 rag-vector 服务，持久化 chunk

        return Result.success();
    }

}
