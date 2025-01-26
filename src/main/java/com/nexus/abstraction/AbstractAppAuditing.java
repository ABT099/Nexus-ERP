package com.nexus.abstraction;

import com.nexus.user.User;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAppAuditing<ID extends Serializable> extends AbstractAuditable<User, ID> {
}
