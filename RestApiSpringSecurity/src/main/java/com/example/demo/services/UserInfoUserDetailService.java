package com.example.demo.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.example.demo.config.UserInfoUserDetails;
import com.example.demo.entity.UsersInfo;
import com.example.demo.repo.UserInfoRepo;

@Component
@Service
public class UserInfoUserDetailService implements UserDetailsService {
	
	@Autowired
	private UserInfoRepo userInfoRepo;

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		Optional<UsersInfo> userInfo = userInfoRepo.findByName(name);
		return userInfo.map(UserInfoUserDetails::new)
		.orElseThrow(()-> new UsernameNotFoundException("User Not Found" + name));
		
	}

}
