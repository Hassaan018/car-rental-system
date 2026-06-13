package carrental.gui;

import carrental.manager.CustomerManager;
import carrental.manager.RentalManager;
import carrental.manager.StaffManager;
import carrental.manager.VehicleManager;
import carrental.model.Staff;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {

    // Shared managers (passed to each screen)
    private VehicleManager  vehicleManager;
    private CustomerManager customerManager;
    private RentalManager   rentalManager;
    private StaffManager    staffManager;
    private Staff           loggedInStaff;

    // UI
    private JPanel  sidebarPanel;
    private JPanel  contentPanel;
    private CardLayout cardLayout;

    // Screen names for CardLayout
    public static final String SCREEN_DASHBOARD = "Dashboard";
    public static final String SCREEN_VEHICLES  = "Vehicles";
    public static final String SCREEN_CUSTOMERS = "Customers";
    public static final String SCREEN_RENTALS   = "Rentals";
    public static final String SCREEN_RETURNS   = "Returns";
    public static final String SCREEN_INVOICE   = "Invoice";

    public MainApp() {

        // Initialize managers
        vehicleManager  = new VehicleManager();
        customerManager = new CustomerManager();
        rentalManager   = new RentalManager(vehicleManager, customerManager);
        staffManager    = new StaffManager();

        // Show Login first
        showLoginScreen();
    }

    // Login

    private void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(staffManager, this::onLoginSuccess);
        loginScreen.setVisible(true);
    }

    private void onLoginSuccess(Staff staff) {
        this.loggedInStaff = staff;
        buildMainWindow();
        setVisible(true);
    }

    // Main Window

    private void buildMainWindow() {
        setTitle("Drive Easy Car Rental System  |  Logged in: " + loggedInStaff.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        buildSidebar();
        buildContentArea();

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Show dashboard by default
        switchScreen(SCREEN_DASHBOARD);
    }

    private void buildSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(18, 24, 38));
        sidebarPanel.setPreferredSize(new Dimension(190, 0));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // App title
        JLabel titleLabel = new JLabel("<html><center>🚗 Drive Easy<br/>Car Rentals</center></html>");
        titleLabel.setForeground(new Color(255, 200, 50));
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebarPanel.add(titleLabel);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 70, 90));
        sep.setMaximumSize(new Dimension(180, 1));
        sidebarPanel.add(sep);
        sidebarPanel.add(Box.createVerticalStrut(10));

        // Nav buttons
        addNavButton("📊  Dashboard",  SCREEN_DASHBOARD);
        addNavButton("🚙  Vehicles",   SCREEN_VEHICLES);
        addNavButton("👤  Customers",  SCREEN_CUSTOMERS);
        addNavButton("📋  New Rental", SCREEN_RENTALS);
        addNavButton("🔄  Returns",    SCREEN_RETURNS);
        addNavButton("🧾  Invoice",    SCREEN_INVOICE);

        sidebarPanel.add(Box.createVerticalGlue());

        // Logout button
        JButton logoutBtn = createSidebarButton("⬅  Logout");
        logoutBtn.setForeground(new Color(255, 100, 100));
        logoutBtn.addActionListener(e -> {
            dispose();
            new MainApp();
        });
        sidebarPanel.add(logoutBtn);
        sidebarPanel.add(Box.createVerticalStrut(10));
    }

    private void addNavButton(String label, String screenName) {
        JButton btn = createSidebarButton(label);
        btn.addActionListener(e -> switchScreen(screenName));
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(4));
    }

    private JButton createSidebarButton(String label) {
        JButton btn = new JButton(label);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(170, 38));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(new Color(30, 38, 56));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void buildContentArea() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240, 242, 248));

        contentPanel.add(new DashboardScreen(vehicleManager, rentalManager, customerManager),
                SCREEN_DASHBOARD);
        contentPanel.add(new VehicleScreen(vehicleManager),
                SCREEN_VEHICLES);
        contentPanel.add(new CustomerScreen(customerManager),
                SCREEN_CUSTOMERS);
        contentPanel.add(new RentalScreen(vehicleManager, customerManager, rentalManager, loggedInStaff),
                SCREEN_RENTALS);
        contentPanel.add(new ReturnScreen(rentalManager, vehicleManager, customerManager),
                SCREEN_RETURNS);
        contentPanel.add(new InvoiceScreen(rentalManager),
                SCREEN_INVOICE);
    }

    public void switchScreen(String screenName) {
        cardLayout.show(contentPanel, screenName);

        // Refresh screens that display live data
        Component[] comps = contentPanel.getComponents();
        for (Component c : comps) {
            if (c.isVisible() && c instanceof Refreshable) {
                ((Refreshable) c).refresh();
            }
        }
    }

    // Main - Entry Point

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainApp();
        });
    }
}
