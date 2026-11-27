package src.view;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.controller.ControleurApplication;
import src.model.Niveau;
import src.model.Parcours;
import src.model.Utilisateur;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Optional;

/* Page initiale après connexion.
 * Elle est reconstruite à chaque affichage pour refléter la progression et le score à jour.
 */
public class PanneauMenu extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ControleurApplication controleur;

    public PanneauMenu(ControleurApplication controleur) {
        this.controleur = controleur;
        setBackground(Theme.FOND);
        setLayout(new BorderLayout());
    }

    public void afficher(Utilisateur utilisateur, Parcours parcours) {
        removeAll();

        // Bandeau du haut : nom du joueur et score
        JPanel haut = new JPanel(new BorderLayout());
        haut.setOpaque(false);
        haut.setBorder(BorderFactory.createEmptyBorder(24, 32, 0, 32));
        haut.add(Theme.etiquette("Bonjour, " + utilisateur.getPseudo() + " !", Theme.SOUS_TITRE, Theme.TEXTE),
                BorderLayout.WEST);
        haut.add(Theme.etiquette("Score : " + utilisateur.getScore() + " pts", Theme.SOUS_TITRE, Theme.JAUNE),
                BorderLayout.EAST);
        add(haut, BorderLayout.NORTH);

        // Carte centrale : les choix
        PanneauCarte carte = new PanneauCarte(new BorderLayout(0, 18), Theme.CARTE, 28);
        carte.setBorder(BorderFactory.createEmptyBorder(32, 48, 32, 48));

        JPanel entete = new JPanel(new GridLayout(0, 1, 0, 4));
        entete.setOpaque(false);
        JLabel titre = Theme.etiquette("JavaQuest", Theme.TITRE, Theme.JAUNE);
        titre.setHorizontalAlignment(JLabel.CENTER);
        entete.add(titre);
        JLabel avancement = Theme.etiquette("Niveaux terminés : " + parcours.nombreNiveauxTermines(utilisateur)
                + " / " + parcours.getNombreNiveaux(), Theme.TEXTE_NORMAL, Theme.TEXTE_DISCRET);
        avancement.setHorizontalAlignment(JLabel.CENTER);
        entete.add(avancement);
        carte.add(entete, BorderLayout.NORTH);

        JPanel boutons = new JPanel(new GridLayout(0, 1, 0, 12));
        boutons.setOpaque(false);

        Optional<Niveau> aReprendre = parcours.niveauAReprendre(utilisateur);
        String verbe = utilisateur.getDernierNiveauTermine() == 0 ? "Commencer" : "Reprendre";
        BoutonArrondi reprendre = aReprendre
                .map(n -> new BoutonArrondi(verbe + " : niveau " + n.getNumero() + " - " + n.getTitre(), Theme.ACCENT))
                .orElseGet(() -> new BoutonArrondi(parcours.estVide() ? "Aucun niveau disponible"
                        : "Tous les niveaux sont terminés !", Theme.ACCENT));
        reprendre.setEnabled(aReprendre.isPresent());
        reprendre.addActionListener(e -> controleur.reprendre());
        boutons.add(reprendre);

        BoutonArrondi debut = new BoutonArrondi("Revenir au début", Theme.CARTE_CLAIRE);
        debut.setEnabled(!parcours.estVide());
        debut.addActionListener(e -> controleur.recommencerAuDebut());
        boutons.add(debut);

        BoutonArrondi classement = new BoutonArrondi("Classement", Theme.CARTE_CLAIRE);
        classement.addActionListener(e -> controleur.afficherClassement());
        boutons.add(classement);

        BoutonArrondi deconnexion = new BoutonArrondi("Se déconnecter", Theme.CARTE_CLAIRE);
        deconnexion.addActionListener(e -> controleur.deconnecter());
        boutons.add(deconnexion);

        carte.add(boutons, BorderLayout.CENTER);

        JPanel centre = new JPanel(new GridBagLayout());
        centre.setOpaque(false);
        centre.add(carte);
        add(centre, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}