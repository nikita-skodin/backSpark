package com.app.backspark.repositories;

import com.app.backspark.models.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SockRepository extends JpaRepository<Sock, Long> {
    Optional<Sock> findByColorAndCottonPercentage(String color, double cottonPercentage);

    Optional<Sock> findByColorAndCottonPercentageAndIdNot(String color, double cottonPercentage, long id);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage BETWEEN :min AND :max")
    long findQuantityByColorAndCottonRange(@Param("color") String color, @Param("min") double min, @Param("max") double max);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color")
    long sumQuantityByColor(String color);
    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage > :cottonPercentage")
    long sumQuantityByColorAndCottonPercentageMoreThan(String color, Double cottonPercentage);
    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage < :cottonPercentage")
    long sumQuantityByColorAndCottonPercentageLessThan(String color, Double cottonPercentage);
    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage = :cottonPercentage")
    long sumQuantityByColorAndCottonPercentageEquals(String color, Double cottonPercentage);
}
