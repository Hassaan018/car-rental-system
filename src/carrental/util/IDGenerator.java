package carrental.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

  // IDGenerator — generates unique IDs for rentals, customers, and vehicles.

public class IDGenerator {

    private static final AtomicInteger rentalCounter   = new AtomicInteger(1);
    private static final AtomicInteger customerCounter = new AtomicInteger(1);
    private static final AtomicInteger vehicleCounter  = new AtomicInteger(1);

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    // Generates a unique Rental ID: RNT-YYMMDD-XXX

    public static String generateRentalId() {
        String date = LocalDateTime.now().format(FORMATTER);
        return String.format("RNT-%s-%03d", date, rentalCounter.getAndIncrement());
    }

    // Generates a unique Customer ID: CUS-XXX

    public static String generateCustomerId() {
        return String.format("CUS-%03d", customerCounter.getAndIncrement());
    }

    // Generates a sequential Vehicle ID prefix for suggestion: VEH-XXX

    public static String generateVehicleId() {
        return String.format("VEH-%03d", vehicleCounter.getAndIncrement());
    }

    // Syncs the rental counter to avoid duplicates when loading existing data.

    public static void syncCounters(int maxRental, int maxCustomer, int maxVehicle) {
        if (maxRental >= rentalCounter.get())   rentalCounter.set(maxRental + 1);
        if (maxCustomer >= customerCounter.get()) customerCounter.set(maxCustomer + 1);
        if (maxVehicle >= vehicleCounter.get())  vehicleCounter.set(maxVehicle + 1);
    }
}
