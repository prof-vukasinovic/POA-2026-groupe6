package model;

import java.util.Optional;

/**
 * Les différents types de niveaux. Chaque type possède un "code" (c'est le mot qu'on écrit
 * après "TYPE:" dans un fichier de niveau).
 *
 * Pour ajouter un type (ex : niveau avec compilation), on ajoute une valeur ici,
 * puis une sous-classe de {@link Niveau}, un analyseur de fichier et un panneau Swing.
 */
public enum TypeNiveau {
    QCM("QCM"),
    TEXTE_A_TROUS("TROUS"),
    CORRECTION_ERREUR("ERREUR");

    private final String code;

    TypeNiveau(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /** Retrouve un type à partir du mot écrit dans le fichier. */
    public static Optional<TypeNiveau> depuisCode(String code) {
        if (code == null) {
            return Optional.empty();
        }
        for (TypeNiveau type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}