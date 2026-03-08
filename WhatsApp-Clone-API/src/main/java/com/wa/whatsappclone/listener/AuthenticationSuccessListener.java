//package com.wa.whatsappclone.listener;
//
//import com.wa.whatsappclone.user.UserSynchronizer;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.event.EventListener;
//import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class AuthenticationSuccessListener {
//
//    private final UserSynchronizer userSynchronizer;
//
//    @EventListener
//    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
//        if(!(event.getAuthentication() instanceof JwtAuthenticationToken token)){
//            return;
//        }
//        Jwt jwt = token.getToken();
//        System.out.println("User Authentication Success: " + jwt.getSubject());
//        userSynchronizer.synchronizeWithIdP(jwt);
//    }
//
//}
