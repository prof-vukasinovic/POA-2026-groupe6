package src.controller;

import java.util.List;

import src.model.BilanNiveau;
import src.model.Parcours;
import src.model.ResultatReponse;
import src.model.SessionNiveau;
import src.model.Utilisateur;

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