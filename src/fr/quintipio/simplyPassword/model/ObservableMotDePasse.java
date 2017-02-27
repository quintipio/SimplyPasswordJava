package fr.quintipio.simplyPassword.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * L'objet Mot de passe
 */
public class ObservableMotDePasse implements Serializable {

    private MotDePasse mdpOri;

    private StringProperty titre;

    private StringProperty login;

    private StringProperty motDePasseObjet;

    private StringProperty commentaire;

    private StringProperty siteWeb;

    private Dossier dossierPossesseur;

    private Integer idIcone;

    public ObservableMotDePasse(MotDePasse mdp) {
        titre = new SimpleStringProperty(mdp.getTitre());
        login = new SimpleStringProperty(mdp.getLogin());
        motDePasseObjet = new SimpleStringProperty(mdp.getMotDePasseObjet());
        siteWeb = new SimpleStringProperty(mdp.getSiteWeb());
        commentaire = new SimpleStringProperty(mdp.getCommentaire());
        idIcone = 0;
        dossierPossesseur = mdp.getDossierPossesseur();
        mdpOri = mdp;


    }

    public MotDePasse getMdpOri() {
        return mdpOri;
    }

    public void setMdpOri(MotDePasse mdpOri) {
        this.mdpOri = mdpOri;
    }

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
        return dossierPossesseur;
    }

    public void setDossierPossesseur(Dossier dossierPossesseur) {
        this.dossierPossesseur = dossierPossesseur;
    }

    public Integer getIdIcone() {
        return idIcone;
    }

    public void setIdIcone(Integer idIcone) {
        this.idIcone = idIcone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObservableMotDePasse)) return false;

        ObservableMotDePasse that = (ObservableMotDePasse) o;

        if (getTitre() != null ? !getTitre().equals(that.getTitre()) : that.getTitre() != null) return false;
        if (getLogin() != null ? !getLogin().equals(that.getLogin()) : that.getLogin() != null) return false;
        if (getMotDePasseObjet() != null ? !getMotDePasseObjet().equals(that.getMotDePasseObjet()) : that.getMotDePasseObjet() != null)
            return false;
        if (getCommentaire() != null ? !getCommentaire().equals(that.getCommentaire()) : that.getCommentaire() != null)
            return false;
        if (getSiteWeb() != null ? !getSiteWeb().equals(that.getSiteWeb()) : that.getSiteWeb() != null) return false;
        if (getDossierPossesseur() != null ? !getDossierPossesseur().equals(that.getDossierPossesseur()) : that.getDossierPossesseur() != null)
            return false;
        return getIdIcone() != null ? getIdIcone().equals(that.getIdIcone()) : that.getIdIcone() == null;

    }

    @Override
    public int hashCode() {
        int result = getTitre() != null ? getTitre().hashCode() : 0;
        result = 31 * result + (getLogin() != null ? getLogin().hashCode() : 0);
        result = 31 * result + (getMotDePasseObjet() != null ? getMotDePasseObjet().hashCode() : 0);
        result = 31 * result + (getCommentaire() != null ? getCommentaire().hashCode() : 0);
        result = 31 * result + (getSiteWeb() != null ? getSiteWeb().hashCode() : 0);
        result = 31 * result + (getDossierPossesseur() != null ? getDossierPossesseur().hashCode() : 0);
        result = 31 * result + (getIdIcone() != null ? getIdIcone().hashCode() : 0);
        return result;
    }
}
