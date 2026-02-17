package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.example.demo.model.MyUserDetails;
import com.example.demo.service.JWTService;
import com.example.demo.service.MyUserDetailsService;



@Configuration
@EnableWebSocketMessageBroker // enables STOMP over WebSocket
public class SocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the endpoint clients connect to, e.g. ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:4200","http://localhost:8080") // allow Angular client
                .withSockJS(); // fallback for older browsers ,,, works because we set spring.web.socket.sock-js.enabled=true in application.properties so spring creates a bean of type SockJsService

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for messages going from server to client
        registry.enableSimpleBroker("/topic");

        // Prefix for messages sent from client to server
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Autowired
    private JWTService jwtService;
    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =StompHeaderAccessor.wrap(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {//"CONNECT" works here but we use the enum for better readability and to avoid typos
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        String username = jwtService.extractUserName(token);
                        if (username != null) {
                            MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(username);
                            if (jwtService.validateToken(token, userDetails)) {
                                UsernamePasswordAuthenticationToken authToken =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                accessor.setUser(authToken);//this happens when a client connect to web socket ,spring creates a new message from the old one and add this user to its headers and then the message is sent to the controller and become a stateful connection .
                            }
                        }
                    }
                }
                return message;
            }
        });
    }
}

//WebSocket is A persistent, two-way communication channel between browser and server over a single TCP connection
//SockJS = WebSocket that works everywhere, even where WebSockets don't.
//STOMP is the language used for messaging between the client and server in a WebSocket connection. It defines a set of commands and conventions for sending messages, subscribing to topics, etc. By enabling STOMP over WebSocket, we can use these conventions to structure our messages and manage subscriptions more effectively.