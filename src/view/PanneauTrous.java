package src.view;

import javax.swing.*;

import src.model.NiveauTXT;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/* Affiche un morceau de code dont les trous ("___") sont remplacés par de vrais champs de saisie,
 * placés au milieu du texte, ligne par ligne.
 */
public class PanneauTrous extends JPanel implements PanneauQuestion {

    private static final long serialVersionUID = 1L;

    private final List<JTextField> champs = new ArrayList<>();

    public PanneauTrous(NiveauTXT.Question question) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (!question.consigne().isBlank()) {
            var consigne = Theme.paragraphe(question.consigne(), Theme.LARGEUR_COLONNE, Theme.ENONCE, Theme.TEXTE);
            consigne.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(consigne);
            add(Box.createVerticalStrut(16));
        }

        PanneauCarte carteCode = new PanneauCarte(null, Theme.FOND_CODE, 16);
        carteCode.setLayout(new BoxLayout(carteCode, BoxLayout.Y_AXIS));
        carteCode.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        carteCode.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String ligne : question.enonce().split("\n", -1)) {
            carteCode.add(construireLigne(ligne));
        }
        add(carteCode);
    }

    /* Une ligne de code = une suite de morceaux de texte séparés par des champs de saisie. */
    private JPanel construireLigne(String ligne) {
        JPanel panneau = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        panneau.setOpaque(false);
        panneau.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] morceaux = ligne.split(NiveauTXT.MARQUEUR_TROU, -1);
        for (int i = 0; i < morceaux.length; i++) {
            if (!morceaux[i].isEmpty()) {
                panneau.add(Theme.etiquette(morceaux[i], Theme.CODE, Theme.TEXTE));
            }
            if (i < morceaux.length - 1) { // un champ entre deux morceaux = un trou
                JTextField champ = Theme.champCode(8);
                champs.add(champ);
                panneau.add(champ);
            }
        }
        if (ligne.isEmpty()) {
            panneau.add(Theme.etiquette(" ", Theme.CODE, Theme.TEXTE));
        }
        return panneau;
    }

    @Override
    public JComponent composant() {
        return this;
    }

    @Override
    public boolean aUneReponse() {
        return champs.stream().noneMatch(champ -> champ.getText().isBlank());
    }

    @Override
    public List<String> getReponses() {
        return champs.stream().map(JTextField::getText).toList();
    }

    @Override
    public void setEditable(boolean editable) {
        champs.forEach(champ -> champ.setEditable(editable));
    }
}