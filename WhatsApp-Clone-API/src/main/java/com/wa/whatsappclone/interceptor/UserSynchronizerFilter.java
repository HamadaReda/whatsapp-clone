//package com.wa.whatsappclone.interceptor;
//
//import com.wa.whatsappclone.user.UserSynchronizer;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//public class UserSynchronizerFilter extends OncePerRequestFilter {
//
//    private final UserSynchronizer userSynchronizer;
//
//    public UserSynchronizerFilter(UserSynchronizer userSynchronizer) {
//        this.userSynchronizer = userSynchronizer;
//    }
//
//    @Override
//    protected void doFilterInternal( HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if(authentication instanceof JwtAuthenticationToken jwtAuth) {
//            userSynchronizer.synchronizeWithIdP(jwtAuth.getToken());
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
