//package io.electrica.common.config;
//
//import feign.RequestInterceptor;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableFeignClients
//public class FeignClientsConfig {
//
//    @Bean
//    public RequestInterceptor authRequestInterceptor(IdentityContextHolder identityContextHolder) {
//        return template -> {
//            Identity identity = identityContextHolder.getIdentity();
//            if (identity.isAuthenticated()) {
//                template.header("Authorization", "Bearer " + identity.getToken());
//            }
//        };
//    }
//}
