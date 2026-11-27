import java.nio.file.Path;

/**
 * Toutes les "règles du jeu" et les chemins de fichiers au même endroit.
 *
 * Pourquoi une classe dédiée ? Changer le nombre d'essais ou les points par niveau ne modifie qu'une ligne.
 */
public final class Configuration {

    private Configuration() { }

    /** Nombre d'erreurs autorisées pour tout un niveau (toutes questions confondues). */
    public static final int ESSAIS_PAR_NIVEAU = 3;

    /** Points gagnés la première fois qu'on termine un niveau. */
    public static final int POINTS_PAR_NIVEAU = 10;

    public static final int PSEUDO_LONGUEUR_MIN = 3;
    public static final int PSEUDO_LONGUEUR_MAX = 20;
    public static final int MOT_DE_PASSE_LONGUEUR_MIN = 4;

    /** Nombre de joueurs affichés dans le classement. */
    public static final int TAILLE_CLASSEMENT = 10;

    /** Dossier contenant les fichiers niveau1.txt, niveau2.txt, ... */
    public static final Path DOSSIER_NIVEAUX = Path.of("niveaux");

    /** Fichier texte contenant les comptes (pseudo, mot de passe, progression, score). */
    public static final Path FICHIER_UTILISATEURS = Path.of("donnees", "utilisateurs.properties");
}