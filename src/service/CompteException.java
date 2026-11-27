package src.service;

/** Problème de compte (pseudo invalide, mot de passe faux...). Le message est affiché à l'utilisateur. */
public class CompteException extends Exception {

    private static final long serialVersionUID = 1L;

    public CompteException(String message) {
        super(message);
    }
}