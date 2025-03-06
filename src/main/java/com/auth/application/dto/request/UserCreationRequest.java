package com.auth.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationRequest {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}
