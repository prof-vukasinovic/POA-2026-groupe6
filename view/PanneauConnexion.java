package view;

import controller.ControleurApplication;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/* Écran d'accueil : connexion à un compte ou création d'un nouveau compte. */
public class PanneauConnexion extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTextField champPseudo = Theme.champTexte(18);
    private final JPasswordField champMotDePasse = Theme.champMotDePasse(18);
    private final JLabel message = Theme.etiquette(" ", Theme.PETIT, Theme.ROUGE);

    public PanneauConnexion(ControleurApplication controleur) {
        setBackground(Theme.FOND);
        setLayout(new GridBagLayout());

        PanneauCarte carte = new PanneauCarte(new GridBagLayout(), Theme.CARTE, 28);
        carte.setBorder(BorderFactory.createEmptyBorder(34, 54, 34, 54));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);

        JLabel titre = Theme.etiquette("JavaQuest", Theme.TITRE, Theme.JAUNE);
        titre.setHorizontalAlignment(JLabel.CENTER);
        carte.add(titre, c);
        JLabel sousTitre = Theme.etiquette("Apprends Java en jouant", Theme.TEXTE_NORMAL, Theme.TEXTE_DISCRET);
        sousTitre.setHorizontalAlignment(JLabel.CENTER);
        c.insets = new Insets(0, 0, 24, 0);
        carte.add(sousTitre, c);

        c.insets = new Insets(5, 0, 2, 0);
        carte.add(Theme.etiquette("Pseudo", Theme.PETIT, Theme.TEXTE_DISCRET), c);
        c.insets = new Insets(0, 0, 10, 0);
        carte.add(champPseudo, c);
        c.insets = new Insets(5, 0, 2, 0);
        carte.add(Theme.etiquette("Mot de passe", Theme.PETIT, Theme.TEXTE_DISCRET), c);
        c.insets = new Insets(0, 0, 6, 0);
        carte.add(champMotDePasse, c);

        c.insets = new Insets(4, 0, 12, 0);
        carte.add(message, c);

        BoutonArrondi connecter = new BoutonArrondi("Se connecter", Theme.ACCENT);
        BoutonArrondi creer = new BoutonArrondi("Créer un compte", Theme.CARTE_CLAIRE);
        c.insets = new Insets(4, 0, 4, 0);
        carte.add(connecter, c);
        carte.add(creer, c);

        Runnable seConnecter = () -> {
            controleur.connecter(champPseudo.getText(), new String(champMotDePasse.getPassword()));
            champMotDePasse.setText("");
        };
        connecter.addActionListener(e -> seConnecter.run());
        champPseudo.addActionListener(e -> champMotDePasse.requestFocusInWindow());
        champMotDePasse.addActionListener(e -> seConnecter.run()); // touche Entrée
        creer.addActionListener(e -> {
            controleur.creerCompte(champPseudo.getText(), new String(champMotDePasse.getPassword()));
            champMotDePasse.setText("");
        });

        add(carte);
    }

    /* Remet l'écran à zéro (mot de passe effacé) et affiche éventuellement une erreur. */
    public void reinitialiser(String erreur) {
        message.setText(erreur == null || erreur.isBlank() ? " " : erreur);
        champMotDePasse.setText("");
        champPseudo.requestFocusInWindow();
    }
}