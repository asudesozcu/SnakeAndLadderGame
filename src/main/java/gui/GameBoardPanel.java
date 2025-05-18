
package gui;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import java.util.Map;

import java.util.Map;

import java.util.HashMap;

/**
 *
 * @author sozcu
 */
public class GameBoardPanel extends JPanel {
    private final JLabel[] pieces;
    private final int[] positions;
    private final Image backgroundImage;
    private final Map<Integer, Integer> snakesAndLadders;

   public GameBoardPanel(int playerCount) {
    setLayout(null);
    setBounds(50, 30, 600, 600);
    setOpaque(false);

    backgroundImage = new ImageIcon(getClass().getResource("/Image/board600.png")).getImage();

    snakesAndLadders = initializeSnakesAndLadders(); // ✅ ÖNCE haritayı yükle

    pieces = new JLabel[playerCount];
    positions = new int[playerCount];

    for (int i = 0; i < playerCount; i++) {
        pieces[i] = new JLabel();
        pieces[i].setSize(40, 40);
        add(pieces[i]);
        setPiecePosition(i, 0); // ✅ Artık burada kullanılabilir
    }
}


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
    }

    public void setPieceIcon(int index, Icon icon) {
        pieces[index].setIcon(icon);
    }
    

 public void setPiecePosition(int index, int pos) {
    // Bu sadece konumu ayarlar, snake/ladder'ı burada kontrol etme!
    positions[index] = pos;
    int[] c = getCoordinates(pos);
    int xOffset = (index % 2) * 10;
    int yOffset = (index / 2) * 10;
    pieces[index].setLocation(c[0] + xOffset, c[1] - yOffset);
}




    private int[] getCoordinates(int pos) {
        if (pos <= 0) return new int[]{0, 540};
        int row = (pos - 1) / 10;
        int col = (pos - 1) % 10;
        if (row % 2 == 1) col = 9 - col;
        int tileSize = 60;
        int baseX = col * tileSize + 10;
        int baseY = 540 - row * tileSize + 10;
        return new int[]{baseX, baseY};
    }

    private Map<Integer, Integer> initializeSnakesAndLadders() {
        Map<Integer, Integer> map = new HashMap<>();
        // Snakes (from > to)
        map.put(21, 2);
        map.put(27, 15);
        map.put(47, 29);
        map.put(56, 37);
        map.put(74, 46);
        map.put(90, 52);
        map.put(99, 41);

        // Ladders (from < to)
        map.put(23, 42);
        map.put(32, 51);
        map.put(61, 79);
        map.put(65, 84);
        map.put(75, 96);

        return map;
    }
    
 public void animatePiecePosition(int index, int start, int end) {
    new Thread(() -> {
        int step = (end > start) ? 1 : -1;

        // 🔁 1. Yavaş yavaş end pozisyonuna ilerle
        for (int pos = start + step; pos != end + step; pos += step) {
            setPiecePosition(index, pos);
            try {
                Thread.sleep(150); // animasyon süresi
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // 🎯 2. End'e tam olarak ulaştıktan sonra pozisyonu yaz
        positions[index] = end;

        // 🐍 3. Eğer end bir snake veya ladder başıysa → şimdi zıpla
        if (snakesAndLadders.containsKey(end)) {
            int jumpTo = snakesAndLadders.get(end);

            System.out.println("[GameBoard] Snake/Ladder triggered: " + end + " → " + jumpTo);

            try {
                Thread.sleep(300); // kısa duraklama efekti
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // çapraz zıplama
            int[] coords = getCoordinates(jumpTo);
            SwingUtilities.invokeLater(() -> pieces[index].setLocation(coords[0], coords[1]));

            positions[index] = jumpTo;
        }

    }).start();
}


        public int getCurrentPosition(int index) {
    return positions[index];
}

} 