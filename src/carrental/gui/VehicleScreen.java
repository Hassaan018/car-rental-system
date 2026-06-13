package carrental.gui;

import carrental.manager.VehicleManager;
import carrental.model.*;
import carrental.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/* VehicleScreen — add, edit, view, delete, search, and filter vehicles. */

public class VehicleScreen extends JPanel implements Refreshable {

    private final VehicleManager vehicleManager;

    // Table
    private DefaultTableModel tableModel;
    private JTable table;

    // Filters
    private JTextField searchField;
    private JComboBox<String> typeFilterBox;

    public VehicleScreen(VehicleManager vehicleManager) {
        this.vehicleManager = vehicleManager;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Vehicle Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(18, 24, 38));
        add(title, BorderLayout.NORTH);

        // Toolbar: search + filter + buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(new Color(240, 242, 248));

        toolbar.add(new JLabel("Search:"));
        searchField = new JTextField(16);
        toolbar.add(searchField);

        toolbar.add(new JLabel("Type:"));
        typeFilterBox = new JComboBox<>(new String[]{"All", "Car", "Van", "SUV"});
        toolbar.add(typeFilterBox);

        JButton searchBtn = new JButton("🔍 Search");
        searchBtn.addActionListener(e -> applyFilter());
        toolbar.add(searchBtn);

        JButton refreshBtn = new JButton("↺ Refresh");
        refreshBtn.addActionListener(e -> refresh());
        toolbar.add(refreshBtn);

        JButton addBtn = new JButton("➕ Add Vehicle");
        addBtn.setBackground(new Color(52, 120, 246));
        addBtn.setForeground(Color.BLACK);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddDialog());
        toolbar.add(addBtn);

        JButton editBtn = new JButton("✏ Edit");
        editBtn.addActionListener(e -> showEditDialog());
        toolbar.add(editBtn);

        JButton deleteBtn = new JButton("🗑 Delete");
        deleteBtn.setBackground(new Color(200, 50, 50));
        deleteBtn.setForeground(Color.BLACK);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteSelected());
        toolbar.add(deleteBtn);

        add(toolbar, BorderLayout.CENTER);

        // Table
        String[] cols = {"ID", "Type", "Brand", "Model", "Year", "Color", "Rate/Day (PKR)", "Available"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(18, 24, 38));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(200, 220, 255));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.SOUTH);

        refresh();
    }

    private void applyFilter() {
        String query  = searchField.getText().trim();
        String type   = (String) typeFilterBox.getSelectedItem();
        List<Vehicle> results;

        if (!query.isEmpty()) {
            results = vehicleManager.searchByBrandOrModel(query);
        } else {
            results = vehicleManager.filterByType(type);
        }
        populateTable(results);
    }

    private void populateTable(List<Vehicle> list) {
        tableModel.setRowCount(0);
        for (Vehicle v : list) {
            tableModel.addRow(new Object[]{
                    v.getVehicleId(), v.getVehicleType(), v.getBrand(), v.getModel(),
                    v.getYear(), v.getColor(),
                    String.format("%.0f", v.getBasePricePerDay()),
                    v.isAvailable() ? "Yes" : "No"
            });
        }
    }

    @Override
    public void refresh() {
        populateTable(vehicleManager.getAllVehicles());
    }

    // Add Dialog

    private void showAddDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Vehicle", true);
        dlg.setSize(440, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Type
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Vehicle Type:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Car", "Van", "SUV"});
        form.add(typeBox, gbc);

        // Fields
        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Vehicle ID:"), gbc);
        gbc.gridx = 1; JTextField idField = new JTextField(14); form.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1; JTextField brandField = new JTextField(14); form.add(brandField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1; JTextField modelField = new JTextField(14); form.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; JTextField yearField = new JTextField(14); form.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; form.add(new JLabel("Color:"), gbc);
        gbc.gridx = 1; JTextField colorField = new JTextField(14); form.add(colorField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; form.add(new JLabel("Base Price/Day (PKR):"), gbc);
        gbc.gridx = 1; JTextField priceField = new JTextField(14); form.add(priceField, gbc);

        // Type-specific fields
        gbc.gridx = 0; gbc.gridy = 7; form.add(new JLabel("Extra Field 1:"), gbc);
        gbc.gridx = 1; JTextField extra1 = new JTextField(14); form.add(extra1, gbc);

        gbc.gridx = 0; gbc.gridy = 8; form.add(new JLabel("Extra Field 2:"), gbc);
        gbc.gridx = 1; JTextField extra2 = new JTextField(14); form.add(extra2, gbc);

        JLabel hint = new JLabel("Car: Doors, Transmission | Van: Capacity, Cargo(Y/N) | SUV: 4WD(Y/N), Seats");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 10));
        hint.setForeground(Color.GRAY);
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        form.add(hint, gbc);

        dlg.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(52, 120, 246));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            String err = Validator.validateVehicleFields(
                    idField.getText(), brandField.getText(), modelField.getText(),
                    yearField.getText(), priceField.getText());
            if (err != null) { JOptionPane.showMessageDialog(dlg, err, "Validation Error", JOptionPane.ERROR_MESSAGE); return; }
            if (!Validator.isNotEmpty(colorField.getText())) {
                JOptionPane.showMessageDialog(dlg, "Color cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }

            String type = (String) typeBox.getSelectedItem();
            String id   = idField.getText().trim();
            String brand = brandField.getText().trim();
            String model = modelField.getText().trim();
            int    year  = Integer.parseInt(yearField.getText().trim());
            String color = colorField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            String e1    = extra1.getText().trim();
            String e2    = extra2.getText().trim();

            Vehicle vehicle;
            try {
                if ("Car".equals(type)) {
                    int doors = e1.isEmpty() ? 4 : Integer.parseInt(e1);
                    String transmission = e2.isEmpty() ? "Manual" : e2;
                    vehicle = new Car(id, brand, model, year, color, price, doors, transmission);
                } else if ("Van".equals(type)) {
                    int cap = e1.isEmpty() ? 8 : Integer.parseInt(e1);
                    boolean cargo = e2.equalsIgnoreCase("Y") || e2.equalsIgnoreCase("Yes");
                    vehicle = new Van(id, brand, model, year, color, price, cap, cargo);
                } else {
                    boolean wd4 = e1.equalsIgnoreCase("Y") || e1.equalsIgnoreCase("Yes");
                    int seats = e2.isEmpty() ? 7 : Integer.parseInt(e2);
                    vehicle = new SUV(id, brand, model, year, color, price, wd4, seats);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Extra fields must be numbers where required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String result = vehicleManager.addVehicle(vehicle);
            if (result != null) {
                JOptionPane.showMessageDialog(dlg, result, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dlg, "Vehicle added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refresh();
            }
        });
        btnPanel.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dlg.dispose());
        btnPanel.add(cancelBtn);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // Edit Dialog

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a vehicle to edit."); return; }

        String vehicleId = (String) tableModel.getValueAt(row, 0);
        Vehicle v = vehicleManager.findById(vehicleId);
        if (v == null) return;

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Vehicle", true);
        dlg.setSize(380, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Brand:"), gbc);
        gbc.gridx = 1; JTextField brandField = new JTextField(v.getBrand(), 14); form.add(brandField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Model:"), gbc);
        gbc.gridx = 1; JTextField modelField = new JTextField(v.getModel(), 14); form.add(modelField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1; JTextField yearField = new JTextField(String.valueOf(v.getYear()), 14); form.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Color:"), gbc);
        gbc.gridx = 1; JTextField colorField = new JTextField(v.getColor(), 14); form.add(colorField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Price/Day (PKR):"), gbc);
        gbc.gridx = 1; JTextField priceField = new JTextField(String.valueOf(v.getBasePricePerDay()), 14); form.add(priceField, gbc);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Update");
        saveBtn.setBackground(new Color(52, 120, 246));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            if (!Validator.isPositiveDouble(priceField.getText()) || !Validator.isPositiveInt(yearField.getText())) {
                JOptionPane.showMessageDialog(dlg, "Please enter valid year and price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String err = vehicleManager.updateVehicle(vehicleId,
                    brandField.getText().trim(), modelField.getText().trim(),
                    Integer.parseInt(yearField.getText().trim()),
                    colorField.getText().trim(),
                    Double.parseDouble(priceField.getText().trim()));
            if (err != null) {
                JOptionPane.showMessageDialog(dlg, err, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dlg, "Vehicle updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refresh();
            }
        });
        btnPanel.add(saveBtn);
        btnPanel.add(new JButton("Cancel") {{ addActionListener(e -> dlg.dispose()); }});
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // Delete

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a vehicle to delete."); return; }

        String vehicleId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete vehicle " + vehicleId + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String err = vehicleManager.deleteVehicle(vehicleId);
        if (err != null) {
            JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Vehicle deleted.");
            refresh();
        }
    }
}
