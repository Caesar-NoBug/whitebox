package org.caesar.repository;

import org.caesar.model.entity.UserExtra;

public interface UserExtraRepository {
    UserExtra getById(long userId);
    UserExtra getUserPrefer(long userId);
}
