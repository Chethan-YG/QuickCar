package com.quickcar.rent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quickcar.rent.entity.BookCar;

@Repository
public interface BookCarRepository extends JpaRepository<BookCar, Long> {
	 List<BookCar> findByUserId(Long id);
	 List<BookCar> findByCarId(Long carId);
}
