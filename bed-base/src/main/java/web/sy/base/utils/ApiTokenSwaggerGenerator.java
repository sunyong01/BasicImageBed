package web.sy.base.utils;

import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import web.sy.base.config.condition.InitializedCondition;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Set;

@Slf4j
@DependsOn("customOpenAPI")
@Component
@Conditional(InitializedCondition.class)
public class ApiTokenSwaggerGenerator {

    @Autowired
    private OpenApiResource openApiResource;

    @Autowired
    @Qualifier("apiTokenUrls")
    private Set<String> apiTokenUrls;

    private OpenAPI cachedApiTokenOpenAPI;

    public OpenAPI generateApiTokenSwaggerJson() {
        if (cachedApiTokenOpenAPI != null) {
            return cachedApiTokenOpenAPI;
        }

        try {
            // 使用反射获取 AbstractOpenApiResource 中的 getOpenApi 方法
            Method getOpenApiMethod = openApiResource.getClass()
                    .getSuperclass() // 获取 OpenApiResource
                    .getSuperclass() // 获取 AbstractOpenApiResource
                    .getDeclaredMethod("getOpenApi", Locale.class); // 获取带 Locale 参数的 getOpenApi 方法
            getOpenApiMethod.setAccessible(true);
            
            // 调用 getOpenApi 方法
            OpenAPI fullOpenAPI = (OpenAPI) getOpenApiMethod.invoke(openApiResource, Locale.getDefault());

            // 创建新的 OpenAPI 实例，只包含 API Token 相关的路径
            OpenAPI apiTokenOpenAPI = new OpenAPI()
                    .info(fullOpenAPI.getInfo())
                    .components(fullOpenAPI.getComponents());

            if (fullOpenAPI.getServers() != null) {
                apiTokenOpenAPI.setServers(fullOpenAPI.getServers());
            }

            // 只保留 API Token 相关的路径
            if (fullOpenAPI.getPaths() != null) {
                fullOpenAPI.getPaths().forEach((path, item) -> {
                    if (apiTokenUrls.contains(path)) {
                        apiTokenOpenAPI.path(path, item);
                    }
                });
            }

            // 缓存生成的 OpenAPI 文档
            cachedApiTokenOpenAPI = apiTokenOpenAPI;

            return apiTokenOpenAPI;
        } catch (Exception e) {
            log.error("Failed to generate API Token OpenAPI document", e);
            throw new RuntimeException("Failed to generate API Token OpenAPI document", e);
        }
    }
}