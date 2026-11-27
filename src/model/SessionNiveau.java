package src.model;

import java.util.List;
import java.util.Objects;

/**
 * Une tentative de l'élève sur un niveau : question en cours, essais restants, état.
 * Rejouer un niveau = créer une nouvelle SessionNiveau sur le même Niveau.
 *
 * Règle : les essais sont comptés pour tout le niveau. Une mauvaise réponse en coûte un,
 * quelle que soit la question ; à zéro, le niveau est perdu.
 */
public class SessionNiveau {

    private final Niveau niveau;
    private final int essaisMax;
    private int essaisRestants;
    private int questionCourante;
    private EtatSession etat = EtatSession.EN_COURS;

    public SessionNiveau(Niveau niveau, int essaisMax) {
        this.niveau = Objects.requireNonNull(niveau);
        if (essaisMax < 1) {
            throw new IllegalArgumentException("Il faut au moins un essai.");
        }
        this.essaisMax = essaisMax;
        this.essaisRestants = essaisMax;
    }

    /*Valide la réponse à la question courante.
     */
    public ResultatReponse soumettre(List<String> reponses) {
        if (etat != EtatSession.EN_COURS) {
        // Réussi ou échoué
            throw new IllegalStateException("Ce niveau est déjà terminé.");
        }
        if (niveau.verifier(questionCourante, reponses)) {
            questionCourante++;
            if (questionCourante >= niveau.getNombreQuestions()) {
                etat = EtatSession.REUSSI;
                return ResultatReponse.NIVEAU_REUSSI;
            }
            return ResultatReponse.BONNE_REPONSE;
        }
        essaisRestants--;
        if (essaisRestants <= 0) {
            etat = EtatSession.ECHOUE;
            return ResultatReponse.NIVEAU_ECHOUE;
        }
        return ResultatReponse.MAUVAISE_REPONSE;
    }

    public Niveau getNiveau() {
        return niveau;
    }

    /** Index (à partir de 0) de la question à laquelle l'élève doit répondre. */
    public int getQuestionCourante() {
        return questionCourante;
    }

    public int getEssaisRestants() {
        return essaisRestants;
    }

    public int getEssaisMax() {
        return essaisMax;
    }

    public EtatSession getEtat() {
        return etat;
    }

    public boolean estTerminee() {
        return etat != EtatSession.EN_COURS;
    }
}