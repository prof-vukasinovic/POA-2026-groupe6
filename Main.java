import controller.ControleurApplication;
import model.Niveau;
import model.Parcours;
import persistence.AnalyseurNiveau;
import persistence.DepotNiveauxFichier;
import persistence.DepotUtilisateurs;
import persistence.DepotUtilisateursFichier;
import persistence.FormatNiveauException;
import service.EncodeurEnClair;
import service.ServiceClassement;
import service.ServiceCompte;
import view.FenetrePrincipale;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.UncheckedIOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        // Toute interface Swing doit être créée dans le thread dédié (EDT), comme dans BABA.
        SwingUtilities.invokeLater(Main::demarrer);
    }

    private static void demarrer() {
        try {
            // Modèle + persistance
            List<Niveau> niveaux = new DepotNiveauxFichier(Configuration.DOSSIER_NIVEAUX, new AnalyseurNiveau())
                    .chargerTous();
            Parcours parcours = new Parcours(niveaux);
            DepotUtilisateurs depot = new DepotUtilisateursFichier(Configuration.FICHIER_UTILISATEURS);

            // Services et contrôleur
            ServiceCompte serviceCompte = new ServiceCompte(depot, new EncodeurEnClair());
            ServiceClassement serviceClassement = new ServiceClassement(depot);
            ControleurApplication controleur = new ControleurApplication(serviceCompte, serviceClassement, parcours);

            // Vue, puis on relie le contrôleur à la vue et on démarre
            FenetrePrincipale fenetre = new FenetrePrincipale(controleur);
            controleur.setVue(fenetre);
            controleur.demarrer();
            fenetre.setVisible(true);

            if (parcours.estVide()) {
                fenetre.afficherMessage("Aucun niveau trouvé dans le dossier « "
                        + Configuration.DOSSIER_NIVEAUX.toAbsolutePath() + " ».");
            }
        } catch (FormatNiveauException | IllegalArgumentException | UncheckedIOException e) {
            JOptionPane.showMessageDialog(null, "Impossible de démarrer JavaQuest :\n" + e.getMessage(),
                    "Erreur au démarrage", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}