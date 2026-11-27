package src.service;

/* Transforme un mot de passe avant son stockage et le compare ensuite.
 *
 * C'est le point d'extension prévu pour le hachage (Si je fini par comprendre comment on fait)
 */
public interface EncodeurMotDePasse {

    /* Valeur à stocker pour ce mot de passe. */
    String encoder(String motDePasse);

    /* Vrai si le mot de passe saisi correspond à la valeur stockée. */
    boolean correspond(String motDePasse, String valeurStockee);
}