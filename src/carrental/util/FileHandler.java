package carrental.util;

import carrental.model.Customer;
import carrental.model.Rental;
import carrental.model.Staff;
import carrental.model.Vehicle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/* FileHandler — manages reading and writing of all data to .dat files.
   Uses Java Object Serialization for persistence across sessions. */

public class FileHandler {

    private static final String DATA_DIR    = "data/";
    private static final String VEHICLES_FILE  = DATA_DIR + "vehicles.dat";
    private static final String CUSTOMERS_FILE = DATA_DIR + "customers.dat";
    private static final String RENTALS_FILE   = DATA_DIR + "rentals.dat";
    private static final String STAFF_FILE     = DATA_DIR + "staff.dat";

    static {
        // Ensure data directory exists on first run
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Vehicles

    @SuppressWarnings("unchecked")
    public static List<Vehicle> loadVehicles() {
        return (List<Vehicle>) loadObject(VEHICLES_FILE);
    }

    public static void saveVehicles(List<Vehicle> vehicles) {
        saveObject(VEHICLES_FILE, vehicles);
    }

    // Customers

    @SuppressWarnings("unchecked")
    public static List<Customer> loadCustomers() {
        return (List<Customer>) loadObject(CUSTOMERS_FILE);
    }

    public static void saveCustomers(List<Customer> customers) {
        saveObject(CUSTOMERS_FILE, customers);
    }

    // Rentals

    @SuppressWarnings("unchecked")
    public static List<Rental> loadRentals() {
        return (List<Rental>) loadObject(RENTALS_FILE);
    }

    public static void saveRentals(List<Rental> rentals) {
        saveObject(RENTALS_FILE, rentals);
    }

    // Staff

    @SuppressWarnings("unchecked")
    public static List<Staff> loadStaff() {
        return (List<Staff>) loadObject(STAFF_FILE);
    }

    public static void saveStaff(List<Staff> staffList) {
        saveObject(STAFF_FILE, staffList);
    }

    // Generic helpers

    private static void saveObject(String filePath, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath + ": " + e.getMessage());
        }
    }

    private static Object loadObject(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
