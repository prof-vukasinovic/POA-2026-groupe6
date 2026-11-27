package src.model;

import java.util.Objects;

/**
 * Un compte : pseudo, mot de passe, progression et score.
 *
 * La progression est représentée par le numéro du dernier niveau terminé (0 = aucun).
 * Elle ne peut qu'augmenter : rejouer un ancien niveau ne fait jamais reculer le joueur
 * (c'est ce qui permettra plus tard de "revenir à un niveau déjà terminé" sans rien perdre).
 *
 * Le champ motDePasse contient la valeur STOCKÉE : aujourd'hui le mot de passe en clair. 
 * (Pas de hachage je sais pas comment faire).
 */
public class Utilisateur {

    private final String pseudo;
    private final String motDePasse;
    private int dernierNiveauTermine;
    private int score;

    /** Nouveau compte : aucune progression, aucun point. */
    public Utilisateur(String pseudo, String motDePasse) {
        this(pseudo, motDePasse, 0, 0);
    }

    /** Compte existant (relu depuis le fichier). */
    public Utilisateur(String pseudo, String motDePasse, int dernierNiveauTermine, int score) {
        this.pseudo = Objects.requireNonNull(pseudo);
        this.motDePasse = Objects.requireNonNull(motDePasse);
        this.dernierNiveauTermine = Math.max(0, dernierNiveauTermine);
        this.score = Math.max(0, score);
    }

    /**
     * Enregistre la réussite d'un niveau.
     * Règle : Les points ne sont accordés qu'à la PREMIÈRE réussite (sinon on pourrait gagner des
     * points à l'infini en rejouant le niveau 1).
     */
    public boolean enregistrerReussite(int numeroNiveau, int points) {
        if (numeroNiveau <= dernierNiveauTermine) {
            return false;
        }
        dernierNiveauTermine = numeroNiveau;
        score += points;
        return true;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public int getDernierNiveauTermine() {
        return dernierNiveauTermine;
    }

    public int getScore() {
        return score;
    }
}