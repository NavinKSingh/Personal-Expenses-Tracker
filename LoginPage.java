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

class LoginPage extends JFrame {
    LoginPage() {
        this.setTitle("Login Page");
        this.setSize(2056, 1028);
        this.setVisible(true);
        this.setLayout(null);
        this.setVisible(true);
        this.getContentPane().setBackground(new Color(255,255,255));

        ImageIcon img = new ImageIcon("Logo.jpg");   //Logo
        JLabel image = new JLabel();
        image.setIcon(img);
        image.setHorizontalAlignment(JLabel.CENTER);
        image.setVerticalTextPosition(JLabel.CENTER);
        image.setBounds(650, 0, 300, 350);
        this.add(image);

        ImageIcon img2 = new ImageIcon("Woman_Chair.jpg");   //Woman Chair
        JLabel image2 = new JLabel();
        image2.setIcon(img2);
        image2.setHorizontalAlignment(JLabel.CENTER);
        image2.setVerticalTextPosition(JLabel.CENTER);
        image2.setBounds(0, 400, 400, 400);
        this.add(image2);

        ImageIcon img3 = new ImageIcon("ZeroDebt.jpg");   //Zero Debt
        JLabel image3 = new JLabel();
        image3.setIcon(img3);
        image3.setHorizontalAlignment(JLabel.CENTER);
        image3.setVerticalTextPosition(JLabel.CENTER);
        image3.setBounds(1200, 400, 250, 250);
        this.add(image3);

        JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(new Font("Arial", Font.PLAIN, 24));
            usernameLabel.setForeground(new Color(67, 141, 67));
            usernameLabel.setBounds(600, 450, 200, 40);  // Adjusting positions and sizes
            this.add(usernameLabel);

            JTextField usernameField = new JTextField();
            usernameField.setFont(new Font("Arial", Font.PLAIN, 24));
            usernameField.setBounds(800, 450, 300, 40);  // Adjusting positions and sizes
            this.add(usernameField);

            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(new Font("Arial", Font.PLAIN, 24));
            passwordLabel.setForeground(new Color(67, 141, 67));
            passwordLabel.setBounds(600, 550, 200, 40);  // Adjusting positions and sizes
            this.add(passwordLabel);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setFont(new Font("Arial", Font.PLAIN, 24));
            passwordField.setBounds(800, 550, 300, 40);  // Adjusting positions and sizes
            this.add(passwordField);

            JButton loginButton = new JButton("Login");
            loginButton.setFont(new Font("Arial", Font.PLAIN, 24));
            loginButton.setBounds(750, 650, 150, 50);
            loginButton.setBackground(new Color(67, 141, 67));
            loginButton.setForeground(Color.WHITE);
            this.add(loginButton);

            // Add action listener for login
            loginButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String username = usernameField.getText();
                    String password = new String(passwordField.getPassword());
    
                    // Validate input
                    if (username.isEmpty() || password.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Username and Password cannot be empty.");
                        return;
                    }
    
                    try {
                        // Database connection
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "Niku@1991");
                        String sql = "SELECT password FROM users WHERE username = ?";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, username);
                        ResultSet rs = pstmt.executeQuery();
    
                        if (rs.next()) {
                            String storedPassword = rs.getString("password");
    
                            if (storedPassword.equals(password)) {
                                JOptionPane.showMessageDialog(null, "Login successful!");
                                // Open FINFOX page
                                new FinfoxPage(username); // Assuming FinfoxPage is another JFrame for the app's main page
                                dispose(); // Close the login page
                            } else {
                                JOptionPane.showMessageDialog(null, "Incorrect password.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "User not found. Redirecting to registration.");
                            new NewUserPage(); // Redirect to New User Registration page
                            dispose(); // Close the login page
                        }
    
                        conn.close();
    
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                    }
                }
            });
        }
    }
