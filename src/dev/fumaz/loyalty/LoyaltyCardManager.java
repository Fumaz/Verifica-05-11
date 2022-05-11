package dev.fumaz.loyalty;

import dev.fumaz.loyalty.card.GoldLoyaltyCard;
import dev.fumaz.loyalty.card.LoyaltyCard;
import dev.fumaz.loyalty.card.PlatinumLoyaltyCard;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.Point;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LoyaltyCardManager extends JFrame {

    private final static String FILE_NAME = "loyalty_cards.csv";

    private final List<LoyaltyCard> cards;

    private JPanel contentPanel;
    private JTable cardsTable;
    private JTextField createFirstNameField;
    private JTextField createLastNameField;
    private JComboBox<String> createTypeComboBox;
    private JButton createButton;
    private JTextField editFirstNameField;
    private JTextField editLastNameField;
    private JTextField editPointsField;
    private JButton editPromoteButton;
    private JButton editSaveButton;
    private JButton editDeleteButton;
    private JButton top3MostPointsButton;
    private JComboBox<String> number1MostPointsComboBox;
    private JButton number1MostPointsButton;

    private LoyaltyCard selected;

    public LoyaltyCardManager() {
        this.cards = new ArrayList<>();

        retrieveCards();
        initializeTable();
        configureCreation();
        configureEdit();
        configureStatistics();

        setContentPane(contentPanel);
        setSize(750, 350);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void retrieveCards() {
        try {
            File file = new File(FILE_NAME);
            file.createNewFile();

            List<String> lines = Files.readAllLines(file.toPath());

            for (String line : lines) {
                String[] parts = line.split(",");

                UUID uuid = UUID.fromString(parts[0]);
                String firstName = parts[1];
                String lastName = parts[2];
                int points = Integer.parseInt(parts[3]);
                String type = parts[4];

                LoyaltyCard card;

                if (type.equalsIgnoreCase("gold")) {
                    card = new GoldLoyaltyCard(uuid, firstName, lastName, points);
                } else {
                    card = new PlatinumLoyaltyCard(uuid, firstName, lastName, points);
                }

                cards.add(card);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred whilst retrieving cards", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveCards() {
        List<String> lines = new ArrayList<>();

        for (LoyaltyCard card : cards) {
            lines.add(card.toDatabase());
        }

        try {
            Files.write(new File(FILE_NAME).toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred whilst saving cards", "Error", JOptionPane.ERROR_MESSAGE);
        }

        updateTable();
    }

    private void initializeTable() {
        DefaultTableModel model = (DefaultTableModel) cardsTable.getModel();
        model.addColumn("Card ID");
        model.addColumn("First Name");
        model.addColumn("Last Name");
        model.addColumn("Points");
        model.addColumn("Type");

        cardsTable.getSelectionModel().addListSelectionListener(e -> {
            if (cardsTable.getSelectedRow() == -1) {
                selected = null;
                updateEdit();
                return;
            }

            selected = cards.get(cardsTable.getSelectedRow());
            updateEdit();
        });

        updateTable();
    }

    private void configureEdit() {
        editPromoteButton.addActionListener(e -> {
            if (selected == null) {
                return;
            }

            cards.remove(selected);
            cards.add(new PlatinumLoyaltyCard(selected));
            saveCards();

            JOptionPane.showMessageDialog(null, "Card promoted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        editDeleteButton.addActionListener(e -> {
            if (selected == null) {
                return;
            }

            cards.remove(selected);
            saveCards();

            JOptionPane.showMessageDialog(null, "Card deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        editSaveButton.addActionListener(e -> {
            if (selected == null) {
                return;
            }

            selected.setFirstName(editFirstNameField.getText());
            selected.setLastName(editLastNameField.getText());
            selected.setPoints(Integer.parseInt(editPointsField.getText()));
            saveCards();

            JOptionPane.showMessageDialog(null, "Card edited successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void updateEdit() {
        if (selected == null) {
            editFirstNameField.setText("");
            editLastNameField.setText("");
            editPointsField.setText("");
            editPromoteButton.setEnabled(false);
            editSaveButton.setEnabled(false);
            editDeleteButton.setEnabled(false);
            return;
        }

        editFirstNameField.setText(selected.getFirstName());
        editLastNameField.setText(selected.getLastName());
        editPointsField.setText(String.valueOf(selected.getPoints()));
        editPromoteButton.setEnabled(selected.getType().equalsIgnoreCase("gold"));
        editSaveButton.setEnabled(true);
        editDeleteButton.setEnabled(true);
    }

    private void configureCreation() {
        createTypeComboBox.addItem("Gold");
        createTypeComboBox.addItem("Platinum");

        createButton.addActionListener(e -> {
            try {
                String firstName = createFirstNameField.getText();
                String lastName = createLastNameField.getText();
                String type = createTypeComboBox.getSelectedItem().toString();

                LoyaltyCard card;

                if (type.equalsIgnoreCase("gold")) {
                    card = new GoldLoyaltyCard(UUID.randomUUID(), firstName, lastName);
                } else {
                    card = new PlatinumLoyaltyCard(UUID.randomUUID(), firstName, lastName);
                }

                cards.add(card);
                saveCards();

                JOptionPane.showMessageDialog(null, "Card created successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void configureStatistics() {
        top3MostPointsButton.addActionListener(e -> {
            List<LoyaltyCard> gold = cards.stream()
                    .filter(card -> card.getType().equalsIgnoreCase("gold"))
                    .sorted(Comparator.comparingInt(LoyaltyCard::getPoints).reversed())
                    .limit(3)
                    .toList();

            List<LoyaltyCard> platinum = cards.stream()
                    .filter(card -> card.getType().equalsIgnoreCase("platinum"))
                    .sorted(Comparator.comparingInt(LoyaltyCard::getPoints).reversed())
                    .limit(3)
                    .toList();

            StringBuilder sb = new StringBuilder();

            sb.append("Top 3 most points:\n");
            sb.append("Gold:\n");
            gold.forEach(card -> sb.append(card.getFirstName()).append(" ").append(card.getLastName()).append(" - ").append(card.getPoints()).append("\n"));
            sb.append("Platinum:\n");
            platinum.forEach(card -> sb.append(card.getFirstName()).append(" ").append(card.getLastName()).append(" - ").append(card.getPoints()).append("\n"));

            JOptionPane.showMessageDialog(null, sb.toString(), "Top 3", JOptionPane.INFORMATION_MESSAGE);
        });

        number1MostPointsComboBox.addItem("Gold");
        number1MostPointsComboBox.addItem("Platinum");

        number1MostPointsButton.addActionListener(e -> {
            String type = number1MostPointsComboBox.getSelectedItem().toString();

            LoyaltyCard card = cards.stream()
                    .filter(c -> c.getType().equalsIgnoreCase(type))
                    .max(Comparator.comparingInt(LoyaltyCard::getPoints))
                    .orElse(null);

            if (card == null) {
                JOptionPane.showMessageDialog(null, "No cards found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(null, card.getFirstName() + " " + card.getLastName() + " - " + card.getPoints(), "Top 1", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) cardsTable.getModel();

        IntStream.range(0, model.getRowCount()).forEach(i -> model.removeRow(0));
        cards.forEach(card -> model.addRow(new Object[]{card.getUUID(), card.getFirstName(), card.getLastName(), card.getPoints(), card.getType()}));
    }

}
