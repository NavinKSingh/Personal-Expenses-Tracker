import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FinfoxPage extends JFrame {
    private String currentUser;
    private JButton logoutButton;
    private JButton addExpenseButton;
    private JButton viewExpensesButton;
    private JButton setBudgetButton;
    private JButton settingsButton;
    private Map<String, Double> budgets = new HashMap<>();
    private List<String> categories = new ArrayList<>(List.of("Food", "Transportation", "Entertainment", "Utilities", "Other"));

    public FinfoxPage(String username) {
        this.currentUser = username;
        setTitle("FINFOX - Personal Expense Tracker");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        createUserTable();
        loadBudgets();
        initComponents();

        setVisible(true);
    }

    private void initComponents() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        // Add logo
        ImageIcon img = new ImageIcon("Logo.jpg");
        JLabel image = new JLabel(img);
        image.setHorizontalAlignment(JLabel.CENTER);
        add(image, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("FINFOX");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.WEST);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        addExpenseButton = new JButton("Add Expense");
        viewExpensesButton = new JButton("View Expenses");
        setBudgetButton = new JButton("Set Budget");
        settingsButton = new JButton("Settings");

        centerPanel.add(addExpenseButton);
        centerPanel.add(viewExpensesButton);
        centerPanel.add(setBudgetButton);
        centerPanel.add(settingsButton);

        addExpenseButton.addActionListener(e -> showAddExpenseDialog());
        viewExpensesButton.addActionListener(e -> showViewExpensesDialog());
        setBudgetButton.addActionListener(e -> showSetBudgetDialog());
        settingsButton.addActionListener(e -> showSettingsDialog());

        return centerPanel;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPage();
        }
    }

    private void showAddExpenseDialog() {
        JDialog addExpenseDialog = new JDialog(this, "Add Expense", true);
        addExpenseDialog.setLayout(new GridLayout(5, 2, 10, 10));
    
        JComboBox<String> categoryCombo = new JComboBox<>(categories.toArray(new String[0]));
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField descriptionField = new JTextField();
    
        addExpenseDialog.add(new JLabel("Category:"));
        addExpenseDialog.add(categoryCombo);
        addExpenseDialog.add(new JLabel("Amount:"));
        addExpenseDialog.add(amountField);
        addExpenseDialog.add(new JLabel("Date (YYYY-MM-DD):"));
        addExpenseDialog.add(dateField);
        addExpenseDialog.add(new JLabel("Description:"));
        addExpenseDialog.add(descriptionField);
    
        JButton saveButton = new JButton("Save Expense");
        saveButton.addActionListener(e -> {
            try {
                String category = (String) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = LocalDate.parse(dateField.getText());
                String description = descriptionField.getText();
    
                if (isBudgetExceeded(category, amount)) {
                    int confirm = JOptionPane.showConfirmDialog(this,
                        "This expense exceeds the budget. Do you want to add it anyway?",
                        "Budget Exceeded", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
    
                addExpense(category, amount, date, description);
                addExpenseDialog.dispose();
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding expense: " + ex.getMessage());
            }
        });
    
        addExpenseDialog.add(saveButton);
        addExpenseDialog.pack();
        addExpenseDialog.setVisible(true);
    }

    private void showViewExpensesDialog() {
        JDialog viewExpensesDialog = new JDialog(this, "View Expenses", true);
        viewExpensesDialog.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Category", "Amount", "Date", "Description"}, 0);
        JTable expensesTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(expensesTable);

        try {
            List<Expense> expenses = getExpenses();
            for (Expense expense : expenses) {
                model.addRow(new Object[]{expense.getId(), expense.getCategory(), expense.getAmount(), expense.getDate(), expense.getDescription()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching expenses: " + e.getMessage());
        }

        JPanel buttonPanel = new JPanel();
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        editButton.addActionListener(e -> {
            int selectedRow = expensesTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                showEditExpenseDialog(id, model, selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an expense to edit.");
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = expensesTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) model.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        deleteExpense(id);
                        model.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting expense: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select an expense to delete.");
            }
        });

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        viewExpensesDialog.add(scrollPane, BorderLayout.CENTER);
        viewExpensesDialog.add(buttonPanel, BorderLayout.SOUTH);

        viewExpensesDialog.setSize(600, 400);
        viewExpensesDialog.setVisible(true);
    }

    private void showEditExpenseDialog(int id, DefaultTableModel model, int rowIndex) {
        JDialog editExpenseDialog = new JDialog(this, "Edit Expense", true);
        editExpenseDialog.setLayout(new GridLayout(5, 2, 10, 10));

        JComboBox<String> categoryCombo = new JComboBox<>(categories.toArray(new String[0]));
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField descriptionField = new JTextField();

        categoryCombo.setSelectedItem(model.getValueAt(rowIndex, 1));
        amountField.setText(model.getValueAt(rowIndex, 2).toString());
        dateField.setText(model.getValueAt(rowIndex, 3).toString());
        descriptionField.setText(model.getValueAt(rowIndex, 4).toString());

        editExpenseDialog.add(new JLabel("Category:"));
        editExpenseDialog.add(categoryCombo);
        editExpenseDialog.add(new JLabel("Amount:"));
        editExpenseDialog.add(amountField);
        editExpenseDialog.add(new JLabel("Date (YYYY-MM-DD):"));
        editExpenseDialog.add(dateField);
        editExpenseDialog.add(new JLabel("Description:"));
        editExpenseDialog.add(descriptionField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            try {
                String category = (String) categoryCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                LocalDate date = LocalDate.parse(dateField.getText());
                String description = descriptionField.getText();

                updateExpense(id, category, amount, date, description);
                model.setValueAt(category, rowIndex, 1);
                model.setValueAt(amount, rowIndex, 2);
                model.setValueAt(date, rowIndex, 3);
                model.setValueAt(description, rowIndex, 4);
                editExpenseDialog.dispose();
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating expense: " + ex.getMessage());
            }
        });

        editExpenseDialog.add(saveButton);
        editExpenseDialog.pack();
        editExpenseDialog.setVisible(true);
    }

    private void showSetBudgetDialog() {
        JDialog setBudgetDialog = new JDialog(this, "Set Budget", true);
        setBudgetDialog.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField overallBudgetField = new JTextField(String.valueOf(budgets.getOrDefault("Overall", 0.0)));
        setBudgetDialog.add(new JLabel("Overall Budget:"));
        setBudgetDialog.add(overallBudgetField);

        Map<String, JTextField> categoryBudgetFields = new HashMap<>();
        for (String category : categories) {
            JTextField field = new JTextField(String.valueOf(budgets.getOrDefault(category, 0.0)));
            setBudgetDialog.add(new JLabel(category + " Budget:"));
            setBudgetDialog.add(field);
            categoryBudgetFields.put(category, field);
        }

        JButton saveButton = new JButton("Save Budgets");
        saveButton.addActionListener(e -> {
            try {
                double overallBudget = Double.parseDouble(overallBudgetField.getText());
                budgets.put("Overall", overallBudget);

                for (Map.Entry<String, JTextField> entry : categoryBudgetFields.entrySet()) {
                    double categoryBudget = Double.parseDouble(entry.getValue().getText());
                    budgets.put(entry.getKey(), categoryBudget);
                }

                saveBudgets();
                setBudgetDialog.dispose();
                JOptionPane.showMessageDialog(this, "Budgets saved successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid budget amount. Please enter numbers only.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error saving budgets: " + ex.getMessage());
            }
        });

        setBudgetDialog.add(saveButton);
        setBudgetDialog.pack();
        setBudgetDialog.setVisible(true);
    }

    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField mobileField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField usernameField = new JTextField(currentUser);
        JPasswordField passwordField = new JPasswordField();

        settingsDialog.add(new JLabel("Name:"));
        settingsDialog.add(nameField);
        settingsDialog.add(new JLabel("Mobile:"));
        settingsDialog.add(mobileField);
        settingsDialog.add(new JLabel("Email:"));
        settingsDialog.add(emailField);
        settingsDialog.add(new JLabel("Username:"));
        settingsDialog.add(usernameField);
        settingsDialog.add(new JLabel("New Password:"));
        settingsDialog.add(passwordField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String mobile = mobileField.getText();
                String email = emailField.getText();
                String newUsername = usernameField.getText();
                String newPassword = new String(passwordField.getPassword());

                updateUserInfo(name, mobile, email, newUsername, newPassword);
                settingsDialog.dispose();
                JOptionPane.showMessageDialog(this, "User information updated successfully!");

                if (!currentUser.equals(newUsername)) {
                    currentUser = newUsername;
                    JOptionPane.showMessageDialog(this, "Username changed. Please log in again.");
                    logout();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error updating user information: " + ex.getMessage());
            }
        });

        settingsDialog.add(saveButton);
        settingsDialog.pack();
        settingsDialog.setVisible(true);
    }

    // Database operations
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "Niku@1991");
    }

    private void createUserTable() {
        String tableName = "expenses_" + currentUser;
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "category VARCHAR(50) NOT NULL," +
                "amount DECIMAL(10, 2) NOT NULL," +
                "date DATE NOT NULL," +
                "description VARCHAR(255)" +
                ")";
                try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
               stmt.execute(createTableSQL);
           } catch (SQLException e) {
               e.printStackTrace();
               JOptionPane.showMessageDialog(this, "Error creating user table: " + e.getMessage());
           }
       }
   
       private boolean isBudgetExceeded(String category, double amount) {
           double categoryBudget = budgets.getOrDefault(category, Double.MAX_VALUE);
           double overallBudget = budgets.getOrDefault("Overall", Double.MAX_VALUE);
           
           double categoryTotal = 0;
           double overallTotal = 0;
           
           try {
               List<Expense> expenses = getExpenses();
               for (Expense expense : expenses) {
                   if (expense.getCategory().equals(category)) {
                       categoryTotal += expense.getAmount();
                   }
                   overallTotal += expense.getAmount();
               }
           } catch (SQLException e) {
               e.printStackTrace();
               JOptionPane.showMessageDialog(this, "Error fetching expenses: " + e.getMessage());
           }
           
           return (categoryTotal + amount > categoryBudget) || (overallTotal + amount > overallBudget);
       }
   
       private void addExpense(String category, double amount, LocalDate date, String description) throws SQLException {
           String tableName = "expenses_" + currentUser;
           String sql = "INSERT INTO " + tableName + " (category, amount, date, description) VALUES (?, ?, ?, ?)";
           try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setString(1, category);
               pstmt.setDouble(2, amount);
               pstmt.setDate(3, java.sql.Date.valueOf(date));
               pstmt.setString(4, description);
               pstmt.executeUpdate();
           }
       }
   
       private List<Expense> getExpenses() throws SQLException {
           List<Expense> expenses = new ArrayList<>();
           String tableName = "expenses_" + currentUser;
           String sql = "SELECT * FROM " + tableName + " ORDER BY date DESC";
           try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
               while (rs.next()) {
                   Expense expense = new Expense(
                       rs.getInt("id"),
                       rs.getString("category"),
                       rs.getDouble("amount"),
                       rs.getDate("date").toLocalDate(),
                       rs.getString("description")
                   );
                   expenses.add(expense);
               }
           }
           return expenses;
       }
   
       private void updateExpense(int id, String category, double amount, LocalDate date, String description) throws SQLException {
           String tableName = "expenses_" + currentUser;
           String sql = "UPDATE " + tableName + " SET category = ?, amount = ?, date = ?, description = ? WHERE id = ?";
           try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setString(1, category);
               pstmt.setDouble(2, amount);
               pstmt.setDate(3, java.sql.Date.valueOf(date));
               pstmt.setString(4, description);
               pstmt.setInt(5, id);
               pstmt.executeUpdate();
           }
       }
   
       private void deleteExpense(int id) throws SQLException {
           String tableName = "expenses_" + currentUser;
           String sql = "DELETE FROM " + tableName + " WHERE id = ?";
           try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setInt(1, id);
               pstmt.executeUpdate();
           }
       }
   
       private void loadBudgets() {
           String sql = "SELECT category, amount FROM budgets WHERE username = ?";
           try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setString(1, currentUser);
               try (ResultSet rs = pstmt.executeQuery()) {
                   while (rs.next()) {
                       budgets.put(rs.getString("category"), rs.getDouble("amount"));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
               JOptionPane.showMessageDialog(this, "Error loading budgets: " + e.getMessage());
           }
       }
   
       private void saveBudgets() throws SQLException {
           String deleteSql = "DELETE FROM budgets WHERE username = ?";
           String insertSql = "INSERT INTO budgets (username, category, amount) VALUES (?, ?, ?)";
           try (Connection conn = getConnection();
                PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
               conn.setAutoCommit(false);
               
               deleteStmt.setString(1, currentUser);
               deleteStmt.executeUpdate();
   
               for (Map.Entry<String, Double> entry : budgets.entrySet()) {
                   insertStmt.setString(1, currentUser);
                   insertStmt.setString(2, entry.getKey());
                   insertStmt.setDouble(3, entry.getValue());
                   insertStmt.executeUpdate();
               }
   
               conn.commit();
           } catch (SQLException e) {
               e.printStackTrace();
               JOptionPane.showMessageDialog(this, "Error saving budgets: " + e.getMessage());
           }
       }
   
       private void updateUserInfo(String name, String mobile, String email, String newUsername, String newPassword) throws SQLException {
           String sql = "UPDATE users SET name = ?, mobile = ?, email = ?, username = ?, password = ? WHERE username = ?";
           try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
               pstmt.setString(1, name);
               pstmt.setString(2, mobile);
               pstmt.setString(3, email);
               pstmt.setString(4, newUsername);
               pstmt.setString(5, newPassword);
               pstmt.setString(6, currentUser);
               pstmt.executeUpdate();
   
               if (!currentUser.equals(newUsername)) {
                   String renameTableSql = "RENAME TABLE expenses_" + currentUser + " TO expenses_" + newUsername;
                   try (Statement stmt = conn.createStatement()) {
                       stmt.execute(renameTableSql);
                   }
               }
           }
       }
   
       public static void main(String[] args) {
           SwingUtilities.invokeLater(() -> new FinfoxPage("testuser"));
       }
   }
   
   class Expense {
       private int id;
       private String category;
       private double amount;
       private LocalDate date;
       private String description;
   
       public Expense(int id, String category, double amount, LocalDate date, String description) {
           this.id = id;
           this.category = category;
           this.amount = amount;
           this.date = date;
           this.description = description;
       }
   
       // Getters
       public int getId() { return id; }
       public String getCategory() { return category; }
       public double getAmount() { return amount; }
       public LocalDate getDate() { return date; }
       public String getDescription() { return description; }
   }