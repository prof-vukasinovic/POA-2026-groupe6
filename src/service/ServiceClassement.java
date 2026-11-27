package src.service;

import src.model.Utilisateur;
import src.persistence.DepotUtilisateurs;

import java.util.Comparator;
import java.util.List;

/* Classement local des joueurs.
 */
public class ServiceClassement {

    private final DepotUtilisateurs depot;

    public ServiceClassement(DepotUtilisateurs depot) {
        this.depot = depot;
    }

    /* Les meilleurs joueurs : score décroissant, puis progression, puis ordre alphabétique. */
    public List<Utilisateur> meilleurs(int nombre) {
        return depot.tous().stream()
                .sorted(Comparator.comparingInt(Utilisateur::getScore).reversed()
                        .thenComparing(Comparator.comparingInt(Utilisateur::getDernierNiveauTermine).reversed())
                        .thenComparing(Utilisateur::getPseudo, String.CASE_INSENSITIVE_ORDER))
                .limit(nombre)
                .toList();
    }
}