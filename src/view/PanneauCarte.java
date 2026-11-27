package src.view;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/* Panneau à fond coloré et coins arrondis. */
public class PanneauCarte extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Color fond;
    private final int rayon;

    public PanneauCarte(LayoutManager layout, Color fond, int rayon) {
        super(layout);
        this.fond = fond;
        this.rayon = rayon;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fond);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), rayon, rayon);
        g2.dispose();
        super.paintComponent(g);
    }
}