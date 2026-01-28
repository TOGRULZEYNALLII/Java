import Model.Car;
import Model.User;
import repository.InMemoryRepository;
import contract.Repository;
import service.CarService;
import Model.Review;
import java.util.*;
import enums.FuelType;
import enums.Transmission;
import enums.SortField;
import enums.UserRole;
public class main {

    public static void main(String[] args) {
        Repository<Car> carRepository = new InMemoryRepository<>();
        Repository<Review> reviewRepository = new InMemoryRepository<>();
        CarService carService = new CarService(carRepository, reviewRepository);
        seedData(carService);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        handleListCars(scanner, carService);
                        break;
                    case 2:
                        handleFilterCars(carService);
                        break;
                    case 3:
                        handleAddReview(scanner, carService);
                        break;
                    case 4:
                        handleShowTopCars(carService);
                        break;
                    case 5:
                        handleShowAverageRatings(carService);
                        break;
                    case 6:
                        running = false;
                        System.out.println("Exiting AutoHub. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }


private static void seedData(CarService carService) {
    Car car1 = new Car(UUID.randomUUID(), "Tesla", "Model 3", 2023, 1500000.0, FuelType.ELECTRIC, Transmission.AUTOMATIC);
    Car car2 = new Car(UUID.randomUUID(), "BMW", "i4", 2024, 3200000.0, FuelType.ELECTRIC, Transmission.AUTOMATIC);
    Car car3 = new Car(UUID.randomUUID(), "Audi", "A4", 2021, 1800000.0, FuelType.DIESEL, Transmission.AUTOMATIC);
    Car car4 = new Car(UUID.randomUUID(), "Toyota", "Corolla", 2022, 1200000.0, FuelType.HYBRID, Transmission.SEMI_AUTOMATIC);

    carService.addCar(car1);
    carService.addCar(car2);
    carService.addCar(car3);
    carService.addCar(car4);

    User user1 = new User("Ali Yılmaz", "ali@mail.com", UserRole.CUSTOMER);
    User user2 = new User("Veli Can", "veli@mail.com", UserRole.CUSTOMER);
    User adminUser = new User("Admin", "admin@autohub.com", UserRole.ADMIN);

    carService.addReview(new Review(UUID.randomUUID(), car1.getId(), user1, 5, "Harika bir elektrikli!", "2024-01-10"));
    carService.addReview(new Review(UUID.randomUUID(), car1.getId(), user2, 4, "Menzili biraz az.", "2024-01-11"));
    carService.addReview(new Review(UUID.randomUUID(), car2.getId(), adminUser, 5, "Alman kalitesi başka.", "2024-01-12"));
    System.out.println(">>> Sistem verileri (Car & Review) başarıyla yüklendi!");
}
    private static void handleListCars(Scanner scanner, CarService carService) {
        System.out.println("\n--- Sıralama Seçin ---");
        System.out.println("1. MARKA | 2. MODEL | 3. YIL | 4. FİYAT | 5. SIRALAMASIZ");

        try {
            String input = scanner.nextLine();
            List<Car> cars;

            if (input.equals("5")) {
                cars = carService.findAll();
            } else {
                SortField field = SortField.values()[Integer.parseInt(input) - 1];
                cars = carService.findAllSorted(field);
            }

            System.out.println("\n--- Sonuçlar ---");
            if (cars.isEmpty()) {
                System.out.println("Listelenecek araç bulunamadı.");
            } else {
                cars.forEach(car -> System.out.println(car.format()));
            }

        } catch (Exception e) {
            System.out.println("Geçersiz seçim yapıldı, liste varsayılan haliyle gösteriliyor.");
            carService.findAll().forEach(car -> System.out.println(car.format()));
        }
    }
    private static void handleFilterCars(CarService carService) {
        System.out.println("\n--- Elektrikli & 2022+ Modeller ---");
        carService.getModernElectricCars().forEach(car -> System.out.println(car.format()));
    }

    private static void handleAddReview(Scanner scanner, CarService carService) {
        System.out.println("\nYorum yapmak istediğiniz arabanın Markasını yazın:");
        String brand = scanner.nextLine();

        Optional<Car> car = carService.findAll().stream()
                .filter(c -> c.getBrand().equalsIgnoreCase(brand))
                .findFirst();

        if (car.isPresent()) {
            System.out.print("İsminiz: "); String name = scanner.nextLine();
            System.out.print("E-posta: "); String email = scanner.nextLine();
            System.out.print("Puan (1-5): "); int rating = Integer.parseInt(scanner.nextLine());
            System.out.print("Yorumunuz: "); String comment = scanner.nextLine();

            // ESKİ: Review review = new Review(..., name, ...); -> HATA VERİR
            // YENİ: Önce User nesnesini yaratıyoruz
            User user = new User(name, email, UserRole.CUSTOMER);

            Review review = new Review(UUID.randomUUID(), car.get().getId(), user, rating, comment, "2024-01-01");

            carService.addReview(review);
            System.out.println("Yorum başarıyla eklendi!");
        } else {
            System.out.println("Araba bulunamadı.");
        }
    }
        private static void handleShowTopCars (CarService carService) {

            System.out.println("\n--- En İyi 3 Araba ---");
            carService.getTop3RatedCars().forEach(car -> {
                double avgRating = carService.getAverageRating(car.getId());
                System.out.println(car.format() + String.format(" | Ortalama Puan: %.2f", avgRating));
            });
        }
    private static void handleShowAverageRatings(CarService carService) {
        System.out.println("\n--- Model Başına Ortalama Puan ---");
        Map<String, List<Car>> carsByModel = new HashMap<>();

        for (Car car : carService.findAll()) {
            carsByModel.computeIfAbsent(car.getModel(), k -> new ArrayList<>()).add(car);
        }

        for (String model : carsByModel.keySet()) {
            List<Car> cars = carsByModel.get(model);
            double totalRating = 0.0;
            int count = 0;

            for (Car car : cars) {
                double avgRating = carService.getAverageRating(car.getId());
                if (avgRating > 0) {
                    totalRating += avgRating;
                    count++;
                }
            }

            double modelAvgRating = (count > 0) ? (totalRating / count) : 0.0;
            System.out.println(String.format("Model: %s | Ortalama Puan: %.2f", model, modelAvgRating));
        }
    }
    private static void printMenu() {
        System.out.println("\n--- AutoHub Menu ---");
        System.out.println("1. List cars");
        System.out.println("2. Filter cars (Electric & Year >= 2022)");
        System.out.println("3. Add a review for a car");
        System.out.println("4. Show top 3 cars by rating");
        System.out.println("5. Show average rating per model");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

}