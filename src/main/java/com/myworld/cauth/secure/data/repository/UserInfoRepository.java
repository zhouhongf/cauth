package com.myworld.cauth.secure.data.repository;


import com.myworld.cauth.secure.data.entity.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    UserInfo findByUsername(String username);
    UserInfo findByIdnumber(String idnumber);
    UserInfo findByIdDetail(String idDetail);
    Page<UserInfo> findByPlayerType(String playerType, Pageable pageable);
    void deleteByIdDetail(String idDetail);
}
