package src.persistence;

import java.util.List;

import src.model.Niveau;

/* Source des niveaux.
 */
public interface DepotNiveaux {
    List<Niveau> chargerTous() throws FormatNiveauException;
}