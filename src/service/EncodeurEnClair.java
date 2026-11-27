package src.service;

/* Le mot de passe est stocké tel quel (pas de hachage). */
public class EncodeurEnClair implements EncodeurMotDePasse {

    @Override
    public String encoder(String motDePasse) {
        return motDePasse;
    }

    @Override
    public boolean correspond(String motDePasse, String valeurStockee) {
        return motDePasse != null && motDePasse.equals(valeurStockee);
    }
}