package src.persistence;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import src.model.Utilisateur;

/* Stocke les comptes dans un fichier texte au format Properties :
 *
 *   alice.pseudo=Alice
 *   alice.motDePasse=secret
 *   alice.dernierNiveau=3
 *   alice.score=30
 *
 * Le fichier est lu une fois au démarrage, puis réécrit en entier à chaque modification
 * (suffisant pour un usage local avec peu de comptes). Le format Properties s'occupe tout seul
 * des caractères spéciaux (accents, '=', ':'...) dans les mots de passe.
 */
public class DepotUtilisateursFichier implements DepotUtilisateurs {

    private static final String SUFFIXE_PSEUDO = ".pseudo";

    private final Path fichier;
    private final Map<String, Utilisateur> parCle = new LinkedHashMap<>();

    public DepotUtilisateursFichier(Path fichier) {
        this.fichier = fichier;
        charger();
    }

    @Override
    public Optional<Utilisateur> trouver(String pseudo) {
        return Optional.ofNullable(parCle.get(cle(pseudo)));
    }

    @Override
    public void enregistrer(Utilisateur utilisateur) {
        parCle.put(cle(utilisateur.getPseudo()), utilisateur);
        sauvegarder();
    }

    @Override
    public List<Utilisateur> tous() {
        return new ArrayList<>(parCle.values());
    }

    private static String cle(String pseudo) {
        return pseudo.strip().toLowerCase(Locale.ROOT);
    }

    private void charger() {
        if (!Files.exists(fichier)) {
            return;
        }
        Properties proprietes = new Properties();
        try (Reader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            proprietes.load(lecteur);
        } catch (IOException e) {
            throw new UncheckedIOException("Lecture impossible de " + fichier, e);
        }
        for (String nomPropriete : proprietes.stringPropertyNames()) {
            if (!nomPropriete.endsWith(SUFFIXE_PSEUDO)) {
                continue;
            }
            String prefixe = nomPropriete.substring(0, nomPropriete.length() - SUFFIXE_PSEUDO.length());
            Utilisateur utilisateur = new Utilisateur(
                    proprietes.getProperty(nomPropriete),
                    proprietes.getProperty(prefixe + ".motDePasse", ""),
                    entier(proprietes.getProperty(prefixe + ".dernierNiveau")),
                    entier(proprietes.getProperty(prefixe + ".score")));
            parCle.put(cle(utilisateur.getPseudo()), utilisateur);
        }
    }

    private void sauvegarder() {
        Properties proprietes = new Properties();
        for (Utilisateur u : parCle.values()) {
            String prefixe = cle(u.getPseudo());
            proprietes.setProperty(prefixe + SUFFIXE_PSEUDO, u.getPseudo());
            proprietes.setProperty(prefixe + ".motDePasse", u.getMotDePasse());
            proprietes.setProperty(prefixe + ".dernierNiveau", String.valueOf(u.getDernierNiveauTermine()));
            proprietes.setProperty(prefixe + ".score", String.valueOf(u.getScore()));
        }
        try {
            Path parent = fichier.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (Writer ecrivain = Files.newBufferedWriter(fichier, StandardCharsets.UTF_8)) {
                proprietes.store(ecrivain, "Comptes JavaQuest");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Écriture impossible de " + fichier, e);
        }
    }

    private static int entier(String texte) {
        try {
            return texte == null ? 0 : Integer.parseInt(texte.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}