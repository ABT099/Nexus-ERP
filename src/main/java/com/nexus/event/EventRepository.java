package com.nexus.event;

import com.nexus.common.ArchivableRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends ArchivableRepository<Event, Long> {
    @Query("""
        SELECT e FROM Event e
        join fetch e.admins a
        where a.id = :adminId
    """)
    List<Event> findAllByAdminId(long adminId);

    @Query("""
        SELECT e FROM Event e
        join fetch e.admins a
        where a.id = :adminId and e.archived = false
    """)
    List<Event> findAllNonArchivedByAdminId(long adminId);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Event e
    SET e.urgent = true
    WHERE e.id IN :ids
    """)
    void updateUrgentToTrue(@Param("ids") Iterable<Long> ids);

}
