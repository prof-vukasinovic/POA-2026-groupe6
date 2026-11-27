package view;

import controller.ControleurApplication;
import model.BilanNiveau;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

/* Écran de fin de niveau : résultat, points gagnés, et que faire ensuite. */
public class PanneauBilan extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ControleurApplication controleur;

    public PanneauBilan(ControleurApplication controleur) {
        this.controleur = controleur;
        setBackground(Theme.FOND);
        setLayout(new GridBagLayout());
    }

    public void afficher(BilanNiveau bilan) {
        removeAll();

        PanneauCarte carte = new PanneauCarte(new GridLayout(0, 1, 0, 12), Theme.CARTE, 28);
        carte.setBorder(BorderFactory.createEmptyBorder(36, 56, 36, 56));

        carte.add(centrer(Theme.etiquette(bilan.reussi() ? "Niveau réussi !" : "Niveau échoué",
                Theme.TITRE, bilan.reussi() ? Theme.VERT : Theme.ROUGE)));
        carte.add(centrer(Theme.etiquette("Niveau " + bilan.niveau().getNumero() + " : " + bilan.niveau().getTitre(),
                Theme.TEXTE_NORMAL, Theme.TEXTE_DISCRET)));

        if (!bilan.reussi()) {
            carte.add(centrer(Theme.etiquette("Relis la leçon et retente ta chance !", Theme.ENONCE, Theme.TEXTE)));
        } else if (bilan.pointsGagnes() > 0) {
            carte.add(centrer(Theme.etiquette("+" + bilan.pointsGagnes() + " points !", Theme.SOUS_TITRE, Theme.JAUNE)));
        } else {
            carte.add(centrer(Theme.etiquette("Niveau déjà validé : pas de nouveaux points.",
                    Theme.ENONCE, Theme.TEXTE)));
        }
        carte.add(centrer(Theme.etiquette("Score total : " + bilan.scoreTotal() + " pts",
                Theme.TEXTE_NORMAL, Theme.TEXTE)));

        if (bilan.reussi() && bilan.suivantDisponible()) {
            BoutonArrondi suivant = new BoutonArrondi("Niveau suivant", Theme.VERT);
            suivant.addActionListener(e -> controleur.niveauSuivant());
            carte.add(suivant);
        } else if (bilan.reussi()) {
            carte.add(centrer(Theme.etiquette("Tu as terminé tous les niveaux disponibles !",
                    Theme.ENONCE, Theme.JAUNE)));
        }
        BoutonArrondi rejouer = new BoutonArrondi("Rejouer ce niveau", bilan.reussi() ? Theme.CARTE_CLAIRE : Theme.ACCENT);
        rejouer.addActionListener(e -> controleur.rejouerNiveau());
        carte.add(rejouer);
        BoutonArrondi menu = new BoutonArrondi("Menu principal", Theme.CARTE_CLAIRE);
        menu.addActionListener(e -> controleur.afficherMenu());
        carte.add(menu);

        add(carte);
        revalidate();
        repaint();
    }

    private static JLabel centrer(JLabel label) {
        label.setHorizontalAlignment(JLabel.CENTER);
        return label;
    }
}