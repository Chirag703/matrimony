package com.matrimony.controller;

import com.matrimony.dto.ApiResponse;
import com.matrimony.dto.ChatDto;
import com.matrimony.dto.MessageDto;
import com.matrimony.entity.User;
import com.matrimony.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatDto>>> getChats(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getUserChats(currentUser.getId())));
    }

    @PostMapping("/start/{otherUserId}")
    public ResponseEntity<ApiResponse<ChatDto>> startChat(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long otherUserId) {
        ChatDto chat = chatService.startChat(currentUser.getId(), otherUserId);
        return ResponseEntity.ok(ApiResponse.success("Chat started", chat));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<List<MessageDto>>> getMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long chatId) {
        return ResponseEntity.ok(ApiResponse.success(
                chatService.getMessages(chatId, currentUser.getId())));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<MessageDto>> sendMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long chatId,
            @RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Message cannot be empty"));
        }
        MessageDto sent = chatService.sendMessage(chatId, currentUser.getId(), message);
        return ResponseEntity.ok(ApiResponse.success("Message sent", sent));
    }
}
