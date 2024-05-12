package org.imtp.client.util;

import java.net.URL;

public class ResourceUtils {

    public static URL classPathResource(final String path) {
        String pathToUse = path;
        if (path.startsWith("/")) {
            pathToUse = path.substring(1);
        }
        ClassLoader cl = ClassUtils.getDefaultClassLoader();
        return cl.getResource(pathToUse);
    }

}
