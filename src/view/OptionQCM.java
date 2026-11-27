package src.view;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/* Une option de QCM : grosse carte cliquable, entourée d'un trait violet quand elle est choisie. */
public class OptionQCM extends JToggleButton {

    private static final long serialVersionUID = 1L;

    private boolean verrouillee;

    public OptionQCM(String texte) {
        super(texte);
        setFont(Theme.CODE);
        setForeground(Theme.TEXTE);
        setHorizontalAlignment(LEFT);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setRolloverEnabled(true);
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /* Une fois la réponse validée, on empêche de changer de choix. */
    public void setVerrouillee(boolean verrouillee) {
        this.verrouillee = verrouillee;
        setRolloverEnabled(!verrouillee);
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (!verrouillee) {
            super.processMouseEvent(e);
        }
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (!verrouillee) {
            super.processKeyEvent(e);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fond = isSelected() ? new Color(0x3F3A82)
                : getModel().isRollover() ? Theme.CARTE_CLAIRE : Theme.CARTE;
        g2.setColor(fond);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        if (isSelected()) {
            g2.setColor(Theme.ACCENT);
            g2.setStroke(new BasicStroke(3f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}