package yuuine.ragapp.ragIngestService.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import yuuine.ragapp.client.IngestionClient;
import yuuine.ragapp.dto.response.RagIngestResponse;
import yuuine.ragapp.ragIngestService.RagIngestService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RagIngestServiceImpl implements RagIngestService {


    private final IngestionClient ingestionClient;

    @Override
    public RagIngestResponse upload(List<MultipartFile> files) {

        // 1. 将文件列表以 List<MultipartFile> files 的形式传入 app-ingestion
        // 2. 将得到的 chunks 封装返回 控制器，等待控制器下一步处理
        RagIngestResponse response = ingestionClient.ingest(files);
        if (response == null || response.getChunks() == null) {
            throw new RuntimeException("Ingestion service failed");
        }

        return response;
    }
}
