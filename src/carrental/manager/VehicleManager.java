package carrental.manager;

import carrental.model.*;
import carrental.util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

/* VehicleManager — manages the vehicle fleet.
   Handles add, edit, delete, search, and filter operations. */

public class VehicleManager {

    private List<Vehicle> vehicles;

    public VehicleManager() {
        this.vehicles = FileHandler.loadVehicles();
        if (vehicles == null) vehicles = new ArrayList<>();
    }

    // CRUD Operations

     // Adds a new vehicle. Returns error message or null on success.

    public String addVehicle(Vehicle vehicle) {
        if (vehicle == null) return "Vehicle cannot be null.";
        if (findById(vehicle.getVehicleId()) != null) {
            return "A vehicle with ID '" + vehicle.getVehicleId() + "' already exists.";
        }
        vehicles.add(vehicle);
        save();
        return null;
    }

    /* Updates an existing vehicle's mutable fields.
       Returns error message or null on success. */

    public String updateVehicle(String vehicleId, String brand, String model, int year,
                                String color, double basePricePerDay) {
        Vehicle v = findById(vehicleId);
        if (v == null) return "Vehicle not found: " + vehicleId;

        v.setBrand(brand);
        v.setModel(model);
        v.setYear(year);
        v.setColor(color);
        v.setBasePricePerDay(basePricePerDay);
        save();
        return null;
    }


    // Removes a vehicle. Cannot delete a currently rented vehicle.

    public String deleteVehicle(String vehicleId) {
        Vehicle v = findById(vehicleId);
        if (v == null) return "Vehicle not found: " + vehicleId;
        if (!v.isAvailable()) return "Cannot delete a vehicle that is currently rented out.";

        vehicles.remove(v);
        save();
        return null;
    }

    // Search & Filter

    public Vehicle findById(String vehicleId) {
        return vehicles.stream()
                .filter(v -> v.getVehicleId().equalsIgnoreCase(vehicleId))
                .findFirst().orElse(null);
    }


    // Searches vehicles by brand or model (case-insensitive).

    public List<Vehicle> searchByBrandOrModel(String query) {
        String q = query.toLowerCase().trim();
        return vehicles.stream()
                .filter(v -> v.getBrand().toLowerCase().contains(q)
                          || v.getModel().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }


    // Filters vehicles by type: "Car", "Van", "SUV", or "All".

    public List<Vehicle> filterByType(String type) {
        if (type == null || type.equalsIgnoreCase("All")) return getAllVehicles();
        return vehicles.stream()
                .filter(v -> v.getVehicleType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // Returns only available vehicles, optionally filtered by type.

    public List<Vehicle> getAvailableVehicles(String type) {
        return vehicles.stream()
                .filter(Vehicle::isAvailable)
                .filter(v -> type == null || type.equalsIgnoreCase("All")
                          || v.getVehicleType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    // Dashboard Stats

    public int getTotalVehicleCount()     { return vehicles.size(); }
    public long getAvailableCount()       { return vehicles.stream().filter(Vehicle::isAvailable).count(); }
    public long getRentedCount()          { return vehicles.stream().filter(v -> !v.isAvailable()).count(); }

    // Persistence

    public void save() {
        FileHandler.saveVehicles(vehicles);
    }
}
