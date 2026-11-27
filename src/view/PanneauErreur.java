package src.view;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import src.model.NiveauErreur;

import java.awt.Component;
import java.util.List;

/* Affiche un code erroné dans une zone de texte modifiable : l'élève corrige directement
 * le code, comme dans un mini éditeur.
 */
public class PanneauErreur extends JPanel implements PanneauQuestion {

    private static final long serialVersionUID = 1L;

    private final JTextArea zoneCode;

    public PanneauErreur(NiveauErreur.Question question) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (!question.consigne().isBlank()) {
            var consigne = Theme.paragraphe(question.consigne(), Theme.LARGEUR_COLONNE, Theme.ENONCE, Theme.TEXTE);
            consigne.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(consigne);
            add(Box.createVerticalStrut(16));
        }

        int lignes = question.codeErrone().split("\n", -1).length;
        zoneCode = new JTextArea(question.codeErrone(), Math.max(lignes + 1, 5), 50);
        zoneCode.setFont(Theme.CODE);
        zoneCode.setForeground(Theme.JAUNE);
        zoneCode.setBackground(Theme.FOND_CODE);
        zoneCode.setCaretColor(Theme.TEXTE);
        zoneCode.setTabSize(4);

        PanneauCarte carte = new PanneauCarte(new java.awt.BorderLayout(), Theme.FOND_CODE, 16);
        carte.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        carte.setAlignmentX(Component.LEFT_ALIGNMENT);
        carte.add(zoneCode);
        add(carte);
    }

    @Override
    public JComponent composant() {
        return this;
    }

    @Override
    public boolean aUneReponse() {
        return !zoneCode.getText().isBlank();
    }

    @Override
    public List<String> getReponses() {
        return List.of(zoneCode.getText());
    }

    @Override
    public void setEditable(boolean editable) {
        zoneCode.setEditable(editable);
    }
}