package com.example.demo.test.Serivce;

import com.example.demo.test.Model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

	User create(User user);

	List<User> findAll();

	Optional<User> findById(Long id);
}



