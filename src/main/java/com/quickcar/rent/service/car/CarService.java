package com.quickcar.rent.service.car;

import com.quickcar.rent.dto.CarDto;
import com.quickcar.rent.dto.SearchCarDTO;
import com.quickcar.rent.entity.Car;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public interface CarService {
	public List<Car> getAllCars();
	public Car saveCar(Car car, MultipartFile file, MultipartFile serviceFile) throws IOException;
	public Car deleteCar(Long Id)throws Exception;
	public Optional<Car> getCarById(Long id);	
	public List<CarDto> searchCars(SearchCarDTO searchCarDTO);
	public Car updateCar(Long id, Car car, MultipartFile file, MultipartFile serviceFile) throws IOException;
	public String uploadCarImage(MultipartFile file);
}
