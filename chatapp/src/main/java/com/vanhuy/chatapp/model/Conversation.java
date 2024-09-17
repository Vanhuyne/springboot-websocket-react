package com.vanhuy.chatapp.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "conversation")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long conversationId;

    private String subject;

    private boolean isGroup;


    @ManyToMany
    @JoinTable(
            name = "Conversation_User",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    //    CREATE TABLE Conversation (
//            conversation_id INT AUTO_INCREMENT PRIMARY KEY,
//            subject VARCHAR(50),
//    is_group BOOLEAN DEFAULT FALSE
//);
}
