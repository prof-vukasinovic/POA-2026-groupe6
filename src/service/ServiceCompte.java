package src.service;

import src.Configuration;
import src.model.Utilisateur;
import src.persistence.DepotUtilisateurs;

import java.util.regex.Pattern;

/* Règles de gestion des comptes : création (avec validation) et connexion.
 * Ne contient aucun code Swing ni aucune lecture de fichier (utilise des interfaces)
 */
public class ServiceCompte {

    private static final Pattern PSEUDO_VALIDE = Pattern.compile("[A-Za-z0-9_-]+");

    private final DepotUtilisateurs depot;
    private final EncodeurMotDePasse encodeur;

    public ServiceCompte(DepotUtilisateurs depot, EncodeurMotDePasse encodeur) {
        this.depot = depot;
        this.encodeur = encodeur;
    }

    public Utilisateur creerCompte(String pseudo, String motDePasse) throws CompteException {
        String pseudoPropre = pseudo == null ? "" : pseudo.strip();
        validerPseudo(pseudoPropre);
        validerMotDePasse(motDePasse);
        if (depot.trouver(pseudoPropre).isPresent()) {
            throw new CompteException("Ce pseudo est déjà utilisé.");
        }
        Utilisateur utilisateur = new Utilisateur(pseudoPropre, encodeur.encoder(motDePasse));
        depot.enregistrer(utilisateur);
        return utilisateur;
    }

    public Utilisateur connecter(String pseudo, String motDePasse) throws CompteException {
        String pseudoPropre = pseudo == null ? "" : pseudo.strip();
        // Même message dans les deux cas : on ne révèle pas si le pseudo existe.
        return depot.trouver(pseudoPropre)
                .filter(u -> encodeur.correspond(motDePasse, u.getMotDePasse()))
                .orElseThrow(() -> new CompteException("Pseudo ou mot de passe incorrect."));
    }

    /** Persiste les changements faits sur un compte (progression, score). */
    public void sauvegarder(Utilisateur utilisateur) {
        depot.enregistrer(utilisateur);
    }

    private void validerPseudo(String pseudo) throws CompteException {
        if (pseudo.length() < Configuration.PSEUDO_LONGUEUR_MIN
                || pseudo.length() > Configuration.PSEUDO_LONGUEUR_MAX) {
            throw new CompteException("Le pseudo doit contenir entre " + Configuration.PSEUDO_LONGUEUR_MIN
                    + " et " + Configuration.PSEUDO_LONGUEUR_MAX + " caractères.");
        }
        if (!PSEUDO_VALIDE.matcher(pseudo).matches()) {
            throw new CompteException("Le pseudo ne peut contenir que des lettres, chiffres, « _ » et « - ».");
        }
    }

    private void validerMotDePasse(String motDePasse) throws CompteException {
        if (motDePasse == null || motDePasse.length() < Configuration.MOT_DE_PASSE_LONGUEUR_MIN) {
            throw new CompteException("Le mot de passe doit contenir au moins "
                    + Configuration.MOT_DE_PASSE_LONGUEUR_MIN + " caractères.");
        }
    }
}