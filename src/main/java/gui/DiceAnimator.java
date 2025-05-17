/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author sozcu
 */
public class DiceAnimator {
     public static void animate(JLabel lblDice, int finalValue, Runnable onComplete) {
        int[] frames = {1, 2, 3, 4, 5, 6};
        Timer timer = new Timer(50, null);
        final int[] index = {0};
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int frame = frames[index[0] % frames.length];
            lblDice.setIcon(new ImageIcon(getClass().getResource("/Image/dice " + frame + ".jpg")));
                index[0]++;
                if (index[0] >= 6) {
                    timer.stop();
                lblDice.setIcon(new ImageIcon(getClass().getResource("/Image/dice " + finalValue + ".jpg")));
                    if (onComplete != null) onComplete.run();
                }
            }
        });
        timer.start();
    }

    
}
