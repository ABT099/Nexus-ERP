package com.nexus.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query("""
        SELECT e FROM Event e
        join e.admins a
        where a.id = :adminId
    """)
    List<Event> findAllByAdminId(long adminId);

    @Modifying
    @Query("""
    UPDATE Event e
    SET e.urgent = true
    WHERE e.id IN :ids
    """)
    void updateUrgentToTrue(@Param("ids") Iterable<Integer> ids);

}
