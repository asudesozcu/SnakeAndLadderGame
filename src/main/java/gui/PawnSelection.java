/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
/**
 *
 * @author sozcu
 */
/**
 * PawnSelection is a modal dialog that prompts the player to select a pawn (color).
 * It disables a specific pawn if passed (to prevent duplicate selection).
 */
public class PawnSelection extends JDialog {
    // Stores the pawn number selected by the player (1 to 6), -1 if none selected
    private int selectedPawn = -1;

    /**
     * Constructor creates the pawn selection dialog.
     * Displays buttons for pawns 1-6 except the disabledPawn.
     * Calls onSelect ActionListener when a pawn is selected.
     *
     * @param parent The parent JFrame to center this dialog on
     * @param disabledPawn Pawn number to disable (already taken by opponent), -1 if none
     * @param onSelect ActionListener callback to handle selection event
     */
    public PawnSelection(JFrame parent, int disabledPawn, ActionListener onSelect) {
        super(parent, "Select Your Pawn", true); // Modal dialog
        setSize(500, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Title label
        JLabel label = new JLabel("SELECT YOUR COLOUR");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        add(label, BorderLayout.NORTH);

        // Panel holding the pawn buttons
        JPanel panel = new JPanel(new FlowLayout());

        // Create a button for each pawn from 1 to 6, skipping the disabled one
        for (int i = 1; i <= 6; i++) {
            if (i == disabledPawn) continue; // skip disabled pawn

            int pawn = i; // local variable for lambda capture
            ImageIcon icon = new ImageIcon(getClass().getResource("/Image/player " + pawn + ".png"));
            JButton btn = new JButton(icon);
            btn.setPreferredSize(new Dimension(60, 60));

            // When clicked, set the selected pawn, call the onSelect listener and close dialog
            btn.addActionListener(e -> {
                selectedPawn = pawn;
                onSelect.actionPerformed(e);
                dispose();
            });

            panel.add(btn);
        }
        add(panel, BorderLayout.CENTER);

        // Remove window decorations and add orange border
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.ORANGE, 4, true));

        // Show dialog immediately
        setVisible(true);
    }

    /**
     * Returns the pawn number selected by the user.
     * 
     * @return selected pawn number or -1 if none selected
     */
    public int getSelectedPawn() {
        return selectedPawn;
    }
}