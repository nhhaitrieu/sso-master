package com.auth.application.model;

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
@XmlRootElement(name = "ResetPasswordRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResetPasswordRequest {

    @XmlElement(name = "Token")
    private String token;

    @XmlElement(name = "NewPassword")
    private String newPassword;
}
