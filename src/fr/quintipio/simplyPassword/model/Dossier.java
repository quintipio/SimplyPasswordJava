package fr.quintipio.simplyPassword.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * L'objet Dossier
 */
@XmlRootElement
public class Dossier implements Serializable{

    private String titre;

	@XmlTransient
    private Dossier dossierParent;

    private List<Dossier> sousDossier;

    private List<MotDePasse> listeMotDePasse;

    private Integer idIcone;

    public Dossier() {

    }

    public Dossier(String titre, Dossier dossierParent) {
        this.titre = titre;
        this.dossierParent = dossierParent;
        sousDossier = new ArrayList<>();
        listeMotDePasse = new ArrayList<>();
        idIcone = 0;
    }


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

	@XmlTransient
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

    @Override
    public String toString() {
        return titre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dossier)) return false;

        Dossier dossier = (Dossier) o;

        if (getTitre() != null ? !getTitre().equals(dossier.getTitre()) : dossier.getTitre() != null) return false;
        if (getDossierParent() != null ? !getDossierParent().equals(dossier.getDossierParent()) : dossier.getDossierParent() != null)
            return false;
        if (getSousDossier() != null ? !getSousDossier().equals(dossier.getSousDossier()) : dossier.getSousDossier() != null)
            return false;
        if (getListeMotDePasse() != null ? !getListeMotDePasse().equals(dossier.getListeMotDePasse()) : dossier.getListeMotDePasse() != null)
            return false;
        return getIdIcone() != null ? getIdIcone().equals(dossier.getIdIcone()) : dossier.getIdIcone() == null;

    }

    @Override
    public int hashCode() {
        int result = getTitre() != null ? getTitre().hashCode() : 0;
        result = 31 * result + (getDossierParent() != null ? getDossierParent().hashCode() : 0);
        result = 31 * result + (getSousDossier() != null ? getSousDossier().hashCode() : 0);
        result = 31 * result + (getListeMotDePasse() != null ? getListeMotDePasse().hashCode() : 0);
        result = 31 * result + (getIdIcone() != null ? getIdIcone().hashCode() : 0);
        return result;
    }
}
