package com.myworld.cauth.secure.security;


import com.myworld.cauth.secure.data.entity.SysAdmin;
import com.myworld.cauth.secure.data.entity.SysUser;
import com.myworld.cauth.secure.data.repository.SysAdminRepository;
import com.myworld.cauth.secure.data.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("myUserDetailService")
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private SysAdminRepository sysAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsername(username);
        if (user == null) {
            SysAdmin admin = sysAdminRepository.findByUsername(username);
            if( admin != null){
                return admin;
            }else {
                throw new UsernameNotFoundException("UserName " + username + " not found");
            }
        }
        return user;
    }

}
