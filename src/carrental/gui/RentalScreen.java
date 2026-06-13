package carrental.gui;

import carrental.manager.CustomerManager;
import carrental.manager.RentalManager;
import carrental.manager.VehicleManager;
import carrental.model.Customer;
import carrental.model.Staff;
import carrental.model.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/*  RentalScreen — create new rental bookings.
    Dropdown for available vehicles and customers, date pickers for rental period. */

public class RentalScreen extends JPanel implements Refreshable {

    private final VehicleManager  vehicleManager;
    private final CustomerManager customerManager;
    private final RentalManager   rentalManager;
    private final Staff           loggedInStaff;

    // Form fields
    private JComboBox<String> vehicleBox;
    private JComboBox<String> customerBox;
    private JTextField        startDateField;
    private JTextField        returnDateField;
    private JComboBox<String> vehicleTypeBox;
    private JLabel            costPreviewLabel;

    // Existing rentals table
    private DefaultTableModel tableModel;

    public RentalScreen(VehicleManager vm, CustomerManager cm,
                        RentalManager rm, Staff staff) {
        this.vehicleManager  = vm;
        this.customerManager = cm;
        this.rentalManager   = rm;
        this.loggedInStaff   = staff;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("New Rental Booking");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(18, 24, 38));
        add(title, BorderLayout.NORTH);

        // Main split: form left, table right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(380);
        split.setBackground(new Color(240, 242, 248));

        split.setLeftComponent(buildForm());
        split.setRightComponent(buildRentalsTable());
        add(split, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 235), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 4, 7, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Filter by type first
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1;
        vehicleTypeBox = new JComboBox<>(new String[]{"All", "Car", "Van", "SUV"});
        vehicleTypeBox.addActionListener(e -> loadAvailableVehicles());
        panel.add(vehicleTypeBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Available Vehicle:"), gbc);
        gbc.gridx = 1;
        vehicleBox = new JComboBox<>();
        vehicleBox.addActionListener(e -> updateCostPreview());
        panel.add(vehicleBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1;
        customerBox = new JComboBox<>();
        panel.add(customerBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(LocalDate.now().toString(), 14);
        startDateField.addActionListener(e -> updateCostPreview());
        panel.add(startDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Return Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        returnDateField = new JTextField(LocalDate.now().plusDays(3).toString(), 14);
        returnDateField.addActionListener(e -> updateCostPreview());
        panel.add(returnDateField, gbc);

        // Cost preview
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        costPreviewLabel = new JLabel("Estimated Cost: —");
        costPreviewLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        costPreviewLabel.setForeground(new Color(40, 120, 40));
        panel.add(costPreviewLabel, gbc);

        // Book button
        gbc.gridy = 6;
        JButton bookBtn = new JButton("📋  Confirm Booking");
        bookBtn.setBackground(new Color(52, 120, 246));
        bookBtn.setForeground(Color.BLACK);
        bookBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        bookBtn.setFocusPainted(false);
        bookBtn.setBorderPainted(false);
        bookBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookBtn.addActionListener(e -> confirmBooking());
        panel.add(bookBtn, gbc);

        return panel;
    }

    private JPanel buildRentalsTable() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(240, 242, 248));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel lbl = new JLabel("All Active Rentals");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(lbl, BorderLayout.NORTH);

        String[] cols = {"Rental ID", "Customer", "Vehicle", "Start", "Return", "PKR"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(new Color(18, 24, 38));
        table.getTableHeader().setForeground(Color.BLACK);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadAvailableVehicles() {
        String type = (String) vehicleTypeBox.getSelectedItem();
        vehicleBox.removeAllItems();
        List<Vehicle> available = vehicleManager.getAvailableVehicles(type);
        for (Vehicle v : available) {
            vehicleBox.addItem(v.getVehicleId() + " - " + v.getBrand() + " " + v.getModel()
                    + " (" + v.getVehicleType() + ")");
        }
        updateCostPreview();
    }

    private void loadCustomers() {
        customerBox.removeAllItems();
        for (Customer c : customerManager.getAllCustomers()) {
            customerBox.addItem(c.getPersonId() + " - " + c.getName());
        }
    }

    private void updateCostPreview() {
        try {
            String selectedVehicle = (String) vehicleBox.getSelectedItem();
            if (selectedVehicle == null) { costPreviewLabel.setText("Estimated Cost: —"); return; }

            String vehicleId = selectedVehicle.split(" - ")[0].trim();
            Vehicle v = vehicleManager.findById(vehicleId);
            if (v == null) return;

            LocalDate start  = LocalDate.parse(startDateField.getText().trim());
            LocalDate end    = LocalDate.parse(returnDateField.getText().trim());

            if (!end.isAfter(start)) {
                costPreviewLabel.setText("⚠ Return date must be after start date.");
                costPreviewLabel.setForeground(Color.RED);
                return;
            }

            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(start, end);
            double cost = v.calculateRentalCost(days);
            costPreviewLabel.setText(String.format("Estimated Cost: PKR %.0f  (%d days)", cost, days));
            costPreviewLabel.setForeground(new Color(40, 120, 40));
        } catch (DateTimeParseException ex) {
            costPreviewLabel.setText("Use format: YYYY-MM-DD");
            costPreviewLabel.setForeground(Color.ORANGE);
        }
    }

    private void confirmBooking() {
        String selectedVehicle  = (String) vehicleBox.getSelectedItem();
        String selectedCustomer = (String) customerBox.getSelectedItem();

        if (selectedVehicle == null) {
            JOptionPane.showMessageDialog(this, "No available vehicles found.", "Error", JOptionPane.ERROR_MESSAGE); return; }
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please register a customer first.", "Error", JOptionPane.ERROR_MESSAGE); return; }

        try {
            String vehicleId  = selectedVehicle.split(" - ")[0].trim();
            String customerId = selectedCustomer.split(" - ")[0].trim();
            LocalDate start  = LocalDate.parse(startDateField.getText().trim());
            LocalDate end    = LocalDate.parse(returnDateField.getText().trim());

            if (!end.isAfter(start)) {
                JOptionPane.showMessageDialog(this, "Return date must be after start date.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }

            String err = rentalManager.createRental(customerId, vehicleId, start, end, loggedInStaff.getPersonId());
            if (err != null) {
                JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Rental booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        loadAvailableVehicles();
        loadCustomers();
        tableModel.setRowCount(0);
        for (var r : rentalManager.getActiveRentals()) {
            tableModel.addRow(new Object[]{
                    r.getRentalId(), r.getCustomerId(), r.getVehicleId(),
                    r.getStartDate(), r.getExpectedReturnDate(),
                    String.format("%.0f", r.getTotalCost())
            });
        }
    }
}
