package src.view;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/* Rangée de coeurs : un coeur plein par essai restant, un coeur grisé par essai perdu. */
public class IndicateurVies extends JComponent {

    private static final long serialVersionUID = 1L;
    private static final int TAILLE = 24;
    private static final int ESPACE = 8;

    private int total = 3;
    private int restantes = 3;

    public void setEtat(int total, int restantes) {
        this.total = total;
        this.restantes = restantes;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(total * (TAILLE + ESPACE), TAILLE + 4);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < total; i++) {
            g2.setColor(i < restantes ? Theme.ROUGE : Theme.CARTE_CLAIRE);
            g2.fill(coeur(i * (TAILLE + ESPACE), 2, TAILLE));
        }
        g2.dispose();
    }

    /* Forme de coeur dessinée avec deux courbes de Bézier. */
    private static Path2D coeur(double x, double y, double t) {
        Path2D forme = new Path2D.Double();
        forme.moveTo(x + t / 2, y + t * 0.95);
        forme.curveTo(x - t * 0.25, y + t * 0.45, x + t * 0.10, y - t * 0.15, x + t / 2, y + t * 0.28);
        forme.curveTo(x + t * 0.90, y - t * 0.15, x + t * 1.25, y + t * 0.45, x + t / 2, y + t * 0.95);
        forme.closePath();
        return forme;
    }
}