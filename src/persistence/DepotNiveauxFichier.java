package src.persistence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import src.model.Niveau;

/* Lit tous les fichiers "niveau<N>.txt" d'un dossier (niveau1.txt, niveau02.txt, niveau12.txt...).
 * 
 * Le numéro du niveau est le nombre contenu dans le nom du fichier : pour ajouter un niveau,
 * il suffit de déposer un nouveau fichier dans le dossier, sans modifier ni recompiler le code.
 */
public class DepotNiveauxFichier implements DepotNiveaux {

    /* Expression régulière qui décrit un nom de fichier valide :
    *   (?i)      = insensible à la casse (Niveau1.TXT accepté)
    *   ^niveau   = commence par "niveau"
    *   (\\d+)    = un ou plusieurs chiffres, "capturés" dans un groupe (c'est le numéro du niveau)
    *   \\.txt$   = se termine par ".txt"
    * Elle est static final : on la compile une seule fois pour toute la classe.
    */
    private static final Pattern NOM_FICHIER = Pattern.compile("(?i)^niveau(\\d+)\\.txt$");

    private final Path dossier; // où chercher les fichiers
    private final AnalyseurNiveau analyseur;

    // On récupère les informations
    public DepotNiveauxFichier(Path dossier, AnalyseurNiveau analyseur) {
        this.dossier = dossier;
        this.analyseur = analyseur;
    }

    @Override
    public List<Niveau> chargerTous() throws FormatNiveauException {
        // Pas de dossier : on renvoie une liste vide au lieu de planter.
        if (!Files.isDirectory(dossier)) {
            return List.of();
        }

        // ÉTAPE 1 : établir la liste des fichiers à lire
        List<Path> fichiers;
        try (Stream<Path> flux = Files.list(dossier)) {
            // On ne garde que les fichiers dont le nom correspond au motif
            // Tri par nom, pour un ordre de lecture stable
            fichiers = flux.filter(p -> NOM_FICHIER.matcher(p.getFileName().toString()).matches())
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
        } catch (IOException e) {
            throw new FormatNiveauException("impossible de lire le dossier " + dossier + " : " + e.getMessage(), e);
        }

        // ÉTAPE 2 : lire et analyser chaque fichier
        List<Niveau> niveaux = new ArrayList<>();
        for (Path fichier : fichiers) {
            String nom = fichier.getFileName().toString();

            // On repasse le nom dans l'expression régulière pour récupérer le groupe (\\d+).
            // On sait que ça réussit car le fichier a déjà passé le filtre de l'étape 1.
            Matcher matcher = NOM_FICHIER.matcher(nom);
            matcher.matches();
            
            try {
                int numero = Integer.parseInt(matcher.group(1));
                // Lecture de toutes les lignes, en UTF-8 pour garder les accents
                List<String> lignes = Files.readAllLines(fichier, StandardCharsets.UTF_8);
                // L'analyseur construit le bon type de Niveau                
                niveaux.add(analyseur.analyser(numero, lignes));

            } catch (FormatNiveauException e) {
                // Le fichier est mal écrit
                throw new FormatNiveauException(nom + " : " + e.getMessage(), e);
            } catch (IOException | NumberFormatException e) {
                // IOException : lecture impossible
                throw new FormatNiveauException(nom + " : lecture impossible (" + e.getMessage() + ")", e);
            }
        }
        return niveaux;
    }
}