package com.quickcar.rent.service.auth;

import com.quickcar.rent.dto.SignUpRequest;
import com.quickcar.rent.dto.Userdto;

public interface AuthService {
	
	Userdto createCustomer(SignUpRequest signUpRequest);
	boolean hasCustomerWithEmail(String email);

}
