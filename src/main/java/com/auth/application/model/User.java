package com.auth.application.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
    @Email(message = "Invalid email format")
    private String username;

    @XmlElement(name = "Password")
    private String password;

    @XmlElement(name = "Role")
    @NotNull(message = "Role cannot be null")
    private String role;

    @XmlElement(name = "FirstName")
    private String firstName;

    @XmlElement(name = "LastName")
    private String lastName;

    @NotNull(message = "MSISDN cannot be null")
    @XmlElement(name = "MSISDN")
    private String msisdn;

    @XmlElement(name = "Status")
    private boolean status;



}
