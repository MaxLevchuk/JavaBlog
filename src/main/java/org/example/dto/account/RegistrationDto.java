package org.example.dto.account;

import lombok.Data;

@Data
public class RegistrationDto {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
}
