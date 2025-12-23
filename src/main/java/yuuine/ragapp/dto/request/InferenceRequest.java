package yuuine.ragapp.dto.request;

import lombok.Data;


@Data
public class InferenceRequest {

    private String query;

    private Integer topK = 5;

    // LLM 生成温度，控制创造性，默认 0.7
    private Double temperature = 0.7;

}