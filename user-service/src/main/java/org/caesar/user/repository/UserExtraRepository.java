package org.caesar.user.repository;

import org.caesar.user.model.entity.UserExtra;

public interface UserExtraRepository {
    UserExtra getById(long userId);
    UserExtra getUserPrefer(long userId);
}
