package fr.quintipio.simplyPassword.model;


import java.io.Serializable;

/**
 * L'objet Mot de passe
 */
public class MotDePasse implements Serializable {

    private String titre;

    private String login;

    private String motDePasseObjet;

    private String commentaire;

    private String siteWeb;

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
}
