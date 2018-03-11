package fr.quintipio.simplyPassword.model;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * L'objet Mot de passe
 */
@XmlRootElement
public class MotDePasse implements Serializable {

    private String titre;

    private String login;

    private String motDePasseObjet;

    private String commentaire;

    private String siteWeb;

    @XmlTransient
    private Dossier dossierPossesseur;

    private Integer idIcone;
    
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasseObjet() {
        return motDePasseObjet;
    }

    public void setMotDePasseObjet(String motDePasseObjet) {
        this.motDePasseObjet = motDePasseObjet;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

	@XmlTransient
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
    public String toString() {
        return titre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MotDePasse)) return false;

        MotDePasse that = (MotDePasse) o;

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
