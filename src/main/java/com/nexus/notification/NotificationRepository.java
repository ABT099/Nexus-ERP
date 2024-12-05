package com.nexus.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("""
        select n from Notification n
        join fetch n.user u
        where u.id = :userId
    """)
    List<Notification> findAllByUserId(long userId);
}
