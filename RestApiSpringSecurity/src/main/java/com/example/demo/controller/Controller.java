package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.UsersInfo;
import com.example.demo.repo.UserInfoRepo;

@RestController
@RequestMapping("/adduser")
public class Controller {
	
	@Autowired
	private UserInfoRepo userInfoRepo;
	
	@Autowired
    private PasswordEncoder passwordEncoder; 
	
	//@Autowired
    //private BCryptPasswordEncoder passwordEncoder;
	 
	
	@PostMapping("/post")
	public ResponseEntity<Object> save(@RequestBody UsersInfo usersInfo) {
		// Check if the employee object has an ID
		if (usersInfo.getId() != 0) {
			// If ID is provided, check if it already exists in the database
			Optional<UsersInfo> existingEmployee = userInfoRepo.findById(usersInfo.getId());
			if (existingEmployee.isPresent()) {
				// If the ID already exists, return a conflict response with a custom error
				// message
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("error", "user with ID " + usersInfo.getId() + " already exists.");
				return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
			}
			
		}
		String encodpass = passwordEncoder.encode(usersInfo.getPassword());
		usersInfo.setPassword(encodpass);
		// Save the employee (either with a provided or auto-generated ID)
		UsersInfo savedEmployee = userInfoRepo.save(usersInfo);

		// Return the saved employee details
		return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
	}

}
