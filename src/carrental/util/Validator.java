package carrental.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/* Validator — centralized input validation utility.
   All field validation logic is here to keep GUI and manager classes clean. */

public class Validator {

    // Regex: XXXXX-XXXXXXX-X
    private static final Pattern CNIC_PATTERN = Pattern.compile("^\\d{5}-\\d{7}-\\d$");

    // Pakistani phone: 03XX-XXXXXXX or 03XXXXXXXXX
    private static final Pattern PHONE_PATTERN = Pattern.compile("^03\\d{2}-?\\d{7}$");

    // Vehicle ID: alphanumeric, 3-10 chars
    private static final Pattern VEHICLE_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{3,10}$");

    // Returns true if the CNIC matches the format XXXXX-XXXXXXX-X.

    public static boolean isValidCnic(String cnic) {
        if (cnic == null) return false;
        return CNIC_PATTERN.matcher(cnic.trim()).matches();
    }

    // Returns true if the phone number is a valid Pakistani mobile number.

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    // Returns true if the string is non-null and non-empty after trimming.

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Returns true if the value is a positive number (> 0).

    public static boolean isPositiveDouble(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Returns true if the value is a positive integer.

    public static boolean isPositiveInt(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Returns true if the return date is strictly after the start date.

    public static boolean isValidDateRange(LocalDate start, LocalDate returnDate) {
        if (start == null || returnDate == null) return false;
        return returnDate.isAfter(start);
    }

    // Returns true if the vehicle ID is alphanumeric and 3–10 characters.

    public static boolean isValidVehicleId(String id) {
        if (id == null) return false;
        return VEHICLE_ID_PATTERN.matcher(id.trim()).matches();
    }

    // Validates a complete set of vehicle fields. Returns an error message or null if valid.

    public static String validateVehicleFields(String id, String brand, String model,
                                               String year, String pricePerDay) {
        if (!isValidVehicleId(id))
            return "Vehicle ID must be 3–10 alphanumeric characters.";
        if (!isNotEmpty(brand))
            return "Brand cannot be empty.";
        if (!isNotEmpty(model))
            return "Model cannot be empty.";
        if (!isPositiveInt(year) || Integer.parseInt(year) < 1990 || Integer.parseInt(year) > LocalDate.now().getYear() + 1)
            return "Please enter a valid year (1990 – " + (LocalDate.now().getYear() + 1) + ").";
        if (!isPositiveDouble(pricePerDay))
            return "Base price per day must be a positive number.";
        return null; // valid
    }

    // Validates a complete set of customer fields. Returns an error message or null if valid.

    public static String validateCustomerFields(String id, String name, String cnic,
                                                String phone, String address, String license) {
        if (!isNotEmpty(id))
            return "Customer ID cannot be empty.";
        if (!isNotEmpty(name))
            return "Name cannot be empty.";
        if (!isValidCnic(cnic))
            return "CNIC must be in format XXXXX-XXXXXXX-X.";
        if (!isValidPhone(phone))
            return "Phone must be a valid Pakistani mobile number (e.g. 0300-1234567).";
        if (!isNotEmpty(address))
            return "Address cannot be empty.";
        if (!isNotEmpty(license))
            return "License number cannot be empty.";
        return null; // valid
    }
}
