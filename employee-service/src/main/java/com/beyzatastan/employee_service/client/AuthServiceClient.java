package com.beyzatastan.employee_service.client;

import com.beyzatastan.employee_service.dto.auth.CreateUserRequest;
import com.beyzatastan.employee_service.dto.auth.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", path = "/api/auth")
public interface AuthServiceClient {

    @PostMapping("/create-user")
    UserResponse createUser(@RequestBody CreateUserRequest request);
}