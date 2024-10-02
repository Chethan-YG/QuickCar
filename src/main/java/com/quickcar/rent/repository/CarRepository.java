package com.quickcar.rent.repository;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.quickcar.rent.entity.Car;

public interface CarRepository extends JpaRepository<Car, Long> {
	List<Car> findAll(Specification<Car> spec);
}