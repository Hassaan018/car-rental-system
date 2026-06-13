package carrental.model;

/* SUV vehicle type. Premium category with a 25% surcharge.
   Overrides rental cost calculation (Polymorphism). */

public class SUV extends Vehicle {

    private static final long serialVersionUID = 1L;

    private boolean is4WD;
    private int seatingCapacity;

    public SUV(String vehicleId, String brand, String model, int year, String color,
               double basePricePerDay, boolean is4WD, int seatingCapacity) {
        super(vehicleId, brand, model, year, color, basePricePerDay);
        this.is4WD = is4WD;
        this.seatingCapacity = seatingCapacity;
    }

    // SUVs apply a 25% premium surcharge due to their size and features.

    @Override
    public double calculateRentalCost(int days) {
        return getBasePricePerDay() * days * 1.25;
    }

    @Override
    public String getVehicleType() {
        return "SUV";
    }

    public boolean is4WD() { return is4WD; }
    public void setIs4WD(boolean is4WD) { this.is4WD = is4WD; }

    public int getSeatingCapacity() { return seatingCapacity; }
    public void setSeatingCapacity(int seatingCapacity) { this.seatingCapacity = seatingCapacity; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | 4WD: %s | Seats: %d",
                is4WD ? "Yes" : "No", seatingCapacity);
    }
}
