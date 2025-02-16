package com.moksh.imposterai.config;

import com.moksh.imposterai.entities.UserEntity;
import com.moksh.imposterai.handler.WebSocketHandler;
import com.moksh.imposterai.services.JwtService;
import com.moksh.imposterai.services.UserService;
import com.moksh.imposterai.services.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final UserService userServices;
    private final JwtService jwtService;
    private final WebSocketService webSocketService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(
                        webSocketService
                ), "/game")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        String token = request.getHeaders().getFirst("Authorization");
                        if (token != null && token.startsWith("Bearer ")) {
                            token = token.substring(7);
                            String userId = jwtService.getUserId(token);
                            UserEntity user = userServices.loadUserById(userId);
                            attributes.put("User", user);
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, org.springframework.web.socket.WebSocketHandler wsHandler, Exception exception) {

                    }
                })
                .setAllowedOrigins("*");
    }
}
