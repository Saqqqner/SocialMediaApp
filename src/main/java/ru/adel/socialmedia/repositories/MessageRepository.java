package ru.adel.socialmedia.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.adel.socialmedia.models.Message;
import ru.adel.socialmedia.models.User;

import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndRecipientOrderByCreatedAtDesc(User sender, User recipient);
}