package com.example.demo.test.Respository;

import com.example.demo.test.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}



