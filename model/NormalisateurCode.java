package model;

/**
 * Prépare un morceau de code pour une comparaison "tolérante" avec la réponse attendue.
 *
 * Sans cela, "int x=5;" et "int x = 5;" seraient jugés différents alors que c'est le même code.
 * Principe : on entoure chaque signe de ponctuation d'espaces, puis on réduit toutes les
 * suites d'espaces / retours à la ligne / tabulations à un seul espace.
 *
 *   "int x=5;"      ->  "int x = 5 ;"
 *   "int   x = 5 ;" ->  "int x = 5 ;"
 *
 * En revanche "intx = 5;" reste différent (l'espace entre deux mots compte) et la casse aussi.
 */
public final class NormalisateurCode {

    private NormalisateurCode() { }

    public static String normaliser(String texte) {
        if (texte == null) {
            return "";
        }
        return texte
                .replaceAll("\\p{Punct}", " $0 ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}