package persistence;

/* Erreur dans un fichier de niveau (champ manquant, type inconnu...). 
* Le message est écrit pour l'auteur du niveau. (Correction)
*/
public class FormatNiveauException extends Exception {

    private static final long serialVersionUID = 1L;

    public FormatNiveauException(String message) {
        super(message);
    }

    public FormatNiveauException(String message, Throwable cause) {
        super(message, cause);
    }
}