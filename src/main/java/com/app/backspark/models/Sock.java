package com.app.backspark.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sock")
public class Sock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String color;
    private double cottonPercentage;
    private int quantity;

    public Sock(String color, double cottonPercentage, int quantity) {
        this.color = color;
        this.cottonPercentage = cottonPercentage;
        this.quantity = quantity;
    }
}
