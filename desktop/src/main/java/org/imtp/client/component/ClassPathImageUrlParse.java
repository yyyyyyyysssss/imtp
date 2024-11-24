package org.imtp.client.component;

import org.imtp.client.util.ResourceUtils;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/21 14:04
 */
public class ClassPathImageUrlParse implements ImageUrlParse {

    private final String prefix = "classpath:";

    @Override
    public String loadUrl(String originalUrl) {
        if (originalUrl == null){
            return ResourceUtils.classPathResource("/img/tmp.jpg").toExternalForm();
        }
        if(!originalUrl.startsWith(prefix)){
            return originalUrl;
        }
        originalUrl = originalUrl.replaceAll(prefix,"");
        return ResourceUtils.classPathResource(originalUrl).toExternalForm();
    }
}
