package com.auth.application.model;


import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@XmlRootElement
public class Password {

    @XmlElement
    private String value;

    @XmlElement
    private String format;
}
