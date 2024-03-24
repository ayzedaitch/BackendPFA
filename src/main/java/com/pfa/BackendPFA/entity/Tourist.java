package com.pfa.BackendPFA.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tourist")
public class Tourist {

    @Id
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

}
