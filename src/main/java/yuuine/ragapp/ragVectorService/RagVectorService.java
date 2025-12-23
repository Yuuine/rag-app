package yuuine.ragapp.ragVectorService;

import yuuine.ragapp.dto.request.VectorAddRequest;
import yuuine.ragapp.dto.request.VectorAddResult;
import yuuine.ragapp.dto.response.RagIngestResponse;

import java.util.List;

public interface RagVectorService {

    VectorAddResult add(List<VectorAddRequest> chunks);

}
