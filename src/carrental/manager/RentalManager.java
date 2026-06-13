package carrental.manager;

import carrental.model.*;
import carrental.util.FileHandler;
import carrental.util.IDGenerator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/* RentalManager — handles all rental business logic:
   booking creation, vehicle returns, invoice generation, and search. */

public class RentalManager {

    private List<Rental> rentals;
    private VehicleManager vehicleManager;
    private CustomerManager customerManager;

    public RentalManager(VehicleManager vehicleManager, CustomerManager customerManager) {
        this.vehicleManager  = vehicleManager;
        this.customerManager = customerManager;
        this.rentals = FileHandler.loadRentals();
    }

    // Booking

    /* Creates a new rental booking.
       Validates availability and date range before confirming.
       return Error message string, or null on success. */

    public String createRental(String customerId, String vehicleId,
                               LocalDate startDate, LocalDate returnDate, String staffId) {
        // Validate date range
        if (!returnDate.isAfter(startDate)) {
            return "Return date must be after the start date.";
        }

        Customer customer = customerManager.findById(customerId);
        if (customer == null) return "Customer not found: " + customerId;

        Vehicle vehicle = vehicleManager.findById(vehicleId);
        if (vehicle == null) return "Vehicle not found: " + vehicleId;

        if (!vehicle.isAvailable()) return "Vehicle " + vehicleId + " is currently not available.";

        // Create rental
        String rentalId = IDGenerator.generateRentalId();
        Rental rental = new Rental(rentalId, customerId, vehicleId, startDate, returnDate, staffId);

        // Calculate cost
        int days = rental.getRentalDays();
        double cost = vehicle.calculateRentalCost(days);
        rental.setTotalCost(cost);

        // Mark vehicle as unavailable
        vehicle.setAvailable(false);
        vehicleManager.save();

        // Increment customer rental count
        customer.incrementTotalRentals();
        customerManager.save();

        rentals.add(rental);
        save();

        return null; // Success
    }

    //  Return Processing

    /* Processes the return of a rented vehicle.
       Marks the car as available, finalizes the invoice cost.
       @return The finalized Rental object, or null if not found. */

    public Rental processReturn(String rentalId, LocalDate actualReturnDate, String notes) {
        Rental rental = findById(rentalId);
        if (rental == null || !rental.isActive()) return null;

        Vehicle vehicle = vehicleManager.findById(rental.getVehicleId());
        if (vehicle == null) return null;

        // Recalculate cost based on actual return date
        rental.setActualReturnDate(actualReturnDate);
        int actualDays = rental.getActualDays();
        if (actualDays < 1) actualDays = 1; // minimum 1 day charge
        double finalCost = vehicle.calculateRentalCost(actualDays);
        rental.setTotalCost(finalCost);
        rental.setStatus(Rental.RentalStatus.COMPLETED);
        rental.setNotes(notes != null ? notes : "");

        // Mark vehicle as available again
        vehicle.setAvailable(true);
        vehicleManager.save();

        save();
        return rental;
    }

    // Invoice

     // Generates a formatted invoice string for a rental.

    public String generateInvoice(String rentalId) {
        Rental rental = findById(rentalId);
        if (rental == null) return "Rental not found.";

        Customer customer = customerManager.findById(rental.getCustomerId());
        Vehicle  vehicle  = vehicleManager.findById(rental.getVehicleId());

        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("            DRIVE EASY CAR RENTALS — INVOICE               \n");
        sb.append("============================================================\n");
        sb.append(String.format("Invoice / Rental ID : %s%n", rental.getRentalId()));
        sb.append(String.format("Invoice Date        : %s%n", LocalDate.now()));
        sb.append("------------------------------------------------------------\n");
        sb.append("CUSTOMER DETAILS\n");
        if (customer != null) {
            sb.append(String.format("  Name    : %s%n", customer.getName()));
            sb.append(String.format("  CNIC    : %s%n", customer.getCnic()));
            sb.append(String.format("  Contact : %s%n", customer.getContactNumber()));
            sb.append(String.format("  Address : %s%n", customer.getAddress()));
        }
        sb.append("------------------------------------------------------------\n");
        sb.append("VEHICLE DETAILS\n");
        if (vehicle != null) {
            sb.append(String.format("  Type    : %s%n", vehicle.getVehicleType()));
            sb.append(String.format("  Brand   : %s%n", vehicle.getBrand()));
            sb.append(String.format("  Model   : %s%n", vehicle.getModel()));
            sb.append(String.format("  Year    : %d%n", vehicle.getYear()));
            sb.append(String.format("  Rate    : PKR %.0f / day%n", vehicle.getBasePricePerDay()));
        }
        sb.append("------------------------------------------------------------\n");
        sb.append("RENTAL DETAILS\n");
        sb.append(String.format("  Start Date      : %s%n", rental.getStartDate()));
        sb.append(String.format("  Expected Return : %s%n", rental.getExpectedReturnDate()));
        if (rental.getActualReturnDate() != null) {
            sb.append(String.format("  Actual Return   : %s%n", rental.getActualReturnDate()));
            sb.append(String.format("  Rental Days     : %d%n", rental.getActualDays()));
        } else {
            sb.append(String.format("  Rental Days     : %d%n", rental.getRentalDays()));
        }
        sb.append(String.format("  Status          : %s%n", rental.getStatus()));
        if (!rental.getNotes().isEmpty()) {
            sb.append(String.format("  Notes           : %s%n", rental.getNotes()));
        }
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("  TOTAL AMOUNT DUE: PKR %.2f%n", rental.getTotalCost()));
        sb.append("============================================================\n");
        sb.append("       Thank you for choosing Drive Easy Car Rentals!       \n");
        sb.append("============================================================\n");
        return sb.toString();
    }

    // Search & Filters

    public List<Rental> getActiveRentals() {
        return rentals.stream().filter(Rental::isActive).collect(Collectors.toList());
    }

    public List<Rental> getCompletedRentals() {
        return rentals.stream()
                .filter(r -> r.getStatus() == Rental.RentalStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    public List<Rental> getRentalsByCustomer(String customerId) {
        return rentals.stream()
                .filter(r -> r.getCustomerId().equalsIgnoreCase(customerId))
                .collect(Collectors.toList());
    }

    public List<Rental> getRentalsByVehicle(String vehicleId) {
        return rentals.stream()
                .filter(r -> r.getVehicleId().equalsIgnoreCase(vehicleId))
                .collect(Collectors.toList());
    }

    public Rental findById(String rentalId) {
        return rentals.stream()
                .filter(r -> r.getRentalId().equalsIgnoreCase(rentalId))
                .findFirst().orElse(null);
    }

    public List<Rental> getAllRentals() {
        return Collections.unmodifiableList(rentals);
    }

    // Dashboard Stats

    public int getActiveRentalCount() { return getActiveRentals().size(); }

    public double getTotalRevenue() {
        return rentals.stream()
                .filter(r -> r.getStatus() == Rental.RentalStatus.COMPLETED)
                .mapToDouble(Rental::getTotalCost)
                .sum();
    }

    // Persistence

    public void save() {
        FileHandler.saveRentals(rentals);
    }
}
