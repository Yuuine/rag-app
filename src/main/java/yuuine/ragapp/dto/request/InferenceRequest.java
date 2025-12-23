package yuuine.ragapp.dto.request;

import lombok.Data;


@Data
public class InferenceRequest {

    private String query;

    private Integer topK = 5;

}