package src.view;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.controller.ControleurApplication;
import src.model.Utilisateur;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/* Tableau des meilleurs scores (local). La ligne du joueur connecté est mise en valeur. */
public class PanneauClassement extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ControleurApplication controleur;

    public PanneauClassement(ControleurApplication controleur) {
        this.controleur = controleur;
        setBackground(Theme.FOND);
        setLayout(new GridBagLayout());
    }

    public void afficher(List<Utilisateur> classement, Utilisateur courant) {
        removeAll();

        PanneauCarte carte = new PanneauCarte(new BorderLayout(0, 20), Theme.CARTE, 28);
        carte.setBorder(BorderFactory.createEmptyBorder(30, 48, 30, 48));

        JLabel titre = Theme.etiquette("Classement", Theme.TITRE, Theme.JAUNE);
        titre.setHorizontalAlignment(JLabel.CENTER);
        carte.add(titre, BorderLayout.NORTH);

        JPanel tableau = new JPanel(new GridBagLayout());
        tableau.setOpaque(false);
        ajouterLigne(tableau, 0, "#", "Joueur", "Niveaux", "Score", Theme.TEXTE_DISCRET, Theme.PETIT);
        for (int i = 0; i < classement.size(); i++) {
            Utilisateur u = classement.get(i);
            boolean moi = courant != null && u.getPseudo().equalsIgnoreCase(courant.getPseudo());
            ajouterLigne(tableau, i + 1, String.valueOf(i + 1), u.getPseudo(),
                    String.valueOf(u.getDernierNiveauTermine()), u.getScore() + " pts",
                    moi ? Theme.JAUNE : Theme.TEXTE, moi ? Theme.ENONCE : Theme.TEXTE_NORMAL);
        }
        carte.add(tableau, BorderLayout.CENTER);

        BoutonArrondi retour = new BoutonArrondi("Retour au menu", Theme.ACCENT);
        retour.addActionListener(e -> controleur.afficherMenu());
        carte.add(retour, BorderLayout.SOUTH);

        add(carte);
        revalidate();
        repaint();
    }

    private static void ajouterLigne(JPanel tableau, int ligne, String rang, String pseudo,
                                     String niveaux, String score, Color couleur, Font police) {
        String[] valeurs = {rang, pseudo, niveaux, score};
        int[] largeurs = {40, 220, 90, 100};
        for (int colonne = 0; colonne < valeurs.length; colonne++) {
            JLabel label = Theme.etiquette(valeurs[colonne], police, couleur);
            if (colonne >= 2) {
                label.setHorizontalAlignment(JLabel.RIGHT);
            }
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = colonne;
            c.gridy = ligne;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(5, 8, 5, 8);
            label.setPreferredSize(new java.awt.Dimension(largeurs[colonne], label.getPreferredSize().height));
            tableau.add(label, c);
        }
    }
}