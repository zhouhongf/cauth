package com.myworld.cauth.secure.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import javax.persistence.*;
import java.io.Serializable;

import java.sql.Timestamp;
import java.util.*;


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "sysuser")
public class SysUser implements Serializable, UserDetails {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 编号MYUSER+系统时间
     */
    private String idDetail;

    /**
     * 用户自己取的独一无二的id
     */
    private String wid;

    /**
     * 创建人
     */
    private String creator;
    /**
     * 更新人
     */
    private String updater;
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "create_time", columnDefinition = "DATETIME COMMENT '创建时间'")
    private Timestamp createTime;
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time", columnDefinition = "DATETIME COMMENT '最后更新时间'")
    private Timestamp updateTime;
    /**
     * 上次登录时间
     */
    @Column(name = "last_login_time", columnDefinition = "DATETIME COMMENT '上次登录时间'")
    private Timestamp lastLoginTime;
    /**
     * 上次登录IP
     */
    private String lastLoginIp;


    /**
     * 用户账号，即手机号
     */
    private String username;
    /**
     * 原用户名，原手机号
     */
    @JsonIgnore
    private String usernameold;
    /**
     * 密码
     */
    @JsonIgnore
    private String password;
    /**
     * 原密码
     */
    @JsonIgnore
    private String passwordold;


    /**
     * 用户角色borrower, lender, houseManager, houseLooker
     */
    @ManyToMany(cascade = {}, fetch = FetchType.EAGER)
    @JoinTable(name = "sysuser_sysrole",
            joinColumns = {@JoinColumn(name = "sysuser_id")},
            inverseJoinColumns = {@JoinColumn(name = "sysrole_id")})
    private Set<SysRole> roles;

    @JsonIgnore
    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> auths = new HashSet<>();
        Set<SysRole> roles = this.getRoles();
        if (roles != null) {
            for (SysRole role : roles) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
                auths.add(authority);
            }
        }
        return auths;
    }

    public SysUser() {
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdDetail() {
        return idDetail;
    }

    public void setIdDetail(String idDetail) {
        this.idDetail = idDetail;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }


    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernameold() {
        return usernameold;
    }

    public void setUsernameold(String usernameold) {
        this.usernameold = usernameold;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordold() {
        return passwordold;
    }

    public void setPasswordold(String passwordold) {
        this.passwordold = passwordold;
    }

    public Set<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SysRole> roles) {
        this.roles = roles;
    }
}
