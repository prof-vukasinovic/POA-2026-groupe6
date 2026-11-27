package src.persistence;

import java.util.List;
import java.util.Optional;

import src.model.Utilisateur;

/* Là où sont rangés les comptes. 
 * Le reste du programme ne connaît que cette interface.
 */
public interface DepotUtilisateurs {

    /** Cherche un compte */
    Optional<Utilisateur> trouver(String pseudo);

    /** Crée ou met à jour un compte */
    void enregistrer(Utilisateur utilisateur);

    List<Utilisateur> tous();
}