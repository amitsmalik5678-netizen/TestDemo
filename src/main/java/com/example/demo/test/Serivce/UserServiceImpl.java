package com.example.demo.test.Serivce;

import com.example.demo.test.Model.User;
import com.example.demo.test.Respository.UserRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public User create(User user) {
		log.debug("Creating user: name={}, city={}, companyName={}", user.getName(), user.getCity(), user.getCompanyName());
		user.setId(null);
		User saved = userRepository.save(user);
		log.debug("Created user with id={}", saved.getId());
		return saved;
	}

	@Override
	public List<User> findAll() {
		List<User> list = userRepository.findAll();
		log.debug("Fetched {} users", list.size());
		return list;
	}

	@Override
	public Optional<User> findById(Long id) {
		log.debug("Fetching user id={}", id);
		return userRepository.findById(id);
	}
}


