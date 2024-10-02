package com.quickcar.rent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quickcar.rent.entity.HireDriver;

public interface HireDriverRepository extends JpaRepository<HireDriver, Long>{
	List<HireDriver> findByUserId(Long id);
	List<HireDriver> findByDriverId(Long driverId);
}
