package com.nexus.event;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("""
        SELECT e FROM Event e
        join fetch e.admins a
        where a.id = :adminId
    """)
    List<Event> findAllByAdminId(long adminId);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Event e
    SET e.urgent = true
    WHERE e.id IN :ids
    """)
    void updateUrgentToTrue(@Param("ids") Iterable<Long> ids);

}
