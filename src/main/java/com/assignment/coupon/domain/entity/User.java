package com.assignment.coupon.domain.entity;

import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.Collection;

@Entity
@Table(name = "user_info")
public class User implements UserDetails {

    public User(){

    }

    @Id
    @Column(name="user_id")
    private String userName;

    @Column(name="user_password")
    private String password;

    @Column(name = "user_create_datetime")
    private Instant createDtNo;

    @Column(name = "user_update_datetime")
    private Instant updateDtNo;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreateDtNo() {
        return createDtNo;
    }

    public void setCreateDtNo(Instant createDtNo) {
        this.createDtNo = createDtNo;
    }

    public Instant getUpdateDtNo() {
        return updateDtNo;
    }

    public void setUpdateDtNo(Instant updateDtNo) {
        this.updateDtNo = updateDtNo;
    }

    public static class Builder {

        private final Instant createDtNo = Instant.now();

        private String userName = "";
        private String password = "";
        private Instant updateDtNo = null;

        public Builder userName(String userName){
            this.userName = userName;
            return this;
        }

        public Builder password(String password){
            this.password = password;
            return this;
        }

        public Builder updateDtNo(Instant updateDtNo){
            this.updateDtNo = updateDtNo;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }

    public User(Builder builder){
        this.userName = builder.userName;
        this.password = builder.password;
        this.createDtNo = builder.createDtNo;
        this.updateDtNo = builder.updateDtNo;
    }

    public static Builder Builder(){
        return new Builder();
    }
}
