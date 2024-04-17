package com.pfa.BackendPFA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="monument")
public class Monument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @OneToMany(mappedBy = "departureMonument")
    private List<Circuit> circuitDeparture;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    @ManyToMany(mappedBy = "monuments")
    private List<Circuit> circuits;
}
