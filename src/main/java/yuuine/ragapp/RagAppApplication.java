package yuuine.ragapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "yuuine.ragapp.client")
@SpringBootApplication
public class RagAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagAppApplication.class, args);
    }

}
