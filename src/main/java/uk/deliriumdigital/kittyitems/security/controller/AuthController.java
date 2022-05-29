package uk.deliriumdigital.kittyitems.security.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.extern.slf4j.Slf4j;
import uk.deliriumdigital.kittyitems.flownftservice.KittyItemsFlowService;
import uk.deliriumdigital.kittyitems.security.JwtUtils;
import uk.deliriumdigital.kittyitems.security.UserDetailsImpl;
import uk.deliriumdigital.kittyitems.security.login.LoginRequest;
import uk.deliriumdigital.kittyitems.security.login.LoginResponse;
import uk.deliriumdigital.kittyitems.security.model.User;
import uk.deliriumdigital.kittyitems.security.service.AbstractUserService;

@Slf4j
@RestController
@RequestMapping("/access")
public class AuthController {
	
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	AbstractUserService userService;

	@Autowired
	private KittyItemsFlowService kittyService;

	@Autowired
	JwtUtils jwtUtils;

	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		
		authentication.getAuthorities();
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		var addr = checkFormat(loginRequest.getFlowAddress());
		kittyService.setUser(addr, loginRequest.getPrivateKey());

		return ResponseEntity.ok(
				new LoginResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles, userDetails.getExpirationTime()));
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody User u) {

		var addr = checkFormat(u.getUserFlowAddress());

		boolean b = kittyService.setUser(addr, u.getUserPrivateKey());

		if(!b) {
			return new ResponseEntity<String>("User not set successfully", HttpStatus.FORBIDDEN);
		}

		log.info("username {}", u.getUsername());
		log.info("userFlowAddress {}", u.getUserFlowAddress());
		userService.saveUser(u);
		return ResponseEntity.ok("User " + u.getUsername() + " registered with success : Address " + u.getUserFlowAddress());
	}

	private String checkFormat(String address) {
		if(address.startsWith("0x")) {
			address = address.substring(2);
		}

		return address;
	}

}
