package com.example.authapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    ACTIVE,ACHIEVED,TRASH;

    @JsonCreator
    public static Status fromValue(String value){
        return Status.valueOf(value.toUpperCase());
    }
}
