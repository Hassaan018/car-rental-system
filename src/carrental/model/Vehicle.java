package carrental.model;

import java.io.Serializable;

/* Abstract base class for all vehicle types.
   Implements Rentable interface */

public abstract class Vehicle implements Serializable, Rentable {

    private static final long serialVersionUID = 1L;

    private String vehicleId;
    private String brand;
    private String model;
    private double basePricePerDay;
    private boolean available;
    private int year;
    private String color;

    public Vehicle(String vehicleId, String brand, String model, int year, String color, double basePricePerDay) {
        this.vehicleId = vehicleId;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.basePricePerDay = basePricePerDay;
        this.available = true;
    }

    // Abstract method — each subclass computes rental rate differently (Polymorphism)
    @Override
    public abstract double calculateRentalCost(int days);

    public abstract String getVehicleType();

    // Getters and Setters (Encapsulation)
    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getBasePricePerDay() { return basePricePerDay; }
    public void setBasePricePerDay(double basePricePerDay) { this.basePricePerDay = basePricePerDay; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s %s (%d) | Color: %s | Rate: PKR %.0f/day | %s",
                vehicleId, getVehicleType(), brand, model, year, color,
                basePricePerDay, available ? "Available" : "Rented");
    }
}
