package yuuine.ragapp.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import yuuine.ragapp.dto.request.VectorAddRequest;
import yuuine.ragapp.dto.request.VectorAddResult;

import java.util.List;

@FeignClient(name = "rag-vector", url = "${services.vector.base-url}")
public interface VectorClient {

    @PostMapping(value = "/vectors/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    VectorAddResult add(
            @RequestBody List<VectorAddRequest> chunks);

//    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)

}
