package com.pfa.BackendPFA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.james.mime4j.dom.datetime.DateTime;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="circuit")
public class Circuit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "departure_time")
    @Temporal(TemporalType.DATE)
    // For example, if you have a Date field named departureTime in your Circuit entity, and you annotate it with @Temporal(TemporalType.DATE), it would be mapped to a DATE column in the database table, storing only the date without the time part.
    private Date departureTime;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @ManyToOne
    @JoinColumn(name = "departure_monument_id")
    private Monument departureMonument;
    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    @ManyToOne
    @JoinColumn(name = "tourist_id")
    private Tourist tourist;
    @ManyToMany
    @JoinTable(
            name = "circuit_monument",
            joinColumns = @JoinColumn(name = "circuit_id"),
            inverseJoinColumns = @JoinColumn(name = "monument_id")
    )
    private List<Monument> monuments;
}
