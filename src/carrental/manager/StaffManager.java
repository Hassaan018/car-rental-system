package carrental.manager;

import carrental.model.Staff;
import carrental.util.FileHandler;

import java.util.ArrayList;
import java.util.List;

 // StaffManager — manages staff accounts and handles authentication.

public class StaffManager {

    private List<Staff> staffList;

    public StaffManager() {
        this.staffList = FileHandler.loadStaff();
        if (staffList == null) staffList = new ArrayList<>();

        // Seed a default admin account if no staff exist
        if (staffList.isEmpty()) {
            Staff admin = new Staff("STF-001", "Admin User", "0300-0000000",
                    "Head Office", "admin", "admin123", "System Administrator", true);
            staffList.add(admin);
            save();
        }
    }

    /* Authenticates a staff member by username and password.
       @return The authenticated Staff object, or null if credentials are wrong. */

    public Staff authenticate(String username, String password) {
        return staffList.stream()
                .filter(s -> s.getUsername().equals(username) && s.authenticate(password))
                .findFirst().orElse(null);
    }

    public String addStaff(Staff staff) {
        if (findByUsername(staff.getUsername()) != null) {
            return "Username '" + staff.getUsername() + "' is already taken.";
        }
        staffList.add(staff);
        save();
        return null;
    }

    public Staff findByUsername(String username) {
        return staffList.stream()
                .filter(s -> s.getUsername().equalsIgnoreCase(username))
                .findFirst().orElse(null);
    }

    public List<Staff> getAllStaff() { return staffList; }

    public void save() {
        FileHandler.saveStaff(staffList);
    }
}
