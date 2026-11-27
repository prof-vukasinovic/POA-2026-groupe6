package model;

/** Ce qui se passe quand l'élève valide une réponse. */
public enum ResultatReponse {
    /** Bonne réponse, il reste des questions dans le niveau. */
    BONNE_REPONSE,
    /** Mauvaise réponse, il reste au moins un essai. */
    MAUVAISE_REPONSE,
    /** Bonne réponse à la dernière question : le niveau est terminé. */
    NIVEAU_REUSSI,
    /** Mauvaise réponse et plus aucun essai : le niveau est perdu. */
    NIVEAU_ECHOUE
}