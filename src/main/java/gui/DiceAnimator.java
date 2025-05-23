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

/**
 * DiceAnimator handles the dice roll animation on a JLabel.
 * It cycles through dice face images quickly to simulate rolling,
 * then shows the final dice face and executes a callback if provided.
 */
public class DiceAnimator {
    /**
     * Animates the dice by cycling through images 1 to 6 rapidly,
     * then sets the dice image to the final rolled value.
     *  */
     public static void animate(JLabel lblDice, int finalValue, Runnable onComplete) {
        // Array of dice face frames to cycle through during animation
        int[] frames = {1, 2, 3, 4, 5, 6};
        
        // Timer to trigger frame changes every 50 milliseconds
        Timer timer = new Timer(50, null);
        
        // Index wrapped in array to allow modification inside inner class
        final int[] index = {0};
        
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Select current frame based on index
                int frame = frames[index[0] % frames.length];
                
                // Update dice image icon to current frame
                lblDice.setIcon(new ImageIcon(getClass().getResource("/Image/dice " + frame + ".jpg")));
                
                // Advance frame index
                index[0]++;
                
                // Stop animation after 6 frames and show final dice value
                if (index[0] >= 6) {
                    timer.stop();
                    lblDice.setIcon(new ImageIcon(getClass().getResource("/Image/dice " + finalValue + ".jpg")));
                    
                    // Run the callback if provided
                    if (onComplete != null) onComplete.run();
                }
            }
        });
        
        // Start the animation timer
        timer.start();
    }
}