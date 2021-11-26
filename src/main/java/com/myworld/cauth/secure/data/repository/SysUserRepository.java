package com.myworld.cauth.secure.data.repository;


import com.myworld.cauth.secure.data.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    SysUser findByUsername(String username);
    SysUser findByIdDetail(String idDetail);
    SysUser findByWid(String wid);
    void deleteByIdDetail(String idDetail);
}
