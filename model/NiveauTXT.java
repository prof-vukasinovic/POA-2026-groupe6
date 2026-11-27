package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Niveau de type "texte à trous" : un morceau de code contient un ou plusieurs trous
 * (noté "___") que l'élève doit compléter.
 *
 * Pour chaque trou, on peut accepter plusieurs réponses (ex : "i++" ou "i += 1").
 * La réponse attendue est une liste : un texte par trou, dans l'ordre.
 */
public class NiveauTXT extends Niveau {

    /** Ce qui représente un trou dans l'énoncé. */
    public static final String MARQUEUR_TROU = "___";

    /* Même principe que QCM. */
    public record Question(String consigne, String enonce, List<List<String>> reponsesAcceptees, String explication) {
        public Question {
            Objects.requireNonNull(enonce, "L'énoncé est obligatoire.");
            Objects.requireNonNull(reponsesAcceptees, "Les réponses sont obligatoires.");
            consigne = consigne == null ? "" : consigne;
            explication = explication == null ? "" : explication;

            // Attention spoiler alert : il faut qu'il y est des trous dans un niveau à trous
            int trous = compterTrous(enonce);
            if (trous == 0) {
                throw new IllegalArgumentException("L'énoncé doit contenir au moins un trou (" + MARQUEUR_TROU + ").");
            }
            if (trous != reponsesAcceptees.size()) {
                throw new IllegalArgumentException("L'énoncé contient " + trous + " trou(s) mais "
                        + reponsesAcceptees.size() + " réponse(s) sont données.");
            }
            // Copie profonde pour que la question soit réellement immuable.
            List<List<String>> copie = new ArrayList<>();
            for (List<String> acceptees : reponsesAcceptees) {
                if (acceptees == null || acceptees.isEmpty()) {
                    throw new IllegalArgumentException("Chaque trou doit avoir au moins une réponse acceptée.");
                }
                copie.add(List.copyOf(acceptees));
            }
            reponsesAcceptees = List.copyOf(copie);
        }

        public int nombreTrous() {
            return reponsesAcceptees.size();
        }
    }

    /*Liste des Questions du niveau. */
    private final List<Question> questions;

    /* NiveauTXT est un Niveau. */
    public NiveauTXT(int numero, String titre, String lecon, List<Question> questions) {
        super(numero, titre, lecon);
        exigerQuestions(questions);
        this.questions = List.copyOf(questions);
    }

    /** Compte les trous d'un énoncé. */
    public static int compterTrous(String enonce) {
        int compteur = 0;
        int position = enonce.indexOf(MARQUEUR_TROU);
        while (position >= 0) {
            compteur++;
            position = enonce.indexOf(MARQUEUR_TROU, position + MARQUEUR_TROU.length());
        }
        return compteur;
    }

    public Question getQuestion(int index) {
        verifierIndex(index);
        return questions.get(index);
    }

    @Override
    public TypeNiveau getType() {
        return TypeNiveau.TEXTE_A_TROUS;
    }

    @Override
    public int getNombreQuestions() {
        return questions.size();
    }

    @Override
    public boolean verifier(int indexQuestion, List<String> reponses) {
        Question question = getQuestion(indexQuestion);
        if (reponses == null || reponses.size() != question.nombreTrous()) {
            return false;
        }
        for (int trou = 0; trou < question.nombreTrous(); trou++) {
            String saisie = NormalisateurCode.normaliser(reponses.get(trou));
            boolean accepte = question.reponsesAcceptees().get(trou).stream()
                    .map(NormalisateurCode::normaliser)
                    .anyMatch(saisie::equals);
            if (!accepte) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getExplication(int indexQuestion) {
        return getQuestion(indexQuestion).explication();
    }
}