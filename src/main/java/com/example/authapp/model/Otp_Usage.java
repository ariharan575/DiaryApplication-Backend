package com.example.authapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Otp_Usage {
    REGISTER,
    FORGET_PASSWORD;

    @JsonCreator
    public static Otp_Usage fromValue(String value){
        return Otp_Usage.valueOf(value.toUpperCase());
    }
}
