package org.imtp.api.config.webdav;

import lombok.RequiredArgsConstructor;
import org.imtp.api.utils.MD5Utils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class WebDavClient {

    private final RestClient webDavRestClient;

    private final WebDavProperties webDavProperties;

    private AtomicInteger nonceCount = new AtomicInteger(1);




    public String getAuthorization(){
        String nonce = getNonce();
        String username = webDavProperties.getUsername();
        String password = webDavProperties.getPassword();
        String uri = "/";
        String method = "GET";
        String qop = "auth";
        String nc = String.format("%08d", nonceCount.getAndIncrement());  // 使用递增的nonce计数
        String cnonce = UUID.randomUUID().toString().replace("-", ""); //使用UUID生成cnonce
        String a1 = MD5Utils.getMD5(username + ":" + "WebDAV" + ":" + password);
        String a2 = MD5Utils.getMD5(method + ":" + uri);
        String response = MD5Utils.getMD5(a1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + a2);
        return "Digest username=\"" + username + "\", realm=\"WebDAV\", nonce=\"" + nonce + "\", uri=\"" + uri + "\", qop=" + qop +
                ", nc=" + nc + ", cnonce=\"" + cnonce + "\", response=\"" + response + "\"";
    }

    private String getNonce(){
        ResponseEntity<String> response = webDavRestClient
                .get()
                .uri("/")
                .retrieve()
                .toEntity(String.class);
        HttpHeaders headers = response.getHeaders();
        String authenticateHeader = headers.getFirst(HttpHeaders.WWW_AUTHENTICATE);
        String noncePrefix = "nonce=\"";
        int startIndex = authenticateHeader.indexOf(noncePrefix);
        if (startIndex == -1) return null;
        startIndex += noncePrefix.length();
        int endIndex = authenticateHeader.indexOf("\"", startIndex);
        if (endIndex == -1) return null;
        return authenticateHeader.substring(startIndex, endIndex);
    }

}
