package src.model;

import java.util.List;
import java.util.Objects;

/**
 * Niveau de type "correction d'erreurs" : on montre un code qui contient une (ou plusieurs)
 * erreur(s), l'élève le corrige directement dans une zone de texte.
 *
 * La réponse attendue est une liste d'un seul élément : le code complet corrigé. 
 *Il est comparé (après normalisation) à une ou plusieurs corrections acceptées.
 */
public class NiveauErreur extends Niveau {
    /* Même principe que QCM. */
    public record Question(String consigne, String codeErrone, List<String> corrections, String explication) {
        public Question {
            Objects.requireNonNull(codeErrone, "Le code à corriger est obligatoire.");
            Objects.requireNonNull(corrections, "Les corrections sont obligatoires.");
            if (corrections.isEmpty()) {
                throw new IllegalArgumentException("Il faut au moins une correction acceptée.");
            }
            consigne = consigne == null ? "" : consigne;
            explication = explication == null ? "" : explication;
            corrections = List.copyOf(corrections);
        }
    }

    /* Liste des questions. */
    private final List<Question> questions;

    /* NiveauErreur est un Niveau. */
    public NiveauErreur(int numero, String titre, String lecon, List<Question> questions) {
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
        return TypeNiveau.CORRECTION_ERREUR;
    }

    @Override
    public int getNombreQuestions() {
        return questions.size();
    }

    @Override
    public boolean verifier(int indexQuestion, List<String> reponses) {
        Question question = getQuestion(indexQuestion);
        if (reponses == null || reponses.size() != 1) {
            return false;
        }
        String saisie = NormalisateurCode.normaliser(reponses.get(0));
        return question.corrections().stream()
                .map(NormalisateurCode::normaliser)
                .anyMatch(saisie::equals);
    }

    @Override
    public String getExplication(int indexQuestion) {
        return getQuestion(indexQuestion).explication();
    }
}