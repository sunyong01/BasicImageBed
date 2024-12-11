package web.sy.bed.base.config;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import web.sy.bed.base.annotation.Anonymous;
import web.sy.bed.base.annotation.ApiTokenSupport;
import web.sy.bed.base.config.condition.InitializedCondition;
import web.sy.bed.base.security.JwtAuthenticationFilter;
import web.sy.bed.base.security.SwaggerAccessFilter;
import web.sy.bed.base.security.RefererControlFilter;
//import web.sy.bed.base.security.SystemURLFilter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


@Slf4j
@Configuration
@EnableWebSecurity
@Conditional(InitializedCondition.class)
public class SecurityConfig {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Lazy
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((authorizeHttpRequests) -> {
                    authorizeHttpRequests.requestMatchers(
                                    getAnonymousUrls()
                            ).permitAll()
                            .anyRequest()
                            .authenticated();
                })

                .addFilterBefore(new SwaggerAccessFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RefererControlFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public Set<String> anonymousUrls() {
        String[] urls = getAnonymousUrls();
        return new HashSet<>(Set.of(urls));
    }

    public String[] getAnonymousUrls() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        Set<String> allAnonymousAccess = new HashSet<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo info = entry.getKey();

            if (handlerMethod.getMethodAnnotation(Anonymous.class) != null ||
                    handlerMethod.getBeanType().isAnnotationPresent(Anonymous.class)) {

                if (info.getPatternsCondition() != null) {
                    allAnonymousAccess.addAll(info.getPatternsCondition().getPatterns());
                }

                if (info.getPathPatternsCondition() != null) {
                    allAnonymousAccess.addAll(info.getPathPatternsCondition().getPatternValues());
                }
            }
        }

        String[] predefinedUrls = {
                "/",
                "/assets/**",
                "/imgs/**",
                "/index.html",
                "/static/**"};
        String[] result = Stream.concat(
                Stream.concat(allAnonymousAccess.stream(), Stream.of(predefinedUrls)), // 合并 allAnonymousAccess 和 predefinedUrls
                Stream.of(GlobalConfig.getSwaggerUrls()) // 再合并 Swagger URLs
        ).toArray(String[]::new);
        log.debug("Anonymous urls: {}", (Object) result);

        return result;
    }

    @Bean
    public Set<String> apiTokenUrls() {
        String[] urls = getApiTokenUrls();
        return new HashSet<>(Set.of(urls));
    }

    private String[] getApiTokenUrls() {
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        Set<String> allApiTokenAccess = new HashSet<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            RequestMappingInfo info = entry.getKey();

            if (handlerMethod.getMethodAnnotation(ApiTokenSupport.class) != null ||
                    handlerMethod.getBeanType().isAnnotationPresent(ApiTokenSupport.class)) {

                if (info.getPatternsCondition() != null) {
                    allApiTokenAccess.addAll(info.getPatternsCondition().getPatterns());
                }

                if (info.getPathPatternsCondition() != null) {
                    allApiTokenAccess.addAll(info.getPathPatternsCondition().getPatternValues());
                }
            }
        }

        String[] result = allApiTokenAccess.toArray(new String[0]);
        log.info("API Token supported urls: {}", (Object) result);

        return result;
    }

}
