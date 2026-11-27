package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/* Gros bouton coloré aux coins arrondis.
 * On dessine nous-mêmes le fond (paintComponent) puis on laisse Swing écrire le texte.
 */
public class BoutonArrondi extends JButton {
   // Identifiant de version demandé par Java.
    // Sert juste à éviter un avertissement.
    private static final long serialVersionUID = 1L;

    // Couleur de fond normale. Elle n'est pas "final" car setCouleur() peut la changer
    // (le bouton du bas passe de violet à vert ou rouge selon la réponse).
    private Color couleur;

    public BoutonArrondi(String texte, Color couleur) {
        super(texte);
        this.couleur = couleur;

        setFont(Theme.BOUTON);          // Police définie dans Theme
        setForeground(Color.WHITE);     // Couleur du texte

        // On désactive le dessin par défaut de Swing, pour dessiner le fond nous-mêmes
        setOpaque(false);
        setContentAreaFilled(false);    // Swing ne remplit plus le fond du bouton
        setBorderPainted(false);        // Pas de bordure standard
        setFocusPainted(false);         // Pas de petit cadre pointillé quand le bouton a le focus
        setRolloverEnabled(true);       // Active la détection du survol (utilisée dans paintComponent)

        // Marge intérieure : 12 px en haut/bas, 28 px à gauche/droite. Elle détermine la taille du bouton.
        setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));
        // Le curseur devient une main quand la souris est sur le bouton. (C'est pas super marrant ?)
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /* Change la couleur du fond, puis redessine le bouton. */
    public void setCouleur(Color couleur) {
        this.couleur = couleur;
        repaint();
    }

    /* Swing appelle cette méthode chaque fois que le bouton doit être dessiné.
     * On la redéfinit (@Override) pour dessiner d'abord notre fond, puis laisser JButton
     * écrire le texte par-dessus.
     */
    @Override
    protected void paintComponent(Graphics g) {
        // g.create() fait une COPIE de l'outil de dessin : on peut changer ses réglages
        // (couleur, lissage) sans perturber le dessin des autres composants.
        Graphics2D g2 = (Graphics2D) g.create();

        // Lissage des bords : sans lui, les coins arrondis auraient des pics pics.
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Choix de la couleur de fond selon l'état du bouton (du plus prioritaire au moins prioritaire)
        // Sert à donner des informations à l'utilisateur
        Color fond;
        if (!isEnabled()) {
            fond = Theme.CARTE_CLAIRE;            // bouton désactivé : gris-bleu discret
        } else if (getModel().isPressed()) {
            fond = couleur.darker();              // en train d'être cliqué : plus sombre
        } else if (getModel().isRollover()) {
            fond = couleur.brighter();            // souris dessus : plus clair
        } else {
            fond = couleur;                       // état normal
        }

        g2.setColor(fond);
        // Rectangle plein de la taille du bouton, avec des coins arrondis de 18 pixels.
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        g2.dispose();                             // libération
        
        super.paintComponent(g);
    }
}