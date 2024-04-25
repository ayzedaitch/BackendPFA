package com.pfa.BackendPFA.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CircuitResponse {

    private int circuitId;
    private String cityName;
    private Date departureTime;
    private String departureMonumentName;
}
