package src.view;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/* Barre horizontale arrondie qui montre l'avancement dans le niveau (valeur entre 0 et 1). */
public class BarreProgression extends JComponent {

    private static final long serialVersionUID = 1L;

    private double valeur;

    public void setValeur(double valeur) {
        this.valeur = Math.max(0, Math.min(1, valeur));
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(320, 16);
    }

    /* Sans taille minimale, un titre long écraserait la barre jusqu'à 0 pixel de large. */
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(80, 16);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int h = getHeight();
        g2.setColor(Theme.CARTE_CLAIRE);
        g2.fillRoundRect(0, 0, getWidth(), h, h, h);
        int largeur = (int) Math.round(getWidth() * valeur);
        if (largeur > 0) {
            g2.setColor(Theme.VERT);
            g2.fillRoundRect(0, 0, Math.max(largeur, h), h, h, h);
        }
        g2.dispose();
    }
}