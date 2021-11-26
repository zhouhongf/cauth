package com.myworld.cauth.secure.data.repository;


import com.myworld.cauth.secure.data.entity.SysAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Repository
public interface SysAdminRepository extends JpaRepository<SysAdmin, Long> {

    SysAdmin findByUsername(String username);
    SysAdmin findByIdDetail(String idDetail);
    SysAdmin findByWid(String wid);
    void deleteByIdDetail(String idDetail);
}
