package ru.adel.SocialMediaApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.adel.SocialMediaApp.models.Message;
import ru.adel.SocialMediaApp.models.User;

import java.util.List;
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndRecipientOrderByCreatedAtDesc(User sender, User recipient);
}