package fr.quintipio.simplyPassword.model;


import java.io.Serializable;
import java.util.List;

/**
 * L'objet Dossier
 */
public class Dossier implements Serializable{

    private String titre;

    private Dossier dossierParent;

    private List<Dossier> sousDossier;

    private List<MotDePasse> listeMotDePasse;

    private Integer idIcone;

    public Integer getIdIcone() {
        return idIcone;
    }

    public void setIdIcone(Integer idIcone) {
        this.idIcone = idIcone;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Dossier getDossierParent() {
        return dossierParent;
    }

    public void setDossierParent(Dossier dossierParent) {
        this.dossierParent = dossierParent;
    }

    public List<Dossier> getSousDossier() {
        return sousDossier;
    }

    public void setSousDossier(List<Dossier> sousDossier) {
        this.sousDossier = sousDossier;
    }

    public List<MotDePasse> getListeMotDePasse() {
        return listeMotDePasse;
    }

    public void setListeMotDePasse(List<MotDePasse> listeMotDePasse) {
        this.listeMotDePasse = listeMotDePasse;
    }
}
