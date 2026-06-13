package carrental.model;

/* Car vehicle type. Standard sedan/hatchback category.
   Inherits from Vehicle and overrides rental cost calculation (Polymorphism). */

public class Car extends Vehicle {

    private static final long serialVersionUID = 1L;

    private int numDoors;
    private String transmission; // Manual / Automatic

    public Car(String vehicleId, String brand, String model, int year, String color,
               double basePricePerDay, int numDoors, String transmission) {
        super(vehicleId, brand, model, year, color, basePricePerDay);
        this.numDoors = numDoors;
        this.transmission = transmission;
    }

    // Cars use the base rate with no surcharge.

    @Override
    public double calculateRentalCost(int days) {
        return getBasePricePerDay() * days;
    }

    @Override
    public String getVehicleType() {
        return "Car";
    }

    public int getNumDoors() { return numDoors; }
    public void setNumDoors(int numDoors) { this.numDoors = numDoors; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Doors: %d | %s", numDoors, transmission);
    }
}
