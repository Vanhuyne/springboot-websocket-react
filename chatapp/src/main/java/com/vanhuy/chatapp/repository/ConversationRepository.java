package com.vanhuy.chatapp.repository;

import com.vanhuy.chatapp.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
//    Conversation findBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
