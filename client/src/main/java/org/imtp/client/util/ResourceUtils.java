package org.imtp.client.util;

import java.net.URL;

public class ResourceUtils {

    public static URL classPathResource(final String path) {
        return ResourceUtils.class.getResource(path);
    }
}
