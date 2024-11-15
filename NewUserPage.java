import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class NewUserPage extends JFrame {
    NewUserPage() {
        this.setTitle("New User Registration");
        this.setSize(2056, 1028);
        this.setLayout(null);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(255, 255, 255));

        ImageIcon img = new ImageIcon("Logo.jpg");   //Logo
        JLabel image = new JLabel();
        image.setIcon(img);
        image.setHorizontalAlignment(JLabel.CENTER);
        image.setVerticalTextPosition(JLabel.CENTER);
        image.setBounds(620, 0, 300, 350);
        this.add(image);

        ImageIcon img2 = new ImageIcon("didi.jpg");   //Logo
        JLabel image2 = new JLabel();
        image2.setIcon(img2);
        image2.setHorizontalAlignment(JLabel.CENTER);
        image2.setVerticalTextPosition(JLabel.CENTER);
        image2.setIcon(new ImageIcon(img2.getImage().getScaledInstance(550, 550, java.awt.Image.SCALE_SMOOTH)));
        image2.setBounds(0, 300, 550, 550);
        this.add(image2);

        ImageIcon img3 = new ImageIcon("saving.jpg");   //Logo
        JLabel image3 = new JLabel();
        image3.setIcon(img3);
        image3.setHorizontalAlignment(JLabel.CENTER);
        image3.setVerticalTextPosition(JLabel.CENTER);
        image3.setIcon(new ImageIcon(img3.getImage().getScaledInstance(200, 250, java.awt.Image.SCALE_SMOOTH)));
        image3.setBounds(1230, 520, 250, 250);
        this.add(image3);

        // Create username label and text field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        usernameLabel.setForeground(new Color(67, 141, 67));
        usernameLabel.setBounds(600, 350, 200, 40);  // Adjusting positions and sizes
        this.add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 24));
        usernameField.setBounds(800, 350, 300, 40);  // Adjusting positions and sizes
        this.add(usernameField);

        // Create password label and password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        passwordLabel.setBounds(600, 450, 200, 40);  // Adjusting positions and sizes
        passwordLabel.setForeground(new Color(67, 141, 67));
        this.add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 24));
        passwordField.setBounds(800, 450, 300, 40);  // Adjusting positions and sizes
        this.add(passwordField);

        // Create confirm password label and password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        confirmPasswordLabel.setBounds(550, 550, 250, 40);  // Adjusting positions and sizes
        confirmPasswordLabel.setForeground(new Color(67, 141, 67));
        this.add(confirmPasswordLabel);

        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 24));
        confirmPasswordField.setBounds(800, 550, 300, 40);  // Adjusting positions and sizes
        this.add(confirmPasswordField);

        // Create submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.PLAIN, 24));
        submitButton.setBounds(700, 650, 150, 50);  // Adjusting position and size
        submitButton.setBackground(new Color(67, 141, 67));
        submitButton.setForeground(Color.WHITE);
        this.add(submitButton);

        // Add action listener to handle form submission
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Validate the input
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields are required.");
                } else if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.");
                } else {
                    // Database connection
                    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "Niku@1991")) {
                        // Check if the username already exists
                        String checkUserQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
                        PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery);
                        checkUserStmt.setString(1, username);
                        ResultSet rs = checkUserStmt.executeQuery();

                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(null, "Username already exists.");
                        } else {
                            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                            PreparedStatement pstmt = conn.prepareStatement(sql);
                            pstmt.setString(1, username);
                            pstmt.setString(2, password);
                            pstmt.executeUpdate();
                            JOptionPane.showMessageDialog(null, "Registration successful!");
                            new LoginPage();  // Redirect to login page
                            dispose();  // Close the registration form
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                    }
                }
            }
        });
    }
}