package model;

import java.util.List;

/**
 * Un niveau du jeu : classe abstraite commune à tous les types de niveaux.
 *
 * Un niveau contient une leçon (le petit cours affiché en haut de l'écran) et une ou
 * plusieurs questions.
 *
 * Ce que le reste du programme a besoin de savoir (combien de questions ? la réponse
 * est-elle bonne ?) est exprimé par les méthodes abstraites.
 *
 * Une réponse est toujours une liste de textes :
 *   - QCM : 1 élément = le texte de l'option choisie
 *   - TXT : 1 élément par trou, dans l'ordre
 *   - Erreur : 1 élément = le code complet corrigé
 * Un futur niveau "compilable" aurait lui aussi 1 élément : le code de l'élève. (Si on arrive à le faire, sinon ligne à supprimer)
 */
public abstract class Niveau {

    private final int numero;
    private final String titre;
    private final String lecon;

    protected Niveau(int numero, String titre, String lecon) {
        /* Les niveaux ont un titre et un numéro sinon il y a eu boulette. */
        if (numero < 1) {
            throw new IllegalArgumentException("Le numéro d'un niveau doit être supérieur ou égal à 1.");
        }
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Un niveau doit avoir un titre.");
        }
        this.numero = numero;
        this.titre = titre.strip();
        /*Il n'est pas obligatoire d'avoir un cours.*/
        this.lecon = lecon == null ? "" : lecon;
    }

    public int getNumero() {
        return numero;
    }

    public String getTitre() {
        return titre;
    }

    /** Le petit cours affiché au-dessus des questions (peut être vide). */
    public String getLecon() {
        return lecon;
    }

    public abstract TypeNiveau getType();

    public abstract int getNombreQuestions();

    /** Vrai si la réponse donnée est correcte pour la question d'index donné (0 = première). */
    /* La réponse colle à la question. */
    public abstract boolean verifier(int indexQuestion, List<String> reponses);

    /** Texte affiché après une bonne réponse (peut être vide). */
    public abstract String getExplication(int indexQuestion);

    /** Utilitaire pour les sous-classes : refuse un index de question hors limites. */
    protected void verifierIndex(int indexQuestion) {
        if (indexQuestion < 0 || indexQuestion >= getNombreQuestions()) {
            throw new IndexOutOfBoundsException("Question inexistante : " + indexQuestion);
        }
    }

    /** Utilitaire pour les sous-classes */
    /*Un niveau sans question n'a pas de sens. Abération */
    protected static void exigerQuestions(List<?> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Un niveau doit contenir au moins une question.");
        }
    }
}