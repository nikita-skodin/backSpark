package com.app.backspark.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SockView {
    private long id;

    @NotBlank(message = "Color is required")
    private String color;

    @Min(value = 0, message = "Cotton percentage must be at least 0")
    @Max(value = 100, message = "Cotton percentage must not exceed 100")
    private double cottonPercentage;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100000, message = "Cotton percentage must be less then 100001")
    private int quantity;

    public SockView(String color, double cottonPercentage, int quantity) {
        this.color = color;
        this.cottonPercentage = cottonPercentage;
        this.quantity = quantity;
    }
}