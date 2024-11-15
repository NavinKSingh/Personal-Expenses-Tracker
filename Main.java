import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // Create the Main JFrame for the application (this could be a splash screen or initial setup)
        JFrame frame = new JFrame();  // Define the JFrame
        frame.setTitle("FinFox");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2056, 1028);
        frame.setVisible(true);

        // Set the icon and background color for the frame
        ImageIcon image = new ImageIcon("Logo.jpg");
        frame.setIconImage(image.getImage()); 
        frame.getContentPane().setBackground(new Color(0x438D43));

        // Instantiate and display the Home page
        new Home();  // Open the Home page as a new JFrame

        // Optionally, close the main frame after opening the Home page
        frame.dispose();
    }
}