package com.vanhuy.chatapp.controller;

import com.vanhuy.chatapp.model.Message;
import com.vanhuy.chatapp.model.User;
import com.vanhuy.chatapp.repository.UserRepository;
import com.vanhuy.chatapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthService authService;

    // Use a thread-safe set to store connected users
    final Set<User> connectedUsers = ConcurrentHashMap.newKeySet();

    @MessageMapping("/connect")
    @SendTo("/topic/public")
    public Message handleUserConnection(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received connection payload: {}", payload);
        String token = payload.get("content");
        Long userId = authService.validateToken(token);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        headerAccessor.getSessionAttributes().put("username", user.getUsername());
        headerAccessor.getSessionAttributes().put("userId", user.getUserId());

        // Add user to connected users set
        connectedUsers.add(user);

        Message message = createMessage(user, user.getUsername() + " joined the chat", Message.MessageType.JOIN);

        // Broadcast updated user list to all connected clients
        broadcastUserList();

        return message;
    }

    @MessageMapping("/disconnect")
    @SendTo("/topic/public")
    public Message handleUserDisconnection(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received disconnection payload: " + payload);
        String username = payload.get("content");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        User user = userRepository.findById(userId).orElseThrow();

        // Remove user from connected users set
        connectedUsers.remove(user);
        Message message = createMessage(user, username + " left the chat", Message.MessageType.LEAVE);

        log.info("User disconnected: {}", username);

        // Broadcast updated user list to all connected clients
        broadcastUserList();

        return message;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public Message sendMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = authService.validateToken(token);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        message.setSender(sender);
        message.setTimestamp(LocalDateTime.now());
        message.setType(Message.MessageType.CHAT);
        log.info("Message sent: {}", message);
        return message;
    }

    private void broadcastUserList() {
        simpMessagingTemplate.convertAndSend("/topic/users", List.copyOf(connectedUsers));
    }

    private Message createMessage(User user, String content, Message.MessageType type) {
        Message message = new Message();
        message.setSender(user);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setType(type);
        return message;
    }
}
