package Model;
import contract.Identifiable;
import enums.FuelType;
import enums.Transmission;
import contract.Formatter;
import java.util.UUID;
public final class Car implements Identifiable ,Formatter{
    private final UUID id;
    private final String brand;
    private final String model;
    private final int year;
    private final double basePrice;
    private final FuelType fuelType;
    private final Transmission transmission;


    public Car(UUID id, String brand, String model, int year, double basePrice, FuelType fuelType, Transmission transmission) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.basePrice = basePrice;
        this.fuelType = fuelType;
        this.transmission = transmission;
    }
    @Override
    public UUID getId() {
        return id;
    }
    public String getBrand(){ return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getBasePrice() { return basePrice; }
    public FuelType getFuelType() { return fuelType; }
    public Transmission getTransmission() { return transmission; }
    @Override
    public String format() {
        return String.format(
                "ARABA -> ID: %s | Marka: %s | Model: %s | Yıl: %d | Fiyat: %.2f TL | Yakıt: %s | Vites: %s",
                id.toString().substring(0, 8), // ID'nin sadece ilk 8 karakterini alalım (kısa olsun)
                brand,
                model,
                year,
                basePrice,
                fuelType,
                transmission
        );
    }
}