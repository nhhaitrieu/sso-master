package com.auth.application.model;



import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@XmlRootElement
public class OTPTokenRequest {

    @XmlElement
    private String userID;
    @XmlElement
    private Password password;

}
