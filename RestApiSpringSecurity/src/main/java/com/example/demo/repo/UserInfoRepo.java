package com.example.demo.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.UsersInfo;

public interface UserInfoRepo extends JpaRepository<UsersInfo, Integer> {

	Optional<UsersInfo> findByName(String username);

	

}
