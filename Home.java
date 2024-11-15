import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {
    public static Object users;

    Home() {
        // Set up the Home frame
        this.setTitle("FinFox");
        this.setSize(2056, 1028);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);  // Center the frame on the screen

        // Set the background color
        this.getContentPane().setBackground(new Color(255, 255, 255));

        // Set the app icon
        ImageIcon logo = new ImageIcon("Logo.jpg");
        this.setIconImage(logo.getImage());

        // Set up images and labels
        ImageIcon img = new ImageIcon("Logo.jpg");  // Logo
        JLabel image = new JLabel();
        image.setIcon(img);
        image.setHorizontalAlignment(JLabel.CENTER);
        image.setVerticalTextPosition(JLabel.CENTER);
        image.setBounds(620, 0, 300, 350);

        ImageIcon img2 = new ImageIcon("Budget.jpg");  // Budget image
        JLabel image2 = new JLabel();
        image2.setIcon(img2);
        image2.setHorizontalAlignment(JLabel.CENTER);
        image2.setVerticalTextPosition(JLabel.CENTER);
        image2.setBounds(940, 230, 700, 600);

        ImageIcon img3 = new ImageIcon("FlowChart.jpg");  // FlowChart Image
        JLabel image3 = new JLabel();
        image3.setIcon(img3);
        image3.setHorizontalAlignment(JLabel.CENTER);
        image3.setVerticalTextPosition(JLabel.CENTER);
        image3.setBounds(0, 350, 360, 500);

        ImageIcon img4 = new ImageIcon("Pig_Coin.jpg");  // Pig Coin image
        JLabel image4 = new JLabel();
        image4.setIcon(img4);
        image4.setIcon(new ImageIcon(img4.getImage().getScaledInstance(300, 300, java.awt.Image.SCALE_SMOOTH)));
        image4.setHorizontalAlignment(JLabel.CENTER);
        image4.setVerticalTextPosition(JLabel.CENTER);
        image4.setBounds(90, 10, 300, 300);

        ImageIcon img5 = new ImageIcon("Invest_2.jpg");  // Invest Image
        JLabel image5 = new JLabel();
        image5.setIcon(img5);
        image5.setIcon(new ImageIcon(img5.getImage().getScaledInstance(150, 250, java.awt.Image.SCALE_SMOOTH)));
        image5.setHorizontalAlignment(JLabel.CENTER);
        image5.setVerticalTextPosition(JLabel.CENTER);
        image5.setBounds(400, 550, 150, 250);

        ImageIcon img6 = new ImageIcon("Spend.jpg");  // Spend Image
        JLabel image6 = new JLabel();
        image6.setIcon(img6);
        image6.setIcon(new ImageIcon(img6.getImage().getScaledInstance(250, 250, java.awt.Image.SCALE_SMOOTH)));
        image6.setHorizontalAlignment(JLabel.CENTER);
        image6.setVerticalTextPosition(JLabel.CENTER);
        image6.setBounds(1200, 0, 250, 250);

        JLabel finfox = new JLabel("FinFox");  // FinFox Label
        finfox.setHorizontalTextPosition(JLabel.CENTER);
        finfox.setVerticalTextPosition(JLabel.BOTTOM);
        finfox.setForeground(new Color(67, 141, 67));
        finfox.setFont(new Font("Algerian", Font.PLAIN, 90));
        finfox.setVerticalAlignment(JLabel.CENTER);
        finfox.setHorizontalAlignment(JLabel.CENTER);
        finfox.setBounds(620, 80, 325, 650);

        // Adding buttons
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(675, 500, 200, 50);
        loginButton.setBackground(new Color(67, 141, 67));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.PLAIN, 20));

        JButton newUserButton = new JButton("New User");
        newUserButton.setBounds(675, 600, 200, 50);
        newUserButton.setBackground(new Color(67, 141, 67));
        newUserButton.setForeground(Color.WHITE);
        newUserButton.setFont(new Font("Arial", Font.PLAIN, 20));

        // Add action listeners for button functionality
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginPage();
                dispose();  // Close the Home frame
            }
        });

        newUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new NewUserPage();
                dispose();  // Close the Home frame
            }
        });

        // Add components to the frame
        this.setLayout(null);
        this.add(image);
        this.add(image2);
        this.add(image3);
        this.add(image4);
        this.add(image5);
        this.add(image6);
        this.add(loginButton);
        this.add(newUserButton);
        this.add(finfox);

        this.setVisible(true);  // Ensure the frame is visible
    }

    public static void main(String[] args) {
        // Ensure the Home frame is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Home();  // Show the Home frame
            }
        });
    }
}