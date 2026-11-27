package model;

import java.util.List;
import java.util.Objects;

/**
 * Niveau de type QCM : une seule bonne réponse.
 * La réponse attendue est une liste d'un seul élément : le texte de l'option choisie.
 */
public class NiveauQCM extends Niveau {

    /**
     * Une question de QCM. Un "record" est un truc déjà construit dans java donc il faut vérifier les données à la création.
     */
    public record Question(String enonce, List<String> options, int indexBonneReponse, String explication) {
        public Question {
            Objects.requireNonNull(enonce, "L'énoncé est obligatoire.");
            Objects.requireNonNull(options, "Les options sont obligatoires.");
            if (options.size() < 2) {
                throw new IllegalArgumentException("Un QCM doit proposer au moins 2 options.");
            }
            if (indexBonneReponse < 0 || indexBonneReponse >= options.size()) {
                throw new IllegalArgumentException(
                        "La bonne réponse doit être un numéro entre 1 et " + options.size() + ".");
            }
            options = List.copyOf(options);
            explication = explication == null ? "" : explication;
        }

        public String bonneReponse() {
            return options.get(indexBonneReponse);
        }
    }

    /*Liste des Questions du niveau. */
    private final List<Question> questions;

    /* NiveauQCM est un Niveau. */
    public NiveauQCM(int numero, String titre, String lecon, List<Question> questions) {
        super(numero, titre, lecon);
        exigerQuestions(questions);
        this.questions = List.copyOf(questions);
    }

    public Question getQuestion(int index) {
        verifierIndex(index);
        return questions.get(index);
    }

    @Override
    public TypeNiveau getType() {
        return TypeNiveau.QCM;
    }

    @Override
    public int getNombreQuestions() {
        return questions.size();
    }

    @Override
    public boolean verifier(int indexQuestion, List<String> reponses) {
        Question question = getQuestion(indexQuestion);
        return reponses != null
                && reponses.size() == 1
                && question.bonneReponse().equals(reponses.get(0));
    }

    @Override
    public String getExplication(int indexQuestion) {
        return getQuestion(indexQuestion).explication();
    }
}