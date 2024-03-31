package com.pfa.BackendPFA.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TouristReq {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
}
