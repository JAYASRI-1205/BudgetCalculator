import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BudgetCalculator extends JFrame {

    // Components for UI
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private JTextField categoryField, amountField, loanStatusField;
    private JComboBox<String> typeComboBox, timePeriodComboBox;

    // Data storage
    private ArrayList<Transaction> transactions;

    public BudgetCalculator() {
        transactions = new ArrayList<>();

        setTitle("BudgetCalculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 600);

        // Top Panel for input
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));

        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Type:"));
        typeComboBox = new JComboBox<>(new String[]{"Income", "Expense", "Loan"});
        inputPanel.add(typeComboBox);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(new JLabel("Loan Status (if Loan):"));
        loanStatusField = new JTextField();
        inputPanel.add(loanStatusField);

        JButton addButton = new JButton("Add Transaction");
        inputPanel.add(addButton);
        addButton.addActionListener(new AddTransactionListener());

        JButton deleteButton = new JButton("Delete Transaction");
        inputPanel.add(deleteButton);
        deleteButton.addActionListener(new DeleteTransactionListener());

        add(inputPanel, BorderLayout.NORTH);

        // Center Panel for table
        tableModel = new DefaultTableModel(new Object[]{"Category", "Type", "Amount", "Loan Status", "CIBIL Score"}, 0);
        transactionsTable = new JTable(tableModel);
        add(new JScrollPane(transactionsTable), BorderLayout.CENTER);

        // Bottom Panel for summary
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        summaryPanel.setLayout(new FlowLayout());

        summaryPanel.add(new JLabel("Select Period:"));
        timePeriodComboBox = new JComboBox<>(new String[]{"Monthly", "Yearly"});
        summaryPanel.add(timePeriodComboBox);

        JButton summaryButton = new JButton("Show Summary");
        summaryPanel.add(summaryButton);
        summaryButton.addActionListener(new SummaryListener());

        add(summaryPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addTransaction(String category, String type, double amount, String loanStatus) {
        String calculatedCibilScore = calculateCibilScore(type, amount, loanStatus);
        transactions.add(new Transaction(category, type, amount, loanStatus, calculatedCibilScore));
        tableModel.addRow(new Object[]{category, type, amount, loanStatus, calculatedCibilScore});
    }

    private void deleteTransaction() {
        int selectedRow = transactionsTable.getSelectedRow();
        if (selectedRow >= 0) {
            transactions.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "No row selected!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String calculateCibilScore(String type, double amount, String loanStatus) {
        if (!type.equalsIgnoreCase("Loan")) {
            return "N/A"; // CIBIL score is only relevant for loans
        }

        int cibilScore = 750;

        if (loanStatus.equalsIgnoreCase("Approved")) {
            cibilScore += 20;
        } else if (loanStatus.equalsIgnoreCase("Pending")) {
            cibilScore -= 10;
        }

        if (amount > 500000) {
            cibilScore -= 30;
        } else if (amount > 200000) {
            cibilScore -= 20;
        }

        return String.valueOf(Math.max(300, Math.min(900, cibilScore)));
    }

    private void showSummary() {
        String period = (String) timePeriodComboBox.getSelectedItem();
        double totalIncome = 0, totalExpense = 0, netSavings = 0;

        for (Transaction transaction : transactions) {
            if (transaction.type.equalsIgnoreCase("Income")) {
                totalIncome += transaction.amount;
            } else if (transaction.type.equalsIgnoreCase("Expense")) {
                totalExpense += transaction.amount;
            }
        }

        netSavings = totalIncome - totalExpense;

        String summaryMessage = "Summary (" + period + "):\n" +
                "Total Income: " + totalIncome + "\n" +
                "Total Expenses: " + totalExpense + "\n" +
                "Net Savings: " + netSavings;

        JOptionPane.showMessageDialog(this, summaryMessage, "Financial Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private class AddTransactionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                String category = categoryField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String loanStatus = loanStatusField.getText();

                addTransaction(category, type, amount, loanStatus);

                categoryField.setText("");
                amountField.setText("");
                loanStatusField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(BudgetCalculator.this, "Invalid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteTransactionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            deleteTransaction();
        }
    }

    private class SummaryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showSummary();
        }
    }

    public static void main(String[] args) {
        new BudgetCalculator();
    }

    // Transaction class to represent each record
    static class Transaction {
        String category;
        String type;
        double amount;
        String loanStatus;
        String cibilScore;

        public Transaction(String category, String type, double amount, String loanStatus, String cibilScore) {
            this.category = category;
            this.type = type;
            this.amount = amount;
            this.loanStatus = loanStatus;
            this.cibilScore = cibilScore;
        }
    }
}
