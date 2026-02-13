package com.wa.whatsappclone.security;

import jakarta.validation.constraints.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt source) {
        return new JwtAuthenticationToken(source,
                Stream.concat(
                        new JwtGrantedAuthoritiesConverter().convert(source).stream(),
                        extractResourceRoles(source).stream()
                )
                        .collect(Collectors.toSet())
        );
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(@NotNull Jwt source) {
        Map<String, Object> resourceAccess = source.getClaim("resource_access");
        if(resourceAccess == null) {
            return Set.of();
        }
        Map<String, Object> account = (Map<String, Object>)resourceAccess.get("account");
        if(account == null) {
            return Set.of();
        }
        List<String> roles = (List<String>)account.get("roles");
        if(roles == null) {
            return Set.of();
        }
        return roles.stream().map(role ->
                new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                    .collect(Collectors.toSet()
        );
    }

}
