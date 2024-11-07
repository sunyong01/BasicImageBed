package web.sy.basicimagebed.configuration;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI ImageBedOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("基础图床API")
                        .description("论坛系统前后端分离API测试")
                        .version("v1"));

    }
}
