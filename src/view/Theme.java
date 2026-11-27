package src.view;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Font;

/* Couleurs, polices et petites fabriques de composants partagées par tous les écrans.
 * Changer l'apparence de toute l'application (thème clair, autre couleur d'accent...)
 * se fait ici, en un seul endroit.
 */
public final class Theme {

    private Theme() { }

    // Palette sombre, colorée, inspirée des applications d'apprentissage ludiques.
    public static final Color FOND = new Color(0x1B1B2F);
    public static final Color CARTE = new Color(0x262645);
    public static final Color CARTE_CLAIRE = new Color(0x34345C);
    public static final Color FOND_CODE = new Color(0x12121F);
    public static final Color TEXTE = new Color(0xF2F2F7);
    public static final Color TEXTE_DISCRET = new Color(0xA5A5C0);
    public static final Color ACCENT = new Color(0x6C5CE7);
    public static final Color VERT = new Color(0x00B894);
    public static final Color ROUGE = new Color(0xE5484D);
    public static final Color JAUNE = new Color(0xFFC145);
    public static final Color FOND_SUCCES = new Color(0x0F3B30);
    public static final Color FOND_ECHEC = new Color(0x45202A);

    public static final Font TITRE = new Font(Font.SANS_SERIF, Font.BOLD, 36);
    public static final Font SOUS_TITRE = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font ENONCE = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    public static final Font TEXTE_NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    public static final Font PETIT = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font BOUTON = new Font(Font.SANS_SERIF, Font.BOLD, 16);
    public static final Font CODE = new Font(Font.MONOSPACED, Font.PLAIN, 16);

    /* Largeur (en pixels) de la colonne centrale des écrans de niveau. */
    public static final int LARGEUR_COLONNE = 760;

    public static JLabel etiquette(String texte, Font police, Color couleur) {
        JLabel label = new JLabel(texte);
        label.setFont(police);
        label.setForeground(couleur);
        return label;
    }

    /* Paragraphe qui passe à la ligne automatiquement dans une largeur donnée.
     * On utilise du HTML dans un JLabel (Swing sait l'afficher) ; le texte est protégé avec
     * {@link #versHtml(String)}. Les `mots entre apostrophes inversées` sont mis en valeur
     * comme du code.
     */
    public static JLabel paragraphe(String texte, int largeur, Font police, Color couleur) {
        JLabel label = new JLabel(htmlLargeur(texte, largeur));
        label.setFont(police);
        label.setForeground(couleur);
        return label;
    }

    /* Texte HTML qui passe à la ligne dans une largeur exacte. On l'enferme dans un tableau HTML :
     * c'est la seule écriture dont la largeur est respectée à coup sûr par Swing
     * (le style "width" sur body ou div donne une largeur différente).
     */
    public static String htmlLargeur(String texte, int largeur) {
        return "<html><table width='" + largeur + "' cellpadding='0' cellspacing='0'><tr><td>" + versHtml(texte) + "</td></tr></table></html>";
    }

    /** Échappe le texte pour l'HTML, convertit les retours à la ligne et les `codes`. */
    public static String versHtml(String texte) {
        String html = texte.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\n", "<br>");
        return html.replaceAll("`([^`]+)`", "<font color='#FFC145'><tt>$1</tt></font>");
    }

    public static JTextField champTexte(int colonnes) {
        JTextField champ = new JTextField(colonnes);
        styler(champ, TEXTE_NORMAL);
        return champ;
    }

    public static JPasswordField champMotDePasse(int colonnes) {
        JPasswordField champ = new JPasswordField(colonnes);
        styler(champ, TEXTE_NORMAL);
        return champ;
    }

    /* Champ de saisie de code (police à chasse fixe), utilisé dans les textes à trous. */
    public static JTextField champCode(int colonnes) {
        JTextField champ = new JTextField(colonnes);
        styler(champ, CODE);
        champ.setForeground(JAUNE);
        return champ;
    }

    private static void styler(JTextField champ, Font police) {
        champ.setFont(police);
        champ.setBackground(CARTE_CLAIRE);
        champ.setForeground(TEXTE);
        champ.setCaretColor(TEXTE);
        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }
}