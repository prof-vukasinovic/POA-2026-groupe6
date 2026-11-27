package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/*L'ensemble des niveaux du jeu, rangés par numéro, et les règles de déblocage.
 */
public class Parcours {

    private final List<Niveau> niveaux;

    /* Création du jeu. */
    public Parcours(List<Niveau> niveaux) {
        List<Niveau> tries = new ArrayList<>(niveaux);
        tries.sort(Comparator.comparingInt(Niveau::getNumero));
        Set<Integer> numeros = new HashSet<>();
        for (Niveau niveau : tries) {
            if (!numeros.add(niveau.getNumero())) {
                throw new IllegalArgumentException("Deux niveaux portent le numéro " + niveau.getNumero() + ".");
            }
        }
        this.niveaux = List.copyOf(tries);
    }

    /* Liste des niveaux. */
    public List<Niveau> getNiveaux() {
        return niveaux;
    }

    public int getNombreNiveaux() {
        return niveaux.size();
    }

    public boolean estVide() {
        return niveaux.isEmpty();
    }

    public Optional<Niveau> premier() {
        return niveaux.stream().findFirst();
    }

    public Optional<Niveau> getNiveau(int numero) {
        return niveaux.stream().filter(n -> n.getNumero() == numero).findFirst();
    }

    /** Premier niveau dont le numéro est strictement supérieur à celui donné. */
    public Optional<Niveau> suivantApres(int numero) {
        return niveaux.stream().filter(n -> n.getNumero() > numero).findFirst();
    }

    /** Le niveau où l'élève doit reprendre. */
    public Optional<Niveau> niveauAReprendre(Utilisateur utilisateur) {
        return suivantApres(utilisateur.getDernierNiveauTermine());
    }

    /** Un niveau est jouable s'il est déjà terminé ou si c'est celui à reprendre. */
    public boolean estDebloque(Utilisateur utilisateur, Niveau niveau) {
        if (niveau.getNumero() <= utilisateur.getDernierNiveauTermine()) {
            return true;
        }
        return niveauAReprendre(utilisateur)
                .map(aReprendre -> aReprendre.getNumero() == niveau.getNumero())
                .orElse(false);
    }

    public long nombreNiveauxTermines(Utilisateur utilisateur) {
        return niveaux.stream().filter(n -> n.getNumero() <= utilisateur.getDernierNiveauTermine()).count();
    }
}