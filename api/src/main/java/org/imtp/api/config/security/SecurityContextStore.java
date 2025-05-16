package org.imtp.api.config.security;

import org.springframework.security.web.context.SecurityContextRepository;

public interface SecurityContextStore extends SecurityContextRepository {

    boolean clearContext(String tokenId);

}
