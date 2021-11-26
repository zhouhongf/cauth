package com.myworld.cauth.secure.data.repository;

import com.myworld.cauth.secure.data.entity.UserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, String> {
}
