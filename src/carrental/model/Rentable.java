package carrental.model;

/* Rentable interface defining the contract that all rentable vehicle types must implement.
   Demonstrates Abstraction through interface-based design. */

public interface Rentable {

    double calculateRentalCost(int days);

    // Returns the type of the vehicle (e.g., "Car", "Van", "SUV").

    String getVehicleType();
}
