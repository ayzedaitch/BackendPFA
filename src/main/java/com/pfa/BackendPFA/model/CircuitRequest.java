package com.pfa.BackendPFA.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CircuitRequest {
    private String cityName;
    private List<String> monuments;
    private String depMonument;
    private Date depDate;
    private String touristEmail;
}
