package carrental.model;

/* Van vehicle type. Larger capacity vehicles with a 15% surcharge.
   Overrides rental cost calculation (Polymorphism). */

public class Van extends Vehicle {

    private static final long serialVersionUID = 1L;

    private int passengerCapacity;
    private boolean hasCargo;

    public Van(String vehicleId, String brand, String model, int year, String color,
               double basePricePerDay, int passengerCapacity, boolean hasCargo) {
        super(vehicleId, brand, model, year, color, basePricePerDay);
        this.passengerCapacity = passengerCapacity;
        this.hasCargo = hasCargo;
    }

    // Vans apply a 15% surcharge over base rate due to higher capacity.

    @Override
    public double calculateRentalCost(int days) {
        return getBasePricePerDay() * days * 1.15;
    }

    @Override
    public String getVehicleType() {
        return "Van";
    }

    public int getPassengerCapacity() { return passengerCapacity; }
    public void setPassengerCapacity(int passengerCapacity) { this.passengerCapacity = passengerCapacity; }

    public boolean isHasCargo() { return hasCargo; }
    public void setHasCargo(boolean hasCargo) { this.hasCargo = hasCargo; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Capacity: %d | Cargo: %s",
                passengerCapacity, hasCargo ? "Yes" : "No");
    }
}
