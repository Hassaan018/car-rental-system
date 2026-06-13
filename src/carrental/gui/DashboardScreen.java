package carrental.gui;

import carrental.manager.CustomerManager;
import carrental.manager.RentalManager;
import carrental.manager.VehicleManager;
import carrental.model.Rental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


 // DashboardScreen — summary cards showing fleet status and active rentals.

public class DashboardScreen extends JPanel implements Refreshable {

    private final VehicleManager  vehicleManager;
    private final RentalManager   rentalManager;
    private final CustomerManager customerManager;

    // Stat labels
    private JLabel totalVehiclesVal;
    private JLabel availableVal;
    private JLabel rentedVal;
    private JLabel totalCustomersVal;
    private JLabel activeRentalsVal;
    private JLabel revenueVal;

    // Active rentals table
    private DefaultTableModel tableModel;

    public DashboardScreen(VehicleManager vm, RentalManager rm, CustomerManager cm) {
        this.vehicleManager  = vm;
        this.rentalManager   = rm;
        this.customerManager = cm;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 248));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Page title
        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(18, 24, 38));
        add(title, BorderLayout.NORTH);

        // Cards panel
        JPanel cards = new JPanel(new GridLayout(2, 3, 14, 14));
        cards.setBackground(new Color(240, 242, 248));

        totalVehiclesVal  = addCard(cards, "Total Vehicles",  "0", new Color(52, 120, 246));
        availableVal      = addCard(cards, "Available",       "0", new Color(40, 167, 90));
        rentedVal         = addCard(cards, "Rented Out",      "0", new Color(220, 100, 50));
        totalCustomersVal = addCard(cards, "Customers",       "0", new Color(130, 80, 220));
        activeRentalsVal  = addCard(cards, "Active Rentals",  "0", new Color(20, 160, 180));
        revenueVal        = addCard(cards, "Total Revenue",   "PKR 0", new Color(180, 140, 30));

        add(cards, BorderLayout.CENTER);

        // Active rentals table
        JPanel tablePanel = new JPanel(new BorderLayout(0, 6));
        tablePanel.setBackground(new Color(240, 242, 248));

        JLabel tableTitle = new JLabel("Active Rentals");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        tableTitle.setForeground(new Color(30, 40, 60));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Rental ID", "Customer ID", "Vehicle ID", "Start Date", "Return Date", "Amount (PKR)"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(new Color(18, 24, 38));
        table.getTableHeader().setForeground(Color.BLACK);
        table.setSelectionBackground(new Color(200, 220, 255));
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(0, 180));
        tablePanel.add(scroll, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.SOUTH);

        refresh();
    }

    private JLabel addCard(JPanel parent, String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 235), 1),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)));

        JLabel topBorder = new JLabel();
        topBorder.setOpaque(true);
        topBorder.setBackground(accent);
        topBorder.setPreferredSize(new Dimension(0, 4));
        card.add(topBorder, BorderLayout.NORTH);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(100, 110, 130));
        card.add(titleLbl, BorderLayout.CENTER);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLbl.setForeground(accent);
        card.add(valueLbl, BorderLayout.SOUTH);

        parent.add(card);
        return valueLbl;
    }

    @Override
    public void refresh() {
        totalVehiclesVal.setText(String.valueOf(vehicleManager.getTotalVehicleCount()));
        availableVal.setText(String.valueOf(vehicleManager.getAvailableCount()));
        rentedVal.setText(String.valueOf(vehicleManager.getRentedCount()));
        totalCustomersVal.setText(String.valueOf(customerManager.getTotalCustomerCount()));

        List<Rental> active = rentalManager.getActiveRentals();
        activeRentalsVal.setText(String.valueOf(active.size()));
        revenueVal.setText(String.format("PKR %.0f", rentalManager.getTotalRevenue()));

        tableModel.setRowCount(0);
        for (Rental r : active) {
            tableModel.addRow(new Object[]{
                    r.getRentalId(), r.getCustomerId(), r.getVehicleId(),
                    r.getStartDate(), r.getExpectedReturnDate(),
                    String.format("%.0f", r.getTotalCost())
            });
        }
    }
}
