package carrental.model;

import java.io.Serializable;

/* Abstract base class for all people in the system.
   Customer and Staff inherit from this class (Inheritance). */

public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    private String personId;
    private String name;
    private String contactNumber;
    private String address;

    public Person(String personId, String name, String contactNumber, String address) {
        this.personId = personId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    public abstract String getRole();

    // Getters and Setters (Encapsulation)
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Contact: %s | Address: %s", personId, name, contactNumber, address);
    }
}
