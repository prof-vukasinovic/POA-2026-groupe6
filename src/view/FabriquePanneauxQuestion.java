package src.view;

import java.util.EnumMap;
import java.util.Map;

import src.model.*;

/* Associe chaque type de niveau au panneau Swing qui sait l'afficher.
 *
 * C'est le seul endroit de la vue qui "connaît" les types de niveaux. 
 * Pour ajouter un type, on écrit son panneau et on l'enregistre ici : PanneauNiveau n'est pas modifié.
 * Le "cast" ((NiveauQCM) niveau) est sûr : la table garantit qu'on n'appelle la fonction
 * que pour un niveau du bon type.
 */
public final class FabriquePanneauxQuestion {

    /** Construit le panneau de la question numéro {@code indexQuestion} d'un niveau. */
    @FunctionalInterface
    public interface Constructeur {
        PanneauQuestion creer(Niveau niveau, int indexQuestion);
    }

    private static final Map<TypeNiveau, Constructeur> CONSTRUCTEURS = new EnumMap<>(TypeNiveau.class);

    static {
        enregistrer(TypeNiveau.QCM, (niveau, i) -> new PanneauQCM(((NiveauQCM) niveau).getQuestion(i)));
        enregistrer(TypeNiveau.TEXTE_A_TROUS, (niveau, i) -> new PanneauTrous(((NiveauTXT) niveau).getQuestion(i)));
        enregistrer(TypeNiveau.CORRECTION_ERREUR,
                (niveau, i) -> new PanneauErreur(((NiveauErreur) niveau).getQuestion(i)));
    }

    private FabriquePanneauxQuestion() { }

    public static void enregistrer(TypeNiveau type, Constructeur constructeur) {
        CONSTRUCTEURS.put(type, constructeur);
    }

    public static PanneauQuestion creer(Niveau niveau, int indexQuestion) {
        Constructeur constructeur = CONSTRUCTEURS.get(niveau.getType());
        if (constructeur == null) {
            throw new IllegalStateException("Aucun panneau n'est prévu pour le type " + niveau.getType());
        }
        return constructeur.creer(niveau, indexQuestion);
    }
}