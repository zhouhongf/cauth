package com.myworld.cauth.secure.data.repository;


import com.myworld.cauth.secure.data.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    SysRole findByName(String name);

}
