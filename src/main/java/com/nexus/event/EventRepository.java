package com.nexus.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("""
        SELECT e FROM Event e
        join e.admins a
        where a.id = :adminId
    """)
    List<Event> findAllByAdminId(long adminId);
}
