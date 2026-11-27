package src.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Un bloc d'un fichier de niveau = un ensemble de champs "CLE: valeur".
 * Une même clé peut apparaître plusieurs fois (ex : OPTION pour un QCM).
 */
public final class BlocTexte {

    private final Map<String, List<String>> champs = new LinkedHashMap<>();

    void ajouter(String cle, String valeur) {
        champs.computeIfAbsent(cle, k -> new ArrayList<>()).add(valeur);
    }

    boolean estVide() {
        return champs.isEmpty();
    }

    /** Valeur d'un champ qui doit être présent (la première s'il y en a plusieurs). */
    public String obligatoire(String cle) throws FormatNiveauException {
        List<String> valeurs = champs.get(cle);
        if (valeurs == null || valeurs.get(0).isBlank()) {
            throw new FormatNiveauException("le champ " + cle + " est obligatoire");
        }
        return valeurs.get(0);
    }

    /* Un niveau sans énoncé n'a pas de sens, alors qu'un niveau sans explication reste jouable. */
    public Optional<String> optionnel(String cle) {
        List<String> valeurs = champs.get(cle);
        if (valeurs == null || valeurs.get(0).isBlank()) {
            return Optional.empty();
        }
        return Optional.of(valeurs.get(0));
    }

    /** Toutes les valeurs d'un champ répétable (liste vide s'il est absent). */
    public List<String> tous(String cle) {
        return Collections.unmodifiableList(champs.getOrDefault(cle, List.of()));
    }

    /** Détecte les fautes de frappe : une clé inconnue (ex : "OPTIONS:") est signalée. */
    public void verifierChampsAutorises(Set<String> autorises) throws FormatNiveauException {
        for (String cle : champs.keySet()) {
            if (!autorises.contains(cle)) {
                throw new FormatNiveauException("champ inconnu « " + cle + " » (champs possibles : "
                        + String.join(", ", autorises) + ")");
            }
        }
    }
}