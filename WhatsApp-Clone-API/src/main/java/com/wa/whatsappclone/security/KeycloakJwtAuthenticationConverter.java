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
        Set<GrantedAuthority> authorities = Stream.of(
                        extractScopeAuthorities(source),
                        extractRealmRoles(source),
                        extractResourceRoles(source),
                        extractAccountRoles(source)
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return new JwtAuthenticationToken(source, authorities);
    }

    private Collection<? extends GrantedAuthority> extractScopeAuthorities(Jwt source) {

        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();

        return converter.convert(source);
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt source) {

        Map<String, Object> realmAccess = source.getClaim("realm_access");

        if (realmAccess == null) {
            return Set.of();
        }

        List<String> roles = (List<String>) realmAccess.get("roles");

        if (roles == null) {
            return Set.of();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt source) {

        Map<String, Object> resourceAccess = source.getClaim("resource_access");

        if (resourceAccess == null) {
            return Set.of();
        }

        Map<String, Object> client = (Map<String, Object>) resourceAccess.get("whatsapp-clone-app");

        if (client == null) {
            return Set.of();
        }

        List<String> roles = (List<String>) client.get("roles");

        if (roles == null) {
            return Set.of();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> extractAccountRoles(Jwt source) {

        Map<String, Object> resourceAccess = source.getClaim("resource_access");

        if (resourceAccess == null) {
            return Set.of();
        }

        Map<String, Object> account = (Map<String, Object>) resourceAccess.get("account");

        if (account == null) {
            return Set.of();
        }

        List<String> roles = (List<String>) account.get("roles");

        if (roles == null) {
            return Set.of();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.replace("-", "_")))
                .collect(Collectors.toSet());
    }



}
