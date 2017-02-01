package fr.quintipio.simplyPassword.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class MotDePasse {

private StringProperty titre;

    private StringProperty login;

    private StringProperty motDePasseObjet;

    private StringProperty commentaire;

    private StringProperty siteWeb;

    private ObjectProperty<Dossier> dossierPossesseur;

    private IntegerProperty idIcone;


    public String getTitre() {
        return titre.get();
    }

    public StringProperty titreProperty() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre.set(titre);
    }

    public String getLogin() {
        return login.get();
    }

    public StringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getMotDePasseObjet() {
        return motDePasseObjet.get();
    }

    public StringProperty motDePasseObjetProperty() {
        return motDePasseObjet;
    }

    public void setMotDePasseObjet(String motDePasseObjet) {
        this.motDePasseObjet.set(motDePasseObjet);
    }

    public String getCommentaire() {
        return commentaire.get();
    }

    public StringProperty commentaireProperty() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire.set(commentaire);
    }

    public String getSiteWeb() {
        return siteWeb.get();
    }

    public StringProperty siteWebProperty() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb.set(siteWeb);
    }

    public Dossier getDossierPossesseur() {
        return dossierPossesseur.get();
    }

    public ObjectProperty<Dossier> dossierPossesseurProperty() {
        return dossierPossesseur;
    }

    public void setDossierPossesseur(Dossier dossierPossesseur) {
        this.dossierPossesseur.set(dossierPossesseur);
    }

    public int getIdIcone() {
        return idIcone.get();
    }

    public IntegerProperty idIconeProperty() {
        return idIcone;
    }

    public void setIdIcone(int idIcone) {
        this.idIcone.set(idIcone);
    }
}
