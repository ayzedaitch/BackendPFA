package com.pfa.BackendPFA.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="monument")
public class Monument {

    @Id
    private int id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private int cityId;

}
