package model;

/** Résumé affiché à la fin d'un niveau (réussi ou échoué). */
public record BilanNiveau(Niveau niveau, boolean reussi, int pointsGagnes,
    boolean suivantDisponible, int scoreTotal) {
}