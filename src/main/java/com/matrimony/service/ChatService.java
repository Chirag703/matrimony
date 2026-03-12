package com.matrimony.service;

import com.matrimony.dto.ChatDto;
import com.matrimony.dto.MessageDto;
import com.matrimony.entity.Chat;
import com.matrimony.entity.Interest;
import com.matrimony.entity.Message;
import com.matrimony.entity.User;
import com.matrimony.repository.ChatRepository;
import com.matrimony.repository.InterestRepository;
import com.matrimony.repository.MessageRepository;
import com.matrimony.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final int FREE_USER_CHAT_LIMIT = 1;

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatDto startChat(Long userId, Long otherUserId) {
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherUserId);

        boolean interestAccepted = interestRepository
                .findByFromUserIdAndToUserId(userId, otherUserId)
                .map(i -> i.getStatus() == Interest.Status.ACCEPTED)
                .orElseGet(() -> interestRepository
                        .findByFromUserIdAndToUserId(otherUserId, userId)
                        .map(i -> i.getStatus() == Interest.Status.ACCEPTED)
                        .orElse(false));

        if (!interestAccepted) {
            throw new IllegalStateException("Interest must be accepted before chatting");
        }

        if (!user.getPremium()) {
            long existingChats = chatRepository.countByUserId(userId);
            if (existingChats >= FREE_USER_CHAT_LIMIT) {
                throw new IllegalStateException("Free users can only have 1 active chat. Upgrade to premium for unlimited chats.");
            }
        }

        Chat chat = chatRepository.findByUsers(userId, otherUserId)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.setUser1(user);
                    newChat.setUser2(otherUser);
                    return chatRepository.save(newChat);
                });

        return toChatDto(chat, userId);
    }

    @Transactional
    public MessageDto sendMessage(Long chatId, Long senderId, String messageText) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Long user1Id = chat.getUser1().getId();
        Long user2Id = chat.getUser2().getId();

        if (!senderId.equals(user1Id) && !senderId.equals(user2Id)) {
            throw new SecurityException("Not authorized to send messages in this chat");
        }

        User sender = getUserOrThrow(senderId);

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setMessage(messageText);
        message.setReadStatus(false);
        message = messageRepository.save(message);

        return toMessageDto(message);
    }

    public List<MessageDto> getMessages(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        if (!userId.equals(chat.getUser1().getId()) && !userId.equals(chat.getUser2().getId())) {
            throw new SecurityException("Not authorized to view this chat");
        }

        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId)
                .stream().map(this::toMessageDto).collect(Collectors.toList());
    }

    public List<ChatDto> getUserChats(Long userId) {
        return chatRepository.findByUserId(userId)
                .stream()
                .map(chat -> toChatDto(chat, userId))
                .collect(Collectors.toList());
    }

    private ChatDto toChatDto(Chat chat, Long currentUserId) {
        ChatDto dto = new ChatDto();
        dto.setId(chat.getId());

        User otherUser = chat.getUser1().getId().equals(currentUserId)
                ? chat.getUser2() : chat.getUser1();

        dto.setOtherUserId(otherUser.getId());
        dto.setOtherUserName(otherUser.getName());

        if (otherUser.getProfile() != null) {
            dto.setOtherUserPhoto(otherUser.getProfile().getPhotoUrl());
        }

        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());
        if (!messages.isEmpty()) {
            Message lastMessage = messages.get(messages.size() - 1);
            dto.setLastMessage(lastMessage.getMessage());
            dto.setLastMessageTime(lastMessage.getCreatedAt());
        }

        dto.setUnreadCount(messageRepository
                .countByChatIdAndReadStatusFalseAndSenderIdNot(chat.getId(), currentUserId));

        return dto;
    }

    private MessageDto toMessageDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setChatId(message.getChat().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getName());
        dto.setMessage(message.getMessage());
        dto.setReadStatus(message.getReadStatus());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
}
