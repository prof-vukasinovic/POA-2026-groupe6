package src.view;

import javax.swing.*;

import src.controller.ControleurApplication;
import src.model.Niveau;
import src.model.ResultatReponse;
import src.model.SessionNiveau;

import java.awt.*;

/* L'écran de jeu, identique pour tous les types de niveaux :
 *   - en haut : titre, barre de progression, cœurs (essais restants), bouton Quitter ;
 *   - au centre : la leçon puis la question (panneau fourni par FabriquePanneauxQuestion) ;
 *   - en bas : une barre de réaction (bravo / raté) avec un bouton d'action.
 *
 * Le bouton du bas change de rôle selon la situation (Valider, Continuer, Réessayer...) :
 * on mémorise simplement "ce qu'il doit faire" dans {@code actionBouton}.
 * Ce panneau ne modifie jamais le modèle : il appelle le contrôleur.
 */
public class PanneauNiveau extends JPanel {

    private static final long serialVersionUID = 1L;

    private final ControleurApplication controleur;

    private final JLabel titre = Theme.etiquette(" ", Theme.SOUS_TITRE, Theme.TEXTE);
    private final JLabel compteur = Theme.etiquette(" ", Theme.PETIT, Theme.TEXTE_DISCRET);
    private final BarreProgression progression = new BarreProgression();
    private final IndicateurVies vies = new IndicateurVies();

    private final PanneauDefilant colonne = new PanneauDefilant(new GridBagLayout());
    private final JScrollPane defilement = new JScrollPane(colonne);

    private final JPanel barreBas = new JPanel(new BorderLayout(20, 0));
    private final JLabel retour = new JLabel(" ");
    private final BoutonArrondi bouton = new BoutonArrondi("Valider", Theme.ACCENT);
    private Runnable actionBouton = () -> { };

    private SessionNiveau session;
    private PanneauQuestion panneauQuestion;

    public PanneauNiveau(ControleurApplication controleur) {
        this.controleur = controleur;
        setBackground(Theme.FOND);
        setLayout(new BorderLayout());

        add(construireEntete(), BorderLayout.NORTH);

        defilement.setBorder(null);
        defilement.setOpaque(false);
        defilement.getViewport().setOpaque(false);
        defilement.getVerticalScrollBar().setUnitIncrement(20);
        add(defilement, BorderLayout.CENTER);

        retour.setFont(Theme.TEXTE_NORMAL);
        barreBas.setBorder(BorderFactory.createEmptyBorder(16, 32, 16, 32));
        barreBas.add(retour, BorderLayout.CENTER);
        barreBas.add(bouton, BorderLayout.EAST);
        add(barreBas, BorderLayout.SOUTH);

        bouton.addActionListener(e -> actionBouton.run());
    }

    private JPanel construireEntete() {
        JPanel entete = new JPanel(new BorderLayout(24, 0));
        entete.setBackground(Theme.CARTE);
        entete.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));

        JPanel gauche = new JPanel();
        gauche.setOpaque(false);
        gauche.setLayout(new BoxLayout(gauche, BoxLayout.Y_AXIS));
        titre.setAlignmentX(Component.LEFT_ALIGNMENT);
        compteur.setAlignmentX(Component.LEFT_ALIGNMENT);
        gauche.add(titre);
        gauche.add(compteur);
        entete.add(gauche, BorderLayout.WEST);

        JPanel milieu = new JPanel(new GridBagLayout());
        milieu.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        milieu.add(progression, c);
        entete.add(milieu, BorderLayout.CENTER);

        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        droite.setOpaque(false);
        BoutonArrondi quitter = new BoutonArrondi("Quitter", Theme.CARTE_CLAIRE);
        quitter.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        quitter.addActionListener(e -> controleur.afficherMenu());
        droite.add(vies);
        droite.add(quitter);
        entete.add(droite, BorderLayout.EAST);
        return entete;
    }

    // Appelé par la fenêtre

    /** Affiche la question courante de la session (nouveau niveau ou question suivante). */
    public void afficherQuestion(SessionNiveau nouvelleSession) {
        session = nouvelleSession;
        Niveau niveau = session.getNiveau();
        titre.setText("Niveau " + niveau.getNumero() + " : " + niveau.getTitre());
        mettreAJourEntete();

        panneauQuestion = FabriquePanneauxQuestion.creer(niveau, session.getQuestionCourante());

        JPanel contenu = new JPanel();
        contenu.setOpaque(false);
        contenu.setLayout(new BoxLayout(contenu, BoxLayout.Y_AXIS));
        if (!niveau.getLecon().isBlank()) {
            contenu.add(construireCarteLecon(niveau.getLecon()));
            contenu.add(Box.createVerticalStrut(24));
        }
        JComponent composant = panneauQuestion.composant();
        composant.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenu.add(composant);

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(24, 24, 24, 24);
        colonne.removeAll();
        colonne.add(contenu, c);
        colonne.revalidate();
        colonne.repaint();
        SwingUtilities.invokeLater(() -> defilement.getVerticalScrollBar().setValue(0));

        passerEnSaisie("Réponds à la question, puis clique sur « Valider ».");
    }

    /* Réagit au résultat d'une validation. */
    public void afficherRetour(ResultatReponse resultat, SessionNiveau sessionCourante, String explication) {
        session = sessionCourante;
        mettreAJourEntete();
        String suite = explication == null || explication.isBlank() ? "" : "\n" + explication;

        switch (resultat) {
            case BONNE_REPONSE -> {
                panneauQuestion.setEditable(false);
                definirBarre(Theme.FOND_SUCCES, Theme.VERT, "Bonne réponse !" + suite);
                configurerBouton("Continuer", Theme.VERT, controleur::continuer);
            }
            case NIVEAU_REUSSI -> {
                panneauQuestion.setEditable(false);
                definirBarre(Theme.FOND_SUCCES, Theme.VERT, "Bonne réponse ! Le niveau est terminé." + suite);
                configurerBouton("Voir le bilan", Theme.VERT, controleur::continuer);
            }
            case MAUVAISE_REPONSE -> {
                definirBarre(Theme.FOND_ECHEC, Theme.ROUGE, "Ce n'est pas la bonne réponse. Il te reste "
                        + sessionCourante.getEssaisRestants() + " essai" + (sessionCourante.getEssaisRestants() > 1 ? "s" : "") + ".");
                configurerBouton("Réessayer", Theme.ROUGE,
                        () -> passerEnSaisie("Corrige ta réponse, puis clique sur « Valider »."));
            }
            case NIVEAU_ECHOUE -> {
                panneauQuestion.setEditable(false);
                definirBarre(Theme.FOND_ECHEC, Theme.ROUGE, "Ce n'est pas la bonne réponse, et tu n'as plus d'essais.");
                configurerBouton("Voir le bilan", Theme.ROUGE, controleur::continuer);
            }
        }
    }

    // Détails internes

    private void passerEnSaisie(String message) {
        panneauQuestion.setEditable(true);
        definirBarre(Theme.CARTE, Theme.TEXTE_DISCRET, message);
        configurerBouton("Valider", Theme.ACCENT, this::valider);
    }

    private void valider() {
        if (!panneauQuestion.aUneReponse()) {
            definirBarre(Theme.CARTE, Theme.JAUNE, "Écris ta réponse avant de valider (cela ne coûte aucun essai).");
            return;
        }
        controleur.validerReponse(panneauQuestion.getReponses());
    }

    private void configurerBouton(String texte, Color couleur, Runnable action) {
        bouton.setText(texte);
        bouton.setCouleur(couleur);
        actionBouton = action;
    }

    private void definirBarre(Color fond, Color couleurTexte, String texte) {
        barreBas.setBackground(fond);
        retour.setForeground(couleurTexte);
        retour.setText(Theme.htmlLargeur(texte, 600));
    }

    private void mettreAJourEntete() {
        int total = session.getNiveau().getNombreQuestions();
        int courante = session.getQuestionCourante();
        compteur.setText("Question " + Math.min(courante + 1, total) + " sur " + total);
        progression.setValeur((double) courante / total);
        vies.setEtat(session.getEssaisMax(), session.getEssaisRestants());
    }

    private JPanel construireCarteLecon(String lecon) {
        PanneauCarte carte = new PanneauCarte(null, Theme.CARTE, 20);
        carte.setLayout(new BoxLayout(carte, BoxLayout.Y_AXIS));
        carte.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        carte.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel entete = Theme.etiquette("LEÇON", Theme.PETIT, Theme.JAUNE);
        entete.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel texte = Theme.paragraphe(lecon, Theme.LARGEUR_COLONNE - 44, Theme.TEXTE_NORMAL, Theme.TEXTE);
        texte.setAlignmentX(Component.LEFT_ALIGNMENT);
        carte.add(entete);
        carte.add(Box.createVerticalStrut(8));
        carte.add(texte);
        return carte;
    }
}