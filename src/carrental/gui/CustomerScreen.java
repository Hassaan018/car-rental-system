package carrental.gui;

import carrental.manager.CustomerManager;
import carrental.model.Customer;
import carrental.util.IDGenerator;
import carrental.util.Validator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

  // CustomerScreen — register, view, edit, delete, and search customers.

public class CustomerScreen extends JPanel implements Refreshable {

    private final CustomerManager customerManager;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;

    public CustomerScreen(CustomerManager customerManager) {
        this.customerManager = customerManager;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Customer Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(18, 24, 38));
        add(title, BorderLayout.NORTH);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        toolbar.setBackground(new Color(240, 242, 248));

        toolbar.add(new JLabel("Search:"));
        searchField = new JTextField(18);
        toolbar.add(searchField);

        JButton searchBtn = new JButton("🔍 Search");
        searchBtn.addActionListener(e -> applySearch());
        toolbar.add(searchBtn);

        JButton refreshBtn = new JButton("↺ Refresh");
        refreshBtn.addActionListener(e -> refresh());
        toolbar.add(refreshBtn);

        JButton addBtn = new JButton("➕ Register Customer");
        addBtn.setBackground(new Color(40, 167, 90));
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
        String[] cols = {"Customer ID", "Name", "CNIC", "Contact", "Address", "License No.", "Total Rentals"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(18, 24, 38));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(200, 220, 255));

        add(new JScrollPane(table), BorderLayout.SOUTH);
        refresh();
    }

    private void applySearch() {
        String q = searchField.getText().trim();
        List<Customer> results = q.isEmpty()
                ? customerManager.getAllCustomers()
                : customerManager.searchByNameOrId(q);
        populateTable(results);
    }

    private void populateTable(List<Customer> list) {
        tableModel.setRowCount(0);
        for (Customer c : list) {
            tableModel.addRow(new Object[]{
                    c.getPersonId(), c.getName(), c.getCnic(),
                    c.getContactNumber(), c.getAddress(),
                    c.getLicenseNumber(), c.getTotalRentals()
            });
        }
    }

    @Override
    public void refresh() {
        populateTable(customerManager.getAllCustomers());
    }

    // Add Dialog

    private void showAddDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register Customer", true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Customer ID:"), gbc);
        gbc.gridx = 1; JTextField idField = new JTextField(IDGenerator.generateCustomerId(), 16);
        form.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; JTextField nameField = new JTextField(16); form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("CNIC (XXXXX-XXXXXXX-X):"), gbc);
        gbc.gridx = 1; JTextField cnicField = new JTextField(16); form.add(cnicField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Contact (03XX-XXXXXXX):"), gbc);
        gbc.gridx = 1; JTextField contactField = new JTextField(16); form.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; JTextField addressField = new JTextField(16); form.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; form.add(new JLabel("License Number:"), gbc);
        gbc.gridx = 1; JTextField licenseField = new JTextField(16); form.add(licenseField, gbc);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Register");
        saveBtn.setBackground(new Color(40, 167, 90));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            String err = Validator.validateCustomerFields(
                    idField.getText(), nameField.getText(), cnicField.getText(),
                    contactField.getText(), addressField.getText(), licenseField.getText());
            if (err != null) {
                JOptionPane.showMessageDialog(dlg, err, "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Customer c = new Customer(idField.getText().trim(), nameField.getText().trim(),
                    cnicField.getText().trim(), contactField.getText().trim(),
                    addressField.getText().trim(), licenseField.getText().trim());
            String result = customerManager.addCustomer(c);
            if (result != null) {
                JOptionPane.showMessageDialog(dlg, result, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dlg, "Customer registered!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                refresh();
            }
        });
        btnPanel.add(saveBtn);
        btnPanel.add(new JButton("Cancel") {{ addActionListener(e -> dlg.dispose()); }});
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // Edit Dialog

    private void showEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a customer to edit."); return; }

        String customerId = (String) tableModel.getValueAt(row, 0);
        Customer c = customerManager.findById(customerId);
        if (c == null) return;

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Customer", true);
        dlg.setSize(380, 300);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(16, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 4, 5, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; JTextField nameField = new JTextField(c.getName(), 14); form.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1; JTextField contactField = new JTextField(c.getContactNumber(), 14); form.add(contactField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; JTextField addressField = new JTextField(c.getAddress(), 14); form.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("License Number:"), gbc);
        gbc.gridx = 1; JTextField licenseField = new JTextField(c.getLicenseNumber(), 14); form.add(licenseField, gbc);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Update");
        saveBtn.setBackground(new Color(52, 120, 246));
        saveBtn.setForeground(Color.BLACK);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> {
            if (!Validator.isNotEmpty(nameField.getText()) || !Validator.isValidPhone(contactField.getText())) {
                JOptionPane.showMessageDialog(dlg, "Please enter valid name and phone.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String err = customerManager.updateCustomer(customerId,
                    nameField.getText().trim(), contactField.getText().trim(),
                    addressField.getText().trim(), licenseField.getText().trim());
            if (err != null) {
                JOptionPane.showMessageDialog(dlg, err, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(dlg, "Customer updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a customer to delete."); return; }

        String customerId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete customer " + customerId + "?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String err = customerManager.deleteCustomer(customerId);
        if (err != null) {
            JOptionPane.showMessageDialog(this, err, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Customer deleted.");
            refresh();
        }
    }
}
