package  com.quickcar.rent.service.car;

import org.springframework.data.jpa.domain.Specification;
import com.quickcar.rent.entity.Car;

public class CarSpecification {

    public static Specification<Car> hasBrand(String brand) {
        return (root, query, criteriaBuilder) -> {
            if (brand == null || brand.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("brand"), brand);
        };
    }

    public static Specification<Car> hasType(String type) {
        return (root, query, criteriaBuilder) -> {
            if (type == null || type.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("type"), type);
        };
    }

    public static Specification<Car> hasTransmission(String transmission) {
        return (root, query, criteriaBuilder) -> {
            if (transmission == null || transmission.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("transmission"), transmission);
        };
    }

    public static Specification<Car> hasColor(String color) {
        return (root, query, criteriaBuilder) -> {
            if (color == null || color.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("color"), color);
        };
    }
}
