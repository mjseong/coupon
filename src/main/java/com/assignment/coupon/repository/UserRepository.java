package com.assignment.coupon.repository;

import com.assignment.coupon.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
}
