package com.pfa.BackendPFA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "content")
    private String content;
    @Column(name = "votes")
    private int votes = 0;
    @Column(name = "created_at")
    private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
    @ManyToOne
    @JoinColumn(name = "circuit_id")
    private Circuit circuit;
    @ManyToOne
    @JoinColumn(name = "tourist_id")
    private Tourist tourist;
}
