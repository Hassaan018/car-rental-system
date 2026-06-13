package carrental.gui;

import carrental.manager.CustomerManager;
import carrental.manager.RentalManager;
import carrental.manager.VehicleManager;
import carrental.model.Rental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/* ReturnScreen — process vehicle returns.
    Let's staff select an active rental, confirm return date, and finalize invoice. */

public class ReturnScreen extends JPanel implements Refreshable {

    private final RentalManager   rentalManager;
    private final VehicleManager  vehicleManager;
    private final CustomerManager customerManager;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField returnDateField;
    private JTextArea  notesArea;
    private JLabel     invoicePreviewLabel;

    public ReturnScreen(RentalManager rm, VehicleManager vm, CustomerManager cm) {
        this.rentalManager   = rm;
        this.vehicleManager  = vm;
        this.customerManager = cm;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Return Processing");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(18, 24, 38));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(500);

        // Active rentals table
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(new Color(240, 242, 248));
        JLabel tableLbl = new JLabel("Select an Active Rental:");
        tableLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        tablePanel.add(tableLbl, BorderLayout.NORTH);

        String[] cols = {"Rental ID", "Customer ID", "Vehicle ID", "Start Date", "Expected Return", "Amount (PKR)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setBackground(new Color(18, 24, 38));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(200, 220, 255));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        split.setLeftComponent(tablePanel);

        // Return form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 235), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 4, 8, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel formTitle = new JLabel("Process Return");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        formPanel.add(formTitle, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Actual Return Date:"), gbc);
        gbc.gridx = 1;
        returnDateField = new JTextField(LocalDate.now().toString(), 14);
        formPanel.add(returnDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Notes (optional):"), gbc);
        gbc.gridx = 1;
        notesArea = new JTextArea(3, 14);
        notesArea.setLineWrap(true);
        formPanel.add(new JScrollPane(notesArea), gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        invoicePreviewLabel = new JLabel(" ");
        invoicePreviewLabel.setForeground(new Color(40, 120, 40));
        invoicePreviewLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        formPanel.add(invoicePreviewLabel, gbc);

        gbc.gridy = 4;
        JButton processBtn = new JButton("🔄  Process Return & Generate Invoice");
        processBtn.setBackground(new Color(220, 100, 50));
        processBtn.setForeground(Color.BLACK);
        processBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        processBtn.setFocusPainted(false);
        processBtn.setBorderPainted(false);
        processBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        processBtn.addActionListener(e -> processReturn());
        formPanel.add(processBtn, gbc);

        split.setRightComponent(formPanel);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    private void processReturn() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an active rental from the table.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rentalId = (String) tableModel.getValueAt(row, 0);

        try {
            LocalDate returnDate = LocalDate.parse(returnDateField.getText().trim());
            Rental rental = rentalManager.findById(rentalId);
            if (rental == null) { JOptionPane.showMessageDialog(this, "Rental not found."); return; }

            if (!returnDate.isAfter(rental.getStartDate().minusDays(1))) {
                JOptionPane.showMessageDialog(this, "Return date cannot be before the rental start date.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Rental completed = rentalManager.processReturn(rentalId, returnDate, notesArea.getText().trim());
            if (completed == null) {
                JOptionPane.showMessageDialog(this, "Failed to process return. Is this rental still active?",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Show invoice
            String invoice = rentalManager.generateInvoice(rentalId);
            JTextArea invoiceArea = new JTextArea(invoice);
            invoiceArea.setEditable(false);
            invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane scroll = new JScrollPane(invoiceArea);
            scroll.setPreferredSize(new Dimension(520, 380));

            JOptionPane.showMessageDialog(this, scroll, "Invoice — " + rentalId, JOptionPane.PLAIN_MESSAGE);
            refresh();

        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        tableModel.setRowCount(0);
        for (Rental r : rentalManager.getActiveRentals()) {
            tableModel.addRow(new Object[]{
                    r.getRentalId(), r.getCustomerId(), r.getVehicleId(),
                    r.getStartDate(), r.getExpectedReturnDate(),
                    String.format("%.0f", r.getTotalCost())
            });
        }
    }
}
