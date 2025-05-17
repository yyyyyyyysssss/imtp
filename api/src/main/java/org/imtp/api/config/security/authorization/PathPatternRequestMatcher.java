package org.imtp.api.config.security.authorization;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.PathContainer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @Description
 * @Author ys
 * @Date 2025/5/17 23:40
 */
public class PathPatternRequestMatcher implements RequestMatcher {

    private final PathPattern pattern;

    private final String httpMethod;

    public PathPatternRequestMatcher(String pattern) {
        this(pattern,null);
    }

    public PathPatternRequestMatcher(String pattern, String httpMethod) {
        this.pattern = new PathPatternParser().parse(pattern);
        this.httpMethod = httpMethod != null ? httpMethod.toUpperCase() : null;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (httpMethod != null && !httpMethod.equalsIgnoreCase(request.getMethod())){
            return false;
        }
        PathContainer path = PathContainer.parsePath(request.getRequestURI());
        return pattern.matches(path);
    }
}
