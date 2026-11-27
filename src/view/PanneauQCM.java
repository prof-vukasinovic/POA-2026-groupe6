package src.view;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

import src.model.NiveauQCM;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/* Affiche une question de QCM : l'énoncé puis une carte cliquable par option. */
public class PanneauQCM extends JPanel implements PanneauQuestion {

    private static final long serialVersionUID = 1L;

    private final List<OptionQCM> options = new ArrayList<>();

    public PanneauQCM(NiveauQCM.Question question) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        var enonce = Theme.paragraphe(question.enonce(), Theme.LARGEUR_COLONNE, Theme.ENONCE, Theme.TEXTE);
        enonce.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(enonce);
        add(Box.createVerticalStrut(16));

        ButtonGroup groupe = new ButtonGroup(); // garantit qu'une seule option est choisie
        for (String texte : question.options()) {
            OptionQCM option = new OptionQCM(texte);
            option.setAlignmentX(Component.LEFT_ALIGNMENT);
            option.setMaximumSize(new Dimension(Integer.MAX_VALUE, option.getPreferredSize().height));
            groupe.add(option);
            options.add(option);
            add(option);
            add(Box.createVerticalStrut(10));
        }
    }

    @Override
    public JComponent composant() {
        return this;
    }

    @Override
    public boolean aUneReponse() {
        return options.stream().anyMatch(OptionQCM::isSelected);
    }

    @Override
    public List<String> getReponses() {
        for (OptionQCM option : options) {
            if (option.isSelected()) {
                return List.of(option.getText());
            }
        }
        return List.of();
    }

    @Override
    public void setEditable(boolean editable) {
        options.forEach(option -> option.setVerrouillee(!editable));
    }
}