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
import java.util.HashSet;
import java.util.Set;


@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "sysadmin")
public class SysAdmin implements Serializable, UserDetails {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * MYADMN1534219317400
     */
    private String idDetail;
    /**
     * 用户自己取的独一无二的id
     */
    private String wid;

    /**
     * 创建时间
     */
    @JsonIgnore
    @CreationTimestamp
    @Column(name = "create_time",columnDefinition="DATETIME COMMENT '创建时间'")
    private Timestamp createTime;
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "update_time",columnDefinition="DATETIME COMMENT '最后更新时间'")
    private Timestamp updateTime;
    /**
     * 上次登录时间
     */
    @JsonIgnore
    @Column(name = "last_login_time",columnDefinition="DATETIME COMMENT '上次登录时间'")
    private Timestamp lastLoginTime;
    /**
     * 上次登录IP
     */
    private String lastLoginIp;

    /**
     * 用户角色
     */
    @JsonIgnore
    @ManyToMany(cascade = {}, fetch = FetchType.EAGER)
    @JoinTable(name = "sysadmin_sysrole",
            joinColumns = {@JoinColumn(name = "admin_id")},
            inverseJoinColumns = {@JoinColumn(name = "adminrole_id")})
    private Set<SysRole> roles;

    /**
     * 用户账号
     */
    private String username;
    /**
     * 密码
     */
    @JsonIgnore
    private String password;
    /**
     * 管理员权限，分为：
     * 借款人审核员（AdminBorrower），
     * 信贷经理审核员（AdminLender），
     * 业务审核员(AdminProject)，
     * 积分审核员(AdminCredit)，
     * 文章管理员(AdminWritings),
     * 超级管理员(AdminSuper),
     */
    private String adminPower;

    @JsonIgnore
    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> auths = new HashSet<>();
        Set<SysRole> roles = this.getRoles();
        if(roles != null) {
            for (SysRole role : roles) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
                auths.add(authority);
            }
        }
        return auths;
    }

    public SysAdmin() {
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

    public Set<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<SysRole> roles) {
        this.roles = roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminPower() {
        return adminPower;
    }

    public void setAdminPower(String adminPower) {
        this.adminPower = adminPower;
    }
}

