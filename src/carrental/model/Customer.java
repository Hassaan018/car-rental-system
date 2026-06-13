package carrental.model;

/* Customer class — inherits common attributes from Person.
   Adds CNIC and license number specific to customers. */

public class Customer extends Person {

    private static final long serialVersionUID = 1L;

    private String cnic;           // Format: XXXXX-XXXXXXX-X
    private String licenseNumber;
    private int totalRentals;

    public Customer(String customerId, String name, String cnic, String contactNumber,
                    String address, String licenseNumber) {
        super(customerId, name, contactNumber, address);
        this.cnic = cnic;
        this.licenseNumber = licenseNumber;
        this.totalRentals = 0;
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    public String getCnic() { return cnic; }
    public void setCnic(String cnic) { this.cnic = cnic; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public int getTotalRentals() { return totalRentals; }
    public void incrementTotalRentals() { this.totalRentals++; }
    public void setTotalRentals(int totalRentals) { this.totalRentals = totalRentals; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | CNIC: %s | License: %s | Rentals: %d",
                cnic, licenseNumber, totalRentals);
    }
}
