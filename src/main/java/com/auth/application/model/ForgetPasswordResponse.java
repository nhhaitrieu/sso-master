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
@XmlRootElement(name = "ForgetPasswordResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ForgetPasswordResponse {

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "Message")
    private String message;
}
