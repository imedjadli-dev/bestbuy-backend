package com.bestbuy.user.dto;

import com.bestbuy.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email , please verify again")
    private String email;
    @NotBlank(message = "Fullname is required")
    private String fullname;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String passwordHash;
    @NotNull(message = "Role is required")
    private Role role;
}
