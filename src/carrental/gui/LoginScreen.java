package carrental.gui;

import carrental.manager.StaffManager;
import carrental.model.Staff;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;


 // LoginScreen — simple username/password authentication for staff access.

public class LoginScreen extends JDialog {

    private final StaffManager      staffManager;
    private final Consumer<Staff>   onSuccess;

    private JTextField  usernameField;
    private JPasswordField passwordField;
    private JLabel      statusLabel;

    public LoginScreen(StaffManager staffManager, Consumer<Staff> onSuccess) {
        this.staffManager = staffManager;
        this.onSuccess    = onSuccess;
        buildUI();
    }

    private void buildUI() {
        setTitle("Drive Easy Car Rental System — Login");
        setModal(true);
        setSize(420, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(18, 24, 38));

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(18, 24, 38));
        header.setBorder(BorderFactory.createEmptyBorder(24, 0, 10, 0));
        JLabel logo = new JLabel("🚗  Drive Easy Car Rentals");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(new Color(255, 200, 50));
        header.add(logo);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(28, 36, 54));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setForeground(Color.LIGHT_GRAY);
        form.add(userLbl, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        form.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setForeground(Color.LIGHT_GRAY);
        form.add(passLbl, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        form.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(255, 90, 90));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        form.add(statusLabel, gbc);

        gbc.gridy = 3;
        JButton loginBtn = new JButton("Login →");
        loginBtn.setBackground(new Color(255, 200, 50));
        loginBtn.setForeground(new Color(18, 24, 38));
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(e -> doLogin());
        form.add(loginBtn, gbc);

        root.add(form, BorderLayout.CENTER);

        // Default credentials hint
        JLabel hint = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        hint.setForeground(new Color(100, 110, 130));
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        root.add(hint, BorderLayout.SOUTH);

        setContentPane(root);

        // Allow Enter key
        passwordField.addActionListener(e -> doLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        Staff staff = staffManager.authenticate(username, password);
        if (staff != null) {
            dispose();
            onSuccess.accept(staff);
        } else {
            statusLabel.setText("Invalid username or password.");
            passwordField.setText("");
        }
    }
}
