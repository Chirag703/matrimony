package com.matrimony.repository;

import com.matrimony.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatIdOrderByCreatedAtAsc(Long chatId);

    long countByChatIdAndReadStatusFalseAndSenderIdNot(Long chatId, Long senderId);
}
