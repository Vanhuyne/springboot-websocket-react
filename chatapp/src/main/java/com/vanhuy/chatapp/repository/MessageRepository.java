package com.vanhuy.chatapp.repository;

import com.vanhuy.chatapp.model.Conversation;
import com.vanhuy.chatapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message , Long> {
    List<Conversation> findByConversationOrderByTimestampAsc(Conversation conversation);
}
