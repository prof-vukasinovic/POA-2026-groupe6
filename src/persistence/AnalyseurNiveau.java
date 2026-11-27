package src.persistence;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import src.model.*;

/**
 * Transforme le texte d'un fichier de niveau en objet.
 *
 * FORMAT DU FICHIER :
 *   - un premier bloc d'en-tête (TYPE, TITRE, LECON), puis un bloc par question ;
 *   - les blocs sont séparés par une ligne "---" ;
 *   - un champ s'écrit "CLE: valeur" (clé en MAJUSCULES) ; les lignes suivantes qui ne
 *     commencent pas par une clé prolongent la valeur (pratique pour du code sur plusieurs lignes) ;
 *   - une ligne qui commence par # est un commentaire.
 */
public class AnalyseurNiveau {

    private static final Pattern CHAMP = Pattern.compile("^([A-Z_]+):(.*)$");
    private static final String SEPARATEUR = "---";

    /** Construit un niveau d'un type donné à partir de l'en-tête et des blocs de questions. */
    @FunctionalInterface
    public interface FabriqueNiveau {
        Niveau creer(int numero, String titre, String lecon, List<BlocTexte> questions) throws FormatNiveauException;
    }

    /* Convertit un bloc en une question d'un type donné. */
    @FunctionalInterface
    private interface ConvertisseurBloc<Q> {
        Q convertir(BlocTexte bloc) throws FormatNiveauException;
    }

    private final Map<TypeNiveau, FabriqueNiveau> fabriques = new EnumMap<>(TypeNiveau.class);

    public AnalyseurNiveau() {
        enregistrer(TypeNiveau.QCM, AnalyseurNiveau::creerQCM);
        enregistrer(TypeNiveau.TEXTE_A_TROUS, AnalyseurNiveau::creerTXT);
        enregistrer(TypeNiveau.CORRECTION_ERREUR, AnalyseurNiveau::creerErreur);
    }

    public final void enregistrer(TypeNiveau type, FabriqueNiveau fabrique) {
        fabriques.put(type, fabrique);
    }

    /*
     * @param = numero numéro du niveau (déduit du nom du fichier)
     * @param = lignes contenu du fichier, ligne par ligne
     */
    public Niveau analyser(int numero, List<String> lignes) throws FormatNiveauException {
        List<BlocTexte> blocs = decouper(lignes);
        if (blocs.size() < 2) {
            throw new FormatNiveauException(
                    "il faut un en-tête puis au moins une question, séparés par une ligne « " + SEPARATEUR + " »");
        }
        BlocTexte entete = blocs.get(0);
        entete.verifierChampsAutorises(Set.of("TYPE", "TITRE", "LECON"));

        String code = entete.obligatoire("TYPE");
        TypeNiveau type = TypeNiveau.depuisCode(code).orElseThrow(() ->
                new FormatNiveauException("type de niveau inconnu « " + code + " » (QCM, TROUS ou ERREUR)"));
        FabriqueNiveau fabrique = fabriques.get(type);
        if (fabrique == null) {
            throw new FormatNiveauException("le type " + type.getCode() + " n'est pas pris en charge");
        }
        try {
            return fabrique.creer(numero, entete.obligatoire("TITRE"),
                    entete.optionnel("LECON").orElse(""), blocs.subList(1, blocs.size()));
        } catch (IllegalArgumentException e) {
            // Les classes du modèle refusent les données incohérentes avec ce type d'exception.
            throw new FormatNiveauException(e.getMessage(), e);
        }
    }

    // Découpage du texte

    static List<BlocTexte> decouper(List<String> lignes) throws FormatNiveauException {
        Decoupeur decoupeur = new Decoupeur();
        int numeroLigne = 0;
        for (String brute : lignes) {
            numeroLigne++;
            String ligne = brute.stripTrailing();
            if (ligne.startsWith("#")) {
                continue;
            }
            if (ligne.strip().equals(SEPARATEUR)) {
                decoupeur.fermerBloc();
                continue;
            }
            Matcher matcher = CHAMP.matcher(ligne);
            if (matcher.matches()) {
                decoupeur.ouvrirChamp(matcher.group(1), matcher.group(2).strip());
            } else if (decoupeur.champOuvert()) {
                decoupeur.prolonger(ligne);
            } else if (!ligne.isBlank()) {
                throw new FormatNiveauException("ligne " + numeroLigne
                        + " : texte en dehors d'un champ (on attend « CLE: valeur »)");
            }
        }
        decoupeur.fermerBloc();
        return decoupeur.blocs;
    }

    /* Petit automate qui accumule les lignes d'un champ puis les range dans le bloc courant. */
    private static final class Decoupeur {
        final List<BlocTexte> blocs = new ArrayList<>();
        private BlocTexte bloc = new BlocTexte();
        private String cle;
        private List<String> valeur = new ArrayList<>();

        boolean champOuvert() {
            return cle != null;
        }

        void ouvrirChamp(String nouvelleCle, String premiereLigne) {
            fermerChamp();
            cle = nouvelleCle;
            valeur = new ArrayList<>();
            valeur.add(premiereLigne);
        }

        void prolonger(String ligne) {
            valeur.add(ligne);
        }

        void fermerChamp() {
            if (cle == null) {
                return;
            }
            int debut = 0;
            int fin = valeur.size();
            while (debut < fin && valeur.get(debut).isBlank()) {
                debut++;
            }
            while (fin > debut && valeur.get(fin - 1).isBlank()) {
                fin--;
            }
            bloc.ajouter(cle, String.join("\n", valeur.subList(debut, fin)));
            cle = null;
        }

        void fermerBloc() {
            fermerChamp();
            if (!bloc.estVide()) {
                blocs.add(bloc);
                bloc = new BlocTexte();
            }
        }
    }

    // Une fabrique par type
    // QCM
    private static Niveau creerQCM(int numero, String titre, String lecon, List<BlocTexte> blocs)
            throws FormatNiveauException {
        List<NiveauQCM.Question> questions = convertir(blocs, bloc -> {
            bloc.verifierChampsAutorises(Set.of("QUESTION", "OPTION", "BONNE", "EXPLICATION"));
            int bonne = entier(bloc.obligatoire("BONNE"), "BONNE");
            return new NiveauQCM.Question(bloc.obligatoire("QUESTION"), bloc.tous("OPTION"),
                    bonne - 1, bloc.optionnel("EXPLICATION").orElse(""));
        });
        return new NiveauQCM(numero, titre, lecon, questions);
    }
    
    // TXT
    private static Niveau creerTXT(int numero, String titre, String lecon, List<BlocTexte> blocs)
            throws FormatNiveauException {
        List<NiveauTXT.Question> questions = convertir(blocs, bloc -> {
            bloc.verifierChampsAutorises(Set.of("CONSIGNE", "ENONCE", "REPONSE", "EXPLICATION"));
            List<List<String>> reponses = new ArrayList<>();
            for (String ligne : bloc.tous("REPONSE")) {
                // "i++ | i += 1" : plusieurs réponses acceptées pour un même trou
                List<String> acceptees = new ArrayList<>();
                for (String possibilite : ligne.split("\\|")) {
                    if (!possibilite.isBlank()) {
                        acceptees.add(possibilite.strip());
                    }
                }
                reponses.add(acceptees);
            }
            return new NiveauTXT.Question(bloc.optionnel("CONSIGNE").orElse("Complète le code."),
                    bloc.obligatoire("ENONCE"), reponses, bloc.optionnel("EXPLICATION").orElse(""));
        });
        return new NiveauTXT(numero, titre, lecon, questions);
    }

    //Erreur
    private static Niveau creerErreur(int numero, String titre, String lecon, List<BlocTexte> blocs)
            throws FormatNiveauException {
        List<NiveauErreur.Question> questions = convertir(blocs, bloc -> {
            bloc.verifierChampsAutorises(Set.of("CONSIGNE", "ENONCE", "CORRECTION", "EXPLICATION"));
            return new NiveauErreur.Question(
                    bloc.optionnel("CONSIGNE").orElse("Ce code contient une erreur. Corrige-la."),
                    bloc.obligatoire("ENONCE"), bloc.tous("CORRECTION"),
                    bloc.optionnel("EXPLICATION").orElse(""));
        });
        return new NiveauErreur(numero, titre, lecon, questions);
    }

    /** Convertit tous les blocs en questions, en ajoutant « question N : » aux messages d'erreur. */
    private static <Q> List<Q> convertir(List<BlocTexte> blocs, ConvertisseurBloc<Q> convertisseur)
            throws FormatNiveauException {
        List<Q> questions = new ArrayList<>();
        for (int i = 0; i < blocs.size(); i++) {
            try {
                questions.add(convertisseur.convertir(blocs.get(i)));
            } catch (FormatNiveauException | IllegalArgumentException e) {
                throw new FormatNiveauException("question " + (i + 1) + " : " + e.getMessage(), e);
            }
        }
        return questions;
    }

    /* NiveauQCM.Question attend un int */
    private static int entier(String texte, String champ) throws FormatNiveauException {
        try {
            return Integer.parseInt(texte.trim());
        } catch (NumberFormatException e) {
            throw new FormatNiveauException("le champ " + champ + " doit être un nombre (reçu : « " + texte + " »)");
        }
    }
}