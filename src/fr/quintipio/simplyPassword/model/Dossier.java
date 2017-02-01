package fr.quintipio.simplyPassword.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class Dossier {

    private StringProperty titre;

    private ObjectProperty<Dossier> dossierParent;

    private ListProperty<Dossier> sousDossier;

    private ListProperty<MotDePasse> listeMotDePasse;

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

    public Dossier getDossierParent() {
        return dossierParent.get();
    }

    public ObjectProperty<Dossier> dossierParentProperty() {
        return dossierParent;
    }

    public void setDossierParent(Dossier dossierParent) {
        this.dossierParent.set(dossierParent);
    }

    public ObservableList<Dossier> getSousDossier() {
        return sousDossier.get();
    }

    public ListProperty<Dossier> sousDossierProperty() {
        return sousDossier;
    }

    public void setSousDossier(ObservableList<Dossier> sousDossier) {
        this.sousDossier.set(sousDossier);
    }

    public ObservableList<MotDePasse> getListeMotDePasse() {
        return listeMotDePasse.get();
    }

    public ListProperty<MotDePasse> listeMotDePasseProperty() {
        return listeMotDePasse;
    }

    public void setListeMotDePasse(ObservableList<MotDePasse> listeMotDePasse) {
        this.listeMotDePasse.set(listeMotDePasse);
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
