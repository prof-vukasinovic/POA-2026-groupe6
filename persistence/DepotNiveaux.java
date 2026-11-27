package persistence;

import model.Niveau;

import java.util.List;

/* Source des niveaux.
 */
public interface DepotNiveaux {
    List<Niveau> chargerTous() throws FormatNiveauException;
}