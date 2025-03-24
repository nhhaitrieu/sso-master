package com.auth.application.model;


import com.auth.application.service.ValidMsisdn;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@XmlRootElement(name = "User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @XmlElement(name = "UserID")
    @NotNull(message = "Username cannot be null")
    private String username;

    @XmlElement(name = "Password")
    private String password;

    @XmlElement(name = "Email")
    @Email(message = "Invalid email format")
    private String email;

    @XmlElement(name = "Role")
    @NotNull(message = "Role cannot be null")
    private String role;

    @XmlElement(name = "FirstName")
    private String firstName;

    @XmlElement(name = "LastName")
    private String lastName;

    @NotNull(message = "MSISDN cannot be null")
    @XmlElement(name = "MSISDN")
    @Pattern( regexp = "(\\+84|0)[0-9]{9}|\\+[1-9][0-9]{1,3}[0-9]{6,12}",
            message = "MSISDN must be valid: '+84' or '0' followed by 9 digits, or '+' with country code and subscriber number")
    @ValidMsisdn // <-- Dùng validator custom tại đây!
    private String msisdn;

    @XmlElement(name = "Status")
    private boolean status;



}
