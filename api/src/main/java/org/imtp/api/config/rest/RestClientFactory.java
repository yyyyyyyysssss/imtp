package org.imtp.api.config.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Component
@Slf4j
public class RestClientFactory {

    private final ObjectMapper objectMapper;

    public RestClientFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RestClient create(ClientHttpRequestFactory requestFactory, String baseUrl, Consumer<RestClient.Builder> customizer) {
        List<HttpMessageConverter<?>> messageConverters = defaultMessageConverters();

        return RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(baseUrl)
                .apply(customizer)
                .defaultStatusHandler(HttpStatusCode::isError, this::logError)
                .messageConverters(messageConverters)
                .requestInterceptor(new RestClientLogInterceptor())
                .build();
    }

    private List<HttpMessageConverter<?>> defaultMessageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new FormHttpMessageConverter());
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        return converters;
    }

    private void logError(HttpRequest req, ClientHttpResponse res) throws IOException {
        log.error("request error {} {}", req.getURI(), res.getStatusCode().value());
    }

    // 请求日志打印
    private record RestClientLogInterceptor() implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(@NotNull HttpRequest request, @NotNull byte[] body, @NotNull ClientHttpRequestExecution execution) throws IOException {
            MediaType reqType = request.getHeaders().getContentType();
            String requestContentType = reqType != null ? reqType.toString() : "";
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append(String.format("Request: [%s] %s", request.getMethod(), request.getURI()));
            // 仅在 Content-Type 为 JSON 或 Text 类型时记录请求体内容
            if(isJsonOrTextRequest(requestContentType)){
                logBuilder.append(String.format("   ,Args: %s", new String(body,StandardCharsets.UTF_8)));
            } else {
                logBuilder.append("   ,Args: [Content omitted due to type]");
            }
            // 记录请求开始时间
            long s = System.currentTimeMillis();
            ClientHttpResponse response;
            try {
                // 执行请求
                response = execution.execute(request, body);
            } catch (Exception e) {
                long diff = System.currentTimeMillis() - s;
                logBuilder.append(String.format("   Spend: %f s", diff / 1000.0));
                logBuilder.append(String.format("   ,Exception: %s", e.getMessage()));
                log.error(logBuilder.toString());
                throw e;
            }
            // 记录响应的时间
            long diff = System.currentTimeMillis() - s;
            logBuilder.append(String.format("   Spend: %f s", diff / 1000.0));
            // 获取响应的 Content-Type
            String responseContentType = response.getHeaders().getContentType().toString();
            // 仅在 Content-Type 为 JSON 或 Text 类型时记录响应体内容
            if(isJsonOrTextRequest(responseContentType)){
                InputStream inputStream = response.getBody();
                byte[] responseBody = StreamUtils.copyToByteArray(inputStream);
                logBuilder.append(String.format("   ,Return: %s", new String(responseBody,StandardCharsets.UTF_8)));
                log.info(logBuilder.toString());
                // 创建新的响应对象并返回缓存的响应体
                return new ClientHttpResponseWrapper(response, responseBody);
            }
            // 对于非 JSON 或 Text 类型的响应，跳过响应体日志记录
            logBuilder.append("   ,Return: [Content omitted due to type]");
            log.info(logBuilder.toString());
            return response;
        }

        private boolean isJsonOrTextRequest(String contentType) {
            // 检查 Content-Type 是否包含 "application/json" 或 "text/"
            return contentType.contains("application/json") || contentType.startsWith("text/");
        }
    }

    private record ClientHttpResponseWrapper(ClientHttpResponse originalResponse,
                                             byte[] responseBody) implements ClientHttpResponse {
        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return originalResponse.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return originalResponse.getStatusText();
        }

        @Override
        public void close() {
            originalResponse.close();
        }

        @Override
        public InputStream getBody() {

            return new ByteArrayInputStream(responseBody);
        }

        @Override
        public HttpHeaders getHeaders() {
            return originalResponse.getHeaders();
        }
    }

}
