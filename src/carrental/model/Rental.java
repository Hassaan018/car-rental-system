package carrental.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/* Rental class — links a Customer and Vehicle with a rental period.
   Tracks rental status and stores invoice details. */

public class Rental implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum RentalStatus {
        ACTIVE, COMPLETED, CANCELLED
    }

    private String rentalId;
    private String customerId;
    private String vehicleId;
    private LocalDate startDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private double totalCost;
    private RentalStatus status;
    private String staffId;           // Who processed this rental
    private String notes;

    public Rental(String rentalId, String customerId, String vehicleId,
                  LocalDate startDate, LocalDate expectedReturnDate, String staffId) {
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;
        this.staffId = staffId;
        this.status = RentalStatus.ACTIVE;
        this.totalCost = 0.0;
        this.notes = "";
    }

    // Returns the number of rental days based on start and expected return date.

    public int getRentalDays() {
        return (int) ChronoUnit.DAYS.between(startDate, expectedReturnDate);
    }

    //Returns the number of actual days if the vehicle has been returned.

    public int getActualDays() {
        if (actualReturnDate != null) {
            return (int) ChronoUnit.DAYS.between(startDate, actualReturnDate);
        }
        return getRentalDays();
    }

    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    // Getters and Setters
    public String getRentalId() { return rentalId; }
    public void setRentalId(String rentalId) { this.rentalId = rentalId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(LocalDate actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return String.format("Rental[%s] Customer:%s Vehicle:%s | %s to %s | PKR %.0f | %s",
                rentalId, customerId, vehicleId, startDate, expectedReturnDate, totalCost, status);
    }
}
