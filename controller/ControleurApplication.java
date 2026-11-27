package controller;

import Configuration;
import model.BilanNiveau;
import model.EtatSession;
import model.Niveau;
import model.Parcours;
import model.ResultatReponse;
import model.SessionNiveau;
import model.Utilisateur;
import service.CompteException;
import service.ServiceClassement;
import service.ServiceCompte;

import java.util.List;
import java.util.Optional;

/**
 * Le "C" de MVC : reçoit les actions de l'utilisateur (clics), met à jour le modèle, puis
 * dit à la vue quoi afficher.
 *   Vue --(appelle)--> Contrôleur --(modifie)--> Modèle
 *   Contrôleur --(via l'interface VueApplication)--> Vue
 * La vue ne modifie jamais le modèle directement.
 */
public class ControleurApplication {

    private final ServiceCompte serviceCompte;
    private final ServiceClassement serviceClassement;
    private final Parcours parcours;

    private VueApplication vue;
    private Utilisateur utilisateur;
    private SessionNiveau session;
    private int pointsGagnes;

    public ControleurApplication(ServiceCompte serviceCompte, ServiceClassement serviceClassement, Parcours parcours) {
        this.serviceCompte = serviceCompte;
        this.serviceClassement = serviceClassement;
        this.parcours = parcours;
    }

    /** La vue et le contrôleur se connaissent : on relie la vue après la construction */
    public void setVue(VueApplication vue) {
        this.vue = vue;
    }

    public void demarrer() {
        vue.afficherConnexion(null);
    }

    // Comptes

    public void connecter(String pseudo, String motDePasse) {
        try {
            utilisateur = serviceCompte.connecter(pseudo, motDePasse);
            afficherMenu();
        } catch (CompteException e) {
            vue.afficherConnexion(e.getMessage());
        }
    }

    public void creerCompte(String pseudo, String motDePasse) {
        try {
            utilisateur = serviceCompte.creerCompte(pseudo, motDePasse);
            afficherMenu();
        } catch (CompteException e) {
            vue.afficherConnexion(e.getMessage());
        }
    }

    public void deconnecter() {
        utilisateur = null;
        session = null;
        vue.afficherConnexion(null);
    }

    //Menu

    public void afficherMenu() {
        session = null;
        vue.afficherMenu(utilisateur, parcours);
    }

    /** Reprendre = lance le premier niveau non terminé. */
    public void reprendre() {
        Optional<Niveau> aReprendre = parcours.niveauAReprendre(utilisateur);
        if (aReprendre.isPresent()) {
            lancerNiveau(aReprendre.get());
        } else {
            vue.afficherMessage("Tu as terminé tous les niveaux disponibles. Bravo !");
        }
    }

    /** Revenir au début = lance le niveau 1. La progression déjà acquise n'est pas effacée (on espère que ça fonctionne vraiment). */
    public void recommencerAuDebut() {
        Optional<Niveau> premier = parcours.premier();
        if (premier.isPresent()) {
            lancerNiveau(premier.get());
        } else {
            vue.afficherMessage("Aucun niveau n'est disponible.");
        }
    }

    public void afficherClassement() {
        vue.afficherClassement(serviceClassement.meilleurs(Configuration.TAILLE_CLASSEMENT), utilisateur);
    }

    // Jeu

    /** Point d'entrée pour démarrer un niveau */
    public void lancerNiveau(Niveau niveau) {
        if (!parcours.estDebloque(utilisateur, niveau)) {
            vue.afficherMessage("Ce niveau n'est pas encore débloqué.");
            return;
        }
        session = new SessionNiveau(niveau, Configuration.ESSAIS_PAR_NIVEAU);
        pointsGagnes = 0;
        vue.afficherQuestion(session);
    }

    public void validerReponse(List<String> reponses) {
        if (session == null || session.estTerminee()) {
            return;
        }
        int indexQuestion = session.getQuestionCourante();
        ResultatReponse resultat = session.soumettre(reponses);

        String explication = "";
        if (resultat == ResultatReponse.BONNE_REPONSE || resultat == ResultatReponse.NIVEAU_REUSSI) {
            explication = session.getNiveau().getExplication(indexQuestion);
        }
        if (resultat == ResultatReponse.NIVEAU_REUSSI) {
            enregistrerReussite();
        }
        vue.afficherRetour(resultat, session, explication);
    }

    /** Continuer = question suivante, ou bilan si le niveau est fini. */
    public void continuer() {
        if (session == null) {
            return;
        }
        if (session.getEtat() == EtatSession.EN_COURS) {
            vue.afficherQuestion(session);
            return;
        }
        boolean reussi = session.getEtat() == EtatSession.REUSSI;
        boolean suivant = parcours.suivantApres(session.getNiveau().getNumero()).isPresent();
        vue.afficherBilan(new BilanNiveau(session.getNiveau(), reussi, pointsGagnes, suivant, utilisateur.getScore()));
    }

    public void rejouerNiveau() {
        if (session != null) {
            lancerNiveau(session.getNiveau());
        }
    }

    public void niveauSuivant() {
        if (session == null) {
            return;
        }
        parcours.suivantApres(session.getNiveau().getNumero()).ifPresent(this::lancerNiveau);
    }

    private void enregistrerReussite() {
        boolean premiereFois = utilisateur.enregistrerReussite(
                session.getNiveau().getNumero(), Configuration.POINTS_PAR_NIVEAU);
        pointsGagnes = premiereFois ? Configuration.POINTS_PAR_NIVEAU : 0;
        if (premiereFois) {
            serviceCompte.sauvegarder(utilisateur);
        }
    }
}