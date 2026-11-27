package controller;

import model.BilanNiveau;
import model.Parcours;
import model.ResultatReponse;
import model.SessionNiveau;
import model.Utilisateur;

import java.util.List;

/** Ce que le contrôleur demande à la vue.
 */
 
public interface VueApplication {

    /** Écran de connexion / création de compte ; message = erreur à afficher (ou null). */
    void afficherConnexion(String message);

    /** Page initiale : reprendre, revenir au début, classement. */
    void afficherMenu(Utilisateur utilisateur, Parcours parcours);

    /** Affiche la question courante de la session. */
    void afficherQuestion(SessionNiveau session);

    /** Réaction à une réponse validée (bravo / raté...), explication = texte pédagogique éventuel (fraudra le faire un jour). */
    void afficherRetour(ResultatReponse resultat, SessionNiveau session, String explication);

    /** Écran de fin de niveau. */
    void afficherBilan(BilanNiveau bilan);

    void afficherClassement(List<Utilisateur> classement, Utilisateur utilisateurCourant);

    /** Information ponctuelle (ex : "Mince alors, j'ai eu la flemme de faire pls de niveaux"). */
    void afficherMessage(String message);
}