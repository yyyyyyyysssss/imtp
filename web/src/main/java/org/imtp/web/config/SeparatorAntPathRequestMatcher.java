package org.imtp.web.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/9/10 14:02
 */
public class SeparatorAntPathRequestMatcher implements RequestMatcher{

    private List<AntPathRequestMatcher> matchers;

    private String defaultSeparator;

    public SeparatorAntPathRequestMatcher(String[] paths){
        this(paths,",");
    }

    public SeparatorAntPathRequestMatcher(String[] antPaths,String defaultSeparator){
        if(antPaths == null || antPaths.length == 0){
            throw new NullPointerException("paths not null");
        }
        this.matchers = new ArrayList<>();
        for (String antPath : antPaths){
            matchers.add(new AntPathRequestMatcher(antPath));
        }
        this.defaultSeparator = defaultSeparator;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return matchers.stream().anyMatch(matcher -> matcher.matches(request));
    }
}
