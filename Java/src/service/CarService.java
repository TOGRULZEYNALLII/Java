package service;

import Model.Car;
import Model.Review;
import contract.Repository;
import java.util.*;
import java.util.stream.Collectors;
import enums.SortField;
public class CarService {
    private final Repository<Car> carRepository;
    private final Repository<Review> reviewRepository;

    public CarService(Repository<Car> carRepository, Repository<Review> reviewRepository) {
        this.carRepository = carRepository;
        this.reviewRepository = reviewRepository;
    }


    public void addCar(Car car) {
        carRepository.save(car);
    }

    public void addReview(Review review) {
        reviewRepository.save(review);
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public List<Car> getModernElectricCars() {
        return carRepository.findAll().stream()
                .filter(car -> car.getFuelType().name().equals("ELECTRIC"))
                .filter(car -> car.getYear() >= 2022)
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsForCar(UUID carId) {
        return reviewRepository.findAll().stream()
                .filter(review -> review.getCarId().equals(carId))
                .collect(Collectors.toList());
    }

    public double getAverageRating(UUID carId) {
        List<Review> reviews = getReviewsForCar(carId);
        if (reviews.isEmpty()) return 0.0;

        return reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public List<Car> getTop3RatedCars() {
        return carRepository.findAll().stream()
                .sorted((c1, c2) -> Double.compare(getAverageRating(c2.getId()), getAverageRating(c1.getId())))
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Car> findAllSorted(SortField field) {
        Comparator<Car> comparator = switch (field) {
            case BRAND -> Comparator.comparing(Car::getBrand);
            case MODEL -> Comparator.comparing(Car::getModel);
            case YEAR  -> Comparator.comparing(Car::getYear);
            case PRICE -> Comparator.comparing(Car::getBasePrice);
            case RATING -> Comparator.comparing(car -> getAverageRating(car.getId()));
        };

        return carRepository.findAll().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}