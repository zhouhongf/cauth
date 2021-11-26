package com.myworld.cauth.secure.service;

import com.myworld.cauth.secure.data.entity.SysRole;
import com.myworld.cauth.secure.data.repository.SysRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class SysRoleService {

    @Autowired
    private SysRoleRepository sysRoleRepository;

    public Set<SysRole> updateRole(String theRoleName){
        Set<SysRole> sysRoles = new HashSet<>();
        //查找数据库中是否有这个role对象，没有有新建一个
        SysRole sysRole = sysRoleRepository.findByName(theRoleName);
        if (sysRole == null) {
            sysRole = new SysRole();
            sysRole.setName(theRoleName);
            sysRoleRepository.save(sysRole);
        }
        //添加至set集合中，并返回set集合
        sysRoles.add(sysRole);
        return sysRoles;
    }
}
