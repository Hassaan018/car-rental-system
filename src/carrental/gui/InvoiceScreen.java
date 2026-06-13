package carrental.gui;

import carrental.manager.RentalManager;
import carrental.model.Rental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


 /* InvoiceScreen — look up any rental by ID and display its formatted invoice.
    Also shows a history of all completed rentals. */

public class InvoiceScreen extends JPanel implements Refreshable {

    private final RentalManager rentalManager;

    private JTextField     rentalIdField;
    private JTextArea      invoiceArea;
    private DefaultTableModel historyModel;

    public InvoiceScreen(RentalManager rentalManager) {
        this.rentalManager = rentalManager;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Invoice Viewer");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(18, 24, 38));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setDividerLocation(420);

        // Left: invoice display
        JPanel invoicePanel = new JPanel(new BorderLayout(0, 8));
        invoicePanel.setBackground(new Color(240, 242, 248));

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        searchBar.setBackground(new Color(240, 242, 248));
        searchBar.add(new JLabel("Rental ID:"));
        rentalIdField = new JTextField(16);
        searchBar.add(rentalIdField);

        JButton searchBtn = new JButton("🧾 Generate Invoice");
        searchBtn.setBackground(new Color(52, 120, 246));
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> generateInvoice());
        searchBar.add(searchBtn);

        JButton printBtn = new JButton("🖨 Print / Copy");
        printBtn.addActionListener(e -> copyInvoice());
        searchBar.add(printBtn);

        invoicePanel.add(searchBar, BorderLayout.NORTH);

        invoiceArea = new JTextArea();
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        invoiceArea.setBackground(new Color(248, 250, 255));
        invoiceArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        invoicePanel.add(new JScrollPane(invoiceArea), BorderLayout.CENTER);

        split.setLeftComponent(invoicePanel);

        // Right: completed rentals history
        JPanel historyPanel = new JPanel(new BorderLayout(0, 8));
        historyPanel.setBackground(new Color(240, 242, 248));
        historyPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JLabel histLbl = new JLabel("Completed Rentals History");
        histLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        historyPanel.add(histLbl, BorderLayout.NORTH);

        String[] cols = {"Rental ID", "Customer", "Vehicle", "Days", "Total (PKR)"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable historyTable = new JTable(historyModel);
        historyTable.setRowHeight(24);
        historyTable.getTableHeader().setBackground(new Color(18, 24, 38));
        historyTable.getTableHeader().setForeground(Color.BLACK);
        historyTable.setSelectionBackground(new Color(200, 220, 255));

        // Click row to load invoice
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = historyTable.getSelectedRow();
                if (row >= 0) {
                    String rId = (String) historyModel.getValueAt(row, 0);
                    rentalIdField.setText(rId);
                    generateInvoice();
                }
            }
        });

        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        split.setRightComponent(historyPanel);

        add(split, BorderLayout.CENTER);

        refresh();
    }

    private void generateInvoice() {
        String id = rentalIdField.getText().trim();
        if (id.isEmpty()) {
            invoiceArea.setText("Please enter a Rental ID.");
            return;
        }
        String invoice = rentalManager.generateInvoice(id);
        invoiceArea.setText(invoice);
        invoiceArea.setCaretPosition(0);
    }

    private void copyInvoice() {
        String text = invoiceArea.getText();
        if (text.isEmpty()) { JOptionPane.showMessageDialog(this, "No invoice to copy."); return; }
        invoiceArea.selectAll();
        invoiceArea.copy();
        invoiceArea.select(0, 0);
        JOptionPane.showMessageDialog(this, "Invoice text copied to clipboard!");
    }

    @Override
    public void refresh() {
        historyModel.setRowCount(0);
        List<Rental> completed = rentalManager.getCompletedRentals();
        for (Rental r : completed) {
            historyModel.addRow(new Object[]{
                    r.getRentalId(), r.getCustomerId(), r.getVehicleId(),
                    r.getActualDays(),
                    String.format("%.0f", r.getTotalCost())
            });
        }
    }
}
