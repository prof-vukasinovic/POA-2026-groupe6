package view;

import controller.ControleurApplication;
import controller.VueApplication;
import model.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

/* La fenêtre de l'application.
 *
 * Elle contient un CardLayou : une "pile de cartes" dont une seule est visible.
 * Chaque écran est un panneau; afficher un écran = mettre à jour son contenu puis le montrer.
 * Elle implémente {@link VueApplication} : c'est par cette interface que le contrôleur lui parle.
 */
public class FenetrePrincipale extends JFrame implements VueApplication {

    private static final long serialVersionUID = 1L;

    private static final String CONNEXION = "CONNEXION";
    private static final String MENU = "MENU";
    private static final String NIVEAU = "NIVEAU";
    private static final String BILAN = "BILAN";
    private static final String CLASSEMENT = "CLASSEMENT";

    private final CardLayout cartes = new CardLayout();
    private final JPanel conteneur = new JPanel(cartes);

    private final PanneauConnexion panneauConnexion;
    private final PanneauMenu panneauMenu;
    private final PanneauNiveau panneauNiveau;
    private final PanneauBilan panneauBilan;
    private final PanneauClassement panneauClassement;

    public FenetrePrincipale(ControleurApplication controleur) {
        super("JavaQuest - Apprends Java en jouant");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panneauConnexion = new PanneauConnexion(controleur);
        panneauMenu = new PanneauMenu(controleur);
        panneauNiveau = new PanneauNiveau(controleur);
        panneauBilan = new PanneauBilan(controleur);
        panneauClassement = new PanneauClassement(controleur);

        conteneur.add(panneauConnexion, CONNEXION);
        conteneur.add(panneauMenu, MENU);
        conteneur.add(panneauNiveau, NIVEAU);
        conteneur.add(panneauBilan, BILAN);
        conteneur.add(panneauClassement, CLASSEMENT);
        setContentPane(conteneur);

        setMinimumSize(new Dimension(920, 700));
        setSize(980, 780);
        setLocationRelativeTo(null);
    }

    @Override
    public void afficherConnexion(String message) {
        panneauConnexion.reinitialiser(message);
        cartes.show(conteneur, CONNEXION);
    }

    @Override
    public void afficherMenu(Utilisateur utilisateur, Parcours parcours) {
        panneauMenu.afficher(utilisateur, parcours);
        cartes.show(conteneur, MENU);
    }

    @Override
    public void afficherQuestion(SessionNiveau session) {
        panneauNiveau.afficherQuestion(session);
        cartes.show(conteneur, NIVEAU);
    }

    @Override
    public void afficherRetour(ResultatReponse resultat, SessionNiveau session, String explication) {
        panneauNiveau.afficherRetour(resultat, session, explication);
    }

    @Override
    public void afficherBilan(BilanNiveau bilan) {
        panneauBilan.afficher(bilan);
        cartes.show(conteneur, BILAN);
    }

    @Override
    public void afficherClassement(List<Utilisateur> classement, Utilisateur utilisateurCourant) {
        panneauClassement.afficher(classement, utilisateurCourant);
        cartes.show(conteneur, CLASSEMENT);
    }

    @Override
    public void afficherMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "JavaQuest", JOptionPane.INFORMATION_MESSAGE);
    }
}