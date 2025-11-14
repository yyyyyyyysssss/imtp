package org.imtp.api.config.security.authentication.apikey;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/10 14:02
 */
public class SeparatorAntPathRequestMatcher implements RequestMatcher{

    private List<RequestMatcher> matchers;

    public SeparatorAntPathRequestMatcher(String[] antPaths){
        if(antPaths == null || antPaths.length == 0){
            throw new NullPointerException("paths not null");
        }
        this.matchers = new ArrayList<>();
        for (String antPath : antPaths){
            matchers.add(PathPatternRequestMatcher.withDefaults().matcher(antPath));
        }
    }

    @Override
    public boolean matches(HttpServletRequest request) {

        return matchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
