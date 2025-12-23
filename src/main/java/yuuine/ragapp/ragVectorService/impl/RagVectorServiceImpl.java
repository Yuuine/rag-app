package yuuine.ragapp.ragVectorService.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuuine.ragapp.client.VectorClient;
import yuuine.ragapp.dto.request.VectorAddRequest;
import yuuine.ragapp.dto.request.VectorAddResult;
import yuuine.ragapp.dto.response.RagIngestResponse;
import yuuine.ragapp.ragVectorService.RagVectorService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RagVectorServiceImpl implements RagVectorService {

    private final VectorClient vectorClient;

    @Override
    public VectorAddResult add(List<VectorAddRequest> chunks) {

        VectorAddResult vectorAddResult = vectorClient.add(chunks);
        if (vectorAddResult == null) {
            throw new RuntimeException("Vector service failed");
        }
        return vectorAddResult;
    }
}
