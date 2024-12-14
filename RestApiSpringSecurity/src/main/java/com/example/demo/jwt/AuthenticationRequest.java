package com.example.demo.jwt;

public class AuthenticationRequest {
	
	private String name;
    private String password;
	public String getUsername() {
		return name;
	}
	public void setUsername(String username) {
		this.name = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    
    

}
