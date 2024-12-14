package com.example.demo.controller;

import com.example.demo.config.JwtUtil;
import com.example.demo.entity.Employee;
import com.example.demo.jwt.AuthenticationRequest;
import com.example.demo.jwt.AuthenticationResponse;
import com.example.demo.repo.UserRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class UserController {

	@Autowired
	private UserRepo UserRepo;
	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            // Authenticate the user using their credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            //throw new Exception("Incorrect username or password", e);
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)  // Return 401 status for incorrect credentials
                    .body(new String("Incorrect username or password"));
            
        }

        // Load the user details
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        // Generate the JWT token
        final String jwt = jwtUtil.generateToken(userDetails);

        // Return the token in the response
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }


	@GetMapping("/getall")
	public List<Employee> getAll() {
		return UserRepo.findAll();

	}

	
	@GetMapping("/get/{id}")
	//@PreAuthorize("hasAuthority('USER')")  //Alternative for RequestMather in config file Method level Authrization
	public ResponseEntity<Employee> getById(@PathVariable Integer id) {

		try {
			Employee emp = UserRepo.findById(id).orElseThrow(() -> new Exception("Employee not found with id: " + id));
			return new ResponseEntity<>(emp, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/post")
	public ResponseEntity<Object> save(@RequestBody Employee emp) {
		// Check if the employee object has an ID
		if (emp.getId() != 0) {
			// If ID is provided, check if it already exists in the database
			Optional<Employee> existingEmployee = UserRepo.findById(emp.getId());
			if (existingEmployee.isPresent()) {
				// If the ID already exists, return a conflict response with a custom error
				// message
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("error", "Employee with ID " + emp.getId() + " already exists.");
				return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
			}
		}

		// Save the employee (either with a provided or auto-generated ID)
		Employee savedEmployee = UserRepo.save(emp);

		// Return the saved employee details
		return ResponseEntity.status(HttpStatus.CREATED).body(savedEmployee);
	}

	@PutMapping("/put")
	public ResponseEntity<Object> update(@RequestBody Employee emp) {
		// Check if the employee object has a valid ID
		if (emp.getId() != 0) {
			// Check if the employee with the given ID exists in the database
			Optional<Employee> existingEmployee = UserRepo.findById(emp.getId());
			if (existingEmployee.isPresent()) {
				// Get the existing employee and update fields
				Employee old = existingEmployee.get();
				old.setName(emp.getName());
				old.setSalary(emp.getSalary());
				old.setAddress(emp.getAddress());

				// Save the updated employee back to the database
				Employee updatedEmployee = UserRepo.save(old);

				// Return the updated employee and status 200 OK
				return ResponseEntity.status(HttpStatus.OK).body(updatedEmployee);
			} else {
				// Return 404 Not Found if employee with the provided ID does not exist
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("error", "Employee with ID " + emp.getId() + " not found.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			}
		}

		// Return a bad request if ID is 0 or not provided
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("error", "Employee ID must be provided for update.");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

}
