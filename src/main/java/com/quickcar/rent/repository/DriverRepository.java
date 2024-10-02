package com.quickcar.rent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quickcar.rent.entity.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

}
