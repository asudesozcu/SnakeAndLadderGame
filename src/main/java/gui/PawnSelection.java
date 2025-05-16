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
public class PawnSelection extends JDialog {
  private int selectedPawn = -1;

   public PawnSelection(JFrame parent, int disabledPawn, ActionListener onPawnSelected) {
    super(parent, "Select Your Pawn", true);
    System.out.println("[PawnSelection] NEW DIALOG OPENED");
        setSize(500, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(0, 0, 0, 0)); // şeffaf arka plan

        JLabel label = new JLabel("SELECT YOUR COLOUR");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        add(label, BorderLayout.NORTH);

        for (int i = 1; i <= 6; i++) {
            if (i == disabledPawn) continue; // rakibin seçtiği piyon görünmesin

            int pawn = i;
            ImageIcon icon = new ImageIcon(getClass().getResource("/Image/player " + pawn + ".png"));
            JButton btn = new JButton(icon);
            btn.setPreferredSize(new Dimension(60, 60));

           btn.addActionListener(e -> {
    System.out.println("[PawnSelection] Pawn " + pawn + " clicked");
    selectedPawn = pawn;
    try {
        onPawnSelected.actionPerformed(new ActionEvent(PawnSelection.this, ActionEvent.ACTION_PERFORMED, "pawn"));
        System.out.println("[PawnSelection] actionPerformed executed");
    } catch (Exception ex) {
        System.err.println("[PawnSelection] Error in actionPerformed: " + ex.getMessage());
    }
    dispose();
    System.out.println("[PawnSelection] Dialog disposed");
});

            panel.add(btn);
        }

        add(panel, BorderLayout.CENTER);
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.ORANGE, 4, true));
        setVisible(true);
    }

    public int getSelectedPawn() {
        return selectedPawn;
    }
}