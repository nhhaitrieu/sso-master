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
@XmlRootElement(name = "ForgetPasswordRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class ForgetPasswordRequest {

    @XmlElement(name = "Email")
    private String email;
}
