package fr.quintipio.simplyPassword.view;

import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Controleur de la fenetre Racine
 */
public class RootLayoutController {

    private Main main;
    private boolean modif;

    ///GETTER ET SETTER
    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public boolean isModif() {
        return modif;
    }

    public void setModif(boolean modif) {
        this.modif = modif;
    }

    /**
     * Constructeur
     */
    public RootLayoutController() {
        modif =false;
    }


    ///Action de la partie fichier

    /**
     * Quitter l'application
     */
    @FXML
    private void handleExit() {

        if(isModif()) {
            save();
        }

        System.exit(0);
    }

    /**
     * Sauvegarder sous un nouveau fichier
     */
    @FXML
    private void saveAs() {
        //vérification si le mot de passe maître existe
        if(PasswordBusiness.isMotDePasse()) {

            //ouverture d'une dlg de fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase()+" (*"+ ContexteStatic.extension+")", "*"+ContexteStatic.extension));
            File file = fileChooser.showSaveDialog(main.getPrimaryStage());
            String path = null;
            if(file != null) {
                if (!file.getPath().endsWith(ContexteStatic.extension)) {
                    path = file.getPath() + ContexteStatic.extension;
                }
            }

            //sauvegarde
            if(path != null && path.isEmpty()) {
                try {
                    PasswordBusiness.save(path);
                }catch(Exception ex) {

                }
            }
        }
        else {

        }
    }

    /**
     * Sauvegarder
     */
    @FXML
    private void save() {
        if(!PasswordBusiness.isMotDePasse()) {

        }

        if(!PasswordBusiness.isFichier()) {
            //ouverture d'une dlg de fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase()+" (*"+ ContexteStatic.extension+")", "*"+ContexteStatic.extension));
            File file = fileChooser.showSaveDialog(main.getPrimaryStage());
            if(file != null) {
                if (!file.getPath().endsWith(ContexteStatic.extension)) {
                    PasswordBusiness.setFichier(new ComFile(file.getPath() + ContexteStatic.extension));
                }
            }
        }

        if(PasswordBusiness.isFichier() && PasswordBusiness.isMotDePasse()) {
            try {
                PasswordBusiness.save(null);
            }catch(Exception ex) {

            }
        }
        else {
            PasswordBusiness.setFichier(null);
            PasswordBusiness.setMotDePasse(null);
        }
    }

    /**
     * Charger un fichier
     */
    @FXML
    private void load() {
        String path = null;
        String password = null;

        //chargement du fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase()+" (*"+ ContexteStatic.extension+")", "*"+ContexteStatic.extension));
        File file = fileChooser.showOpenDialog(main.getPrimaryStage());
        if(file != null) {
            if (!file.getPath().endsWith(ContexteStatic.extension)) {
                PasswordBusiness.setFichier(new ComFile(file.getPath() + ContexteStatic.extension));
            }
        }

        //chargement du mot de passe
        if(path != null && !path.isEmpty()) {

        }

        //chargement
        if(path != null && !path.isEmpty() && password != null && !password.isEmpty()) {
            try {
                PasswordBusiness.load(path,password);
            }catch(Exception ex) {

            }
        }
    }

    /**
     * Reinitialiser
     */
    @FXML
    private void newApp() {
        PasswordBusiness.reset();
    }

}
