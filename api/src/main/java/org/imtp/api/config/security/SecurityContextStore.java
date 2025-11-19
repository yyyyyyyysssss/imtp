package org.imtp.api.config.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.SecurityContextRepository;

public interface SecurityContextStore extends SecurityContextRepository {

    void saveContext(SecurityContext context,String tokenId);

    boolean clearContext(String tokenId);

}
