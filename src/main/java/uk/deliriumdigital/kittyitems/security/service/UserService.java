package uk.deliriumdigital.kittyitems.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import uk.deliriumdigital.kittyitems.security.model.User;
import uk.deliriumdigital.kittyitems.security.repository.UserRepository;

@Service
public class UserService implements AbstractUserService {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public User saveUser(User u) {
		String passwordHash = passwordEncoder.encode(u.getPassword());
		String privateKeyHash = passwordEncoder.encode(u.getUserPrivateKey());
		u.setPassword(passwordHash);
		u.setUserPrivateKey(privateKeyHash);
		return userRepo.save(u);
	}

}
