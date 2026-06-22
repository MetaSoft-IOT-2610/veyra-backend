package com.metasoft.veyra.platform.tracking.infrastructure.authorization;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public class EdgeGatewayAuthentication extends AbstractAuthenticationToken {

    public static final String ROLE_EDGE_GATEWAY = "ROLE_EDGE_GATEWAY";

    private final EdgeGatewayPrincipal principal;

    public EdgeGatewayAuthentication(EdgeGatewayPrincipal principal) {
        super(List.of(new SimpleGrantedAuthority(ROLE_EDGE_GATEWAY)));
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public EdgeGatewayPrincipal getPrincipal() {
        return principal;
    }
}
