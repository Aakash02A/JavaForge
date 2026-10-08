package com.javaforge.rolebasedaccess.service;

import com.javaforge.rolebasedaccess.dto.RoleUpdateRequest;
import com.javaforge.rolebasedaccess.dto.UserResponse;
import com.javaforge.rolebasedaccess.exception.ResourceNotFoundException;
import com.javaforge.rolebasedaccess.model.AppUser;
import com.javaforge.rolebasedaccess.repository.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final AppUserRepository userRepository;

    public AdminService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAllByOrderByIdAsc().stream().map(UserResponse::from).toList();
    }

    @Transactional
    public UserResponse updateRole(Long userId, RoleUpdateRequest request) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        user.changeRole(request.role());
        return UserResponse.from(user);
    }
}
