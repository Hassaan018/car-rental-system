package carrental.model;

/* Staff class — inherits from Person.
   Adds staff-specific fields: username, password, and job title. */

public class Staff extends Person {

    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String jobTitle;
    private boolean isAdmin;

    public Staff(String staffId, String name, String contactNumber, String address,
                 String username, String password, String jobTitle, boolean isAdmin) {
        super(staffId, name, contactNumber, address);
        this.username = username;
        this.password = password;
        this.jobTitle = jobTitle;
        this.isAdmin = isAdmin;
    }

    @Override
    public String getRole() {
        return isAdmin ? "Admin" : "Staff";
    }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Username: %s | Role: %s | Title: %s",
                username, getRole(), jobTitle);
    }
}
