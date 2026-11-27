package src.view;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/* Panneau à placer dans un JScrollPane : sa largeur suit celle de la fenêtre (donc son contenu
 * reste centré) et seule la hauteur défile.
 */
public class PanneauDefilant extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    public PanneauDefilant(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return Math.max(40, visibleRect.height - 40);
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}