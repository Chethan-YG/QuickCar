package com.quickcar.rent.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quickcar.rent.dto.SignUpRequest;
import com.quickcar.rent.dto.Userdto;
import com.quickcar.rent.entity.User;
import com.quickcar.rent.enums.UserRole;
import com.quickcar.rent.repository.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
	
    @PostConstruct
    public void createAdminAccount() {
        User adminAccount = userRepository.findByUserRole(UserRole.ADMIN);
        if (adminAccount == null) {
            User newAdminAccount = new User();
            newAdminAccount.setName("ADMIN");
            newAdminAccount.setEmail("admin57@gmail.com");
            newAdminAccount.setPassword(passwordEncoder.encode("admin57"));
            newAdminAccount.setUserRole(UserRole.ADMIN);
            User createdAdmin = userRepository.save(newAdminAccount);
            System.out.println(createdAdmin);
        }
    }

    @Override
    public Userdto createCustomer(SignUpRequest signUpRequest) {
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setUserRole(UserRole.CUSTOMER);
        User createdUser = userRepository.save(user);
        Userdto userdto = new Userdto();
        userdto.setId(createdUser.getId());
        return userdto;
    }

    @Override
    public boolean hasCustomerWithEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
