package src.view;

import javax.swing.JComponent;
import java.util.List;

/* Ce que PanneauNiveau attend de n'importe quel panneau de question.
 */
public interface PanneauQuestion {

    /* Le composant Swing à afficher. */
    JComponent composant();

    /* Vrai si l'élève a rempli sa réponse. */
    boolean aUneReponse();

    /* La réponse de l'élève, sous la forme attendue par {@code Niveau.verifier}. */
    List<String> getReponses();

    /* Autorise ou bloque la saisie (bloquée après une bonne réponse). */
    void setEditable(boolean editable);
}