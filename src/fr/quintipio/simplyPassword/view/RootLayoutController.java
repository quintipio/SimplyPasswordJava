package fr.quintipio.simplyPassword.view;

import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controleur de la fenetre Racine
 */
public class RootLayoutController implements Initializable  {

    private Main main;
    private boolean modif;
    private ResourceBundle bundle;

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

    
    
    ///INIT
    
    /**
     * Constructeur
     */
    public RootLayoutController() {
        modif =false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	bundle = resources;
    }

    
    
    
    
    
    ///ACTION

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
                    PasswordBusiness.setFichier(file.getPath() + ContexteStatic.extension);
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
                PasswordBusiness.setFichier(file.getPath() + ContexteStatic.extension);
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
    
    /**
     * Ouvre la fen�tre pour changer le mot de passe ma�tre
     */
    @FXML
    private void changeMdpMaitre() {
    	main.showChangeMasterPassword();
    }
    
    /**
     * Ouvre la boite de dialogue pour changer de langue
     */
    @FXML
    private void changeLangue() {
    	List<String> listeLangue = new ArrayList<String>();
    	listeLangue.add(bundle.getString("fr"));
    	listeLangue.add(bundle.getString("en"));
    	
    	ChoiceDialog<String> dialog = new ChoiceDialog<>(listeLangue.get(0),listeLangue);
    	dialog.setTitle(bundle.getString("changerlangue"));
    	dialog.setHeaderText(bundle.getString("selectLangue"));
    	dialog.setContentText(bundle.getString("langue"));
    	
    	Optional<String> result = dialog.showAndWait();
    	result.ifPresent(lang -> appliquerLangue(lang,listeLangue));
    }
    
    /**
     * Applique une nouvelle langue
     * @param langue la langue s�lectionn�
     * @param liste la liste des langues
     */
    private void appliquerLangue(String langue,List<String> liste) {
    	if(langue.equals(liste.get(0))) {
    		main.ouvrirFenetre(new Locale("fr","FR"));
    	}
    	if(langue.equals(liste.get(1))) {
    		main.ouvrirFenetre(new Locale("en","EN"));
    	}
    	
    }
    
    /**
     * Affiche la boite de dialogue � propos de...
     */
    @FXML
    private void openAppd() {
    	Dialog dlg = new Dialog<String>();
    	dlg.setTitle(bundle.getString("appd"));
    	dlg.setHeaderText(ContexteStatic.nomAppli);
    	
    	ButtonType button = new ButtonType("OK",ButtonData.OK_DONE);
    	dlg.getDialogPane().getButtonTypes().add(button);
    	
    	GridPane grid = new GridPane();
    	Label laa = new Label();
    	laa.setText(bundle.getString("version"));
    	Label lab = new Label();
    	lab.setText(ContexteStatic.version);
    	Label lba = new Label();
    	lba.setText(bundle.getString("developpeur"));
    	Label lbb = new Label();
    	lbb.setText(ContexteStatic.developpeur);
    	Label lca = new Label();
    	lca.setText(bundle.getString("copyright"));
    	Label lcb = new Label();
    	lcb.setText(ContexteStatic.developpeur+" "+Calendar.getInstance().get(Calendar.YEAR));
    	
    	grid.add(laa,0,0);
    	grid.add(lab, 1,0);
    	grid.add(lba,0,1);
    	grid.add(lbb, 1,1);
    	grid.add(lca,0,2);
    	grid.add(lcb, 1,2);
    	dlg.getDialogPane().setContent(grid);
    	dlg.show();
    }

}
