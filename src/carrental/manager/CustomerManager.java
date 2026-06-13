package carrental.manager;

import carrental.model.Customer;
import carrental.util.FileHandler;

import java.util.*;
import java.util.stream.Collectors;

/* CustomerManager — manages all customer records.
   Handles registration, updates, deletion, and search. */

public class CustomerManager {

    private List<Customer> customers;

    public CustomerManager() {
        this.customers = FileHandler.loadCustomers();
        if (customers == null) customers = new ArrayList<>();
    }

    // CRUD Operations

     // Registers a new customer. Returns error message or null on success.

    public String addCustomer(Customer customer) {
        if (customer == null) return "Customer cannot be null.";
        if (findById(customer.getPersonId()) != null) {
            return "A customer with ID '" + customer.getPersonId() + "' already exists.";
        }
        if (findByCnic(customer.getCnic()) != null) {
            return "A customer with CNIC '" + customer.getCnic() + "' is already registered.";
        }
        customers.add(customer);
        save();
        return null;
    }

     // Updates an existing customer's mutable fields.

    public String updateCustomer(String customerId, String name, String contactNumber,
                                 String address, String licenseNumber) {
        Customer c = findById(customerId);
        if (c == null) return "Customer not found: " + customerId;

        c.setName(name);
        c.setContactNumber(contactNumber);
        c.setAddress(address);
        c.setLicenseNumber(licenseNumber);
        save();
        return null;
    }

     // Deletes a customer by ID.

    public String deleteCustomer(String customerId) {
        Customer c = findById(customerId);
        if (c == null) return "Customer not found: " + customerId;
        customers.remove(c);
        save();
        return null;
    }

    // Search

    public Customer findById(String customerId) {
        return customers.stream()
                .filter(c -> c.getPersonId().equalsIgnoreCase(customerId))
                .findFirst().orElse(null);
    }

    public Customer findByCnic(String cnic) {
        return customers.stream()
                .filter(c -> c.getCnic().equalsIgnoreCase(cnic))
                .findFirst().orElse(null);
    }

     // Searches by name or ID (case-insensitive).

    public List<Customer> searchByNameOrId(String query) {
        String q = query.toLowerCase().trim();
        return customers.stream()
                .filter(c -> c.getName().toLowerCase().contains(q)
                          || c.getPersonId().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Customer> getAllCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public int getTotalCustomerCount() { return customers.size(); }

    // Persistence

    public void save() {
        FileHandler.saveCustomers(customers);
    }
}
