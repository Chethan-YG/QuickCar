package com.quickcar.rent.service.car;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.quickcar.rent.dto.CarDto;
import com.quickcar.rent.dto.SearchCarDTO;
import com.quickcar.rent.entity.Car;
import com.quickcar.rent.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import com.quickcar.rent.exception.ResourceNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

	private final CarRepository carRepository;
	private final Cloudinary cloudinary;

	@Override
	public List<Car> getAllCars() {
		return carRepository.findAll();
	}

	@Override
	public Car saveCar(Car car, MultipartFile file, MultipartFile serviceFile) throws IOException {
		if (file != null && !file.isEmpty()) {
			Map<String, Object> uploadImageResult = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.emptyMap());
			car.setImageUrl(uploadImageResult.get("url").toString());
		}
		if (serviceFile != null && !serviceFile.isEmpty()) {
			Map<String, Object> uploadServiceResult = cloudinary.uploader().upload(serviceFile.getBytes(),
					ObjectUtils.emptyMap());
			car.setInvoiceUrl(uploadServiceResult.get("url").toString());
		}
		return carRepository.save(car);
	}

	@Override
	public Car updateCar(Long id, Car car, MultipartFile file, MultipartFile serviceFile) throws IOException {
		Car existingCar = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car not found"));

		existingCar.setBrand(car.getBrand());
		existingCar.setName(car.getName());
		existingCar.setType(car.getType());
		existingCar.setTransmission(car.getTransmission());
		existingCar.setColor(car.getColor());
		existingCar.setYear(car.getYear());
		existingCar.setPrice(car.getPrice());
		existingCar.setOdometer(car.getOdometer());
		existingCar.setFuelType(car.getFuelType());

		if (file != null && !file.isEmpty()) {
			Map<String, Object> uploadImageResult = cloudinary.uploader().upload(file.getBytes(),
					ObjectUtils.emptyMap());
			existingCar.setImageUrl(uploadImageResult.get("url").toString());
		}

		if (serviceFile != null && !serviceFile.isEmpty()) {
			Map<String, Object> uploadServiceResult = cloudinary.uploader().upload(serviceFile.getBytes(),
					ObjectUtils.emptyMap());
			existingCar.setInvoiceUrl(uploadServiceResult.get("url").toString());
		}

		return carRepository.save(existingCar);
	}

	@Override
	public Car deleteCar(Long id) {
        Car car = carRepository.findById(id).orElse(null);
        if (car != null) {
            carRepository.delete(car);
            return car;
        }
        return null;
    }

	@Override
	public Optional<Car> getCarById(Long id) {
		return carRepository.findById(id);
	}

	@Override
	public List<CarDto> searchCars(SearchCarDTO searchCarDTO) {
		Specification<Car> spec = Specification.where(CarSpecification.hasBrand(searchCarDTO.getBrand()))
				.and(CarSpecification.hasType(searchCarDTO.getType()))
				.and(CarSpecification.hasTransmission(searchCarDTO.getTransmission()))
				.and(CarSpecification.hasColor(searchCarDTO.getColor()));
		List<Car> cars = carRepository.findAll(spec);
		return cars.stream().map(Car::getCarDTO).toList();
	}

	@Override
	public String uploadCarImage(MultipartFile file) {
	    String url = "";
	    if (file != null && !file.isEmpty()) {
	        try {
	            Map<String, Object> uploadImageResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
	            url = uploadImageResult.get("url").toString();
	        } catch (IOException e) {
	        	System.out.println(e);
	        }
	    }
	    return url;
	}

}
