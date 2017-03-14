package fr.quintipio.simplyPassword.view;

import com.sun.deploy.net.offline.DeployOfflineManager;
import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.business.ParamBusiness;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    private void close() {
        if(main.askSave()) {
        	main.getPrimaryStage().close();
        }
    }

    /**
     * Sauvegarder sous un nouveau fichier
     */
    @FXML
    private void saveAs() {
        try {
			//ouverture d'une dlg de fichier
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase()+" (*"+ ContexteStatic.extension+")", "*"+ContexteStatic.extension));
			File file = fileChooser.showSaveDialog(main.getPrimaryStage());
			if(file != null) {
				if (file.getPath().endsWith(ContexteStatic.extension)) {
			        PasswordBusiness.setFichier(file.getPath(),true);
			    }
			    else {
			        PasswordBusiness.setFichier(file.getPath()+ContexteStatic.extension,true);
			    }
			}

			//sauvegarde
			if(file != null && file.getPath() != null && !file.getPath().isEmpty()) {
			    save();
			}
		} catch (Exception e) {
			Main.showError(e);
		}
    }

    /**
     * Sauvegarder (demande un mot de passe, si aucun, demande un emplacement de sauvegarde si aucun
     */
    @FXML
    private void save() {
        main.save();
    }

    /**
     * Charger un fichier
     */
    @FXML
    private void load() {
        try {
            
        if(main.askSave()) {
            String path = null;
                String password = null;

                //chargement du fichier
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase()+" (*"+ ContexteStatic.extension+")", "*"+ContexteStatic.extension));
                File file = fileChooser.showOpenDialog(main.getPrimaryStage());
                if(file != null) {
                        if (file.getPath().endsWith(ContexteStatic.extension)) {
                        PasswordBusiness.setFichier(file.getPath(),true);
                    }
                    else {
                        PasswordBusiness.setFichier(file.getPath()+ContexteStatic.extension,true);
                    }

                        PasswordBusiness.setMotDePasse(null);

                        main.ouvrirFenetre(true);
                }
        }
                
        } catch (Exception e) {
                Main.showError(e);
        }
    }

    /**
     * Réinitialiser
     */
    @FXML
    private void newApp() {
        try {
            if(main.askSave()) {
                PasswordBusiness.reset();
		main.ouvrirFenetre(false);
            }	
            } catch (Exception e) {
                    Main.showError(e);
            }
    }
    
    /**
     * Ouvre la fenêtre pour changer le mot de passe maître
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
    	try {
			List<String> listeLangue = new ArrayList<String>();
			for (String lang : ContexteStatic.listeLangues) {
				listeLangue.add(bundle.getString(lang));
			}
			
			int choixDefaut = 0;
			if(ParamBusiness.getParametreLangue() != null && ParamBusiness.getParametreLangue().contentEquals(listeLangue.get(1))) {
				choixDefaut = 1;
			}
			
			ChoiceDialog<String> dialog = new ChoiceDialog<>(listeLangue.get(choixDefaut),listeLangue);
			dialog.setTitle(bundle.getString("changerlangue"));
			dialog.setHeaderText(bundle.getString("selectLangue"));
			dialog.setContentText(bundle.getString("langue"));
			
			Optional<String> result = dialog.showAndWait();
			result.ifPresent(lang -> appliquerLangue(lang,listeLangue));
		} catch (Exception e) {
			Main.showError(e);
		}
    }
    
    /**
     * Applique une nouvelle langue
     * @param langue la langue sélectionné
     * @param liste la liste des langues
     */
    private void appliquerLangue(String langue,List<String> liste) {
    	
    	if(langue.equals(liste.get(0))) {
        	ParamBusiness.setParametreLangue(ContexteStatic.listeLangues[0]);
    		main.ouvrirFenetre(false);
    	}
    	if(langue.equals(liste.get(1))) {
        	ParamBusiness.setParametreLangue(ContexteStatic.listeLangues[1]);
    		main.ouvrirFenetre(false);
    	}
    	ParamBusiness.ecrireFichierParamUser();
    	
    }
    
    /**
     * Ouvre la dlg de chiffrement d'un fichier
     */
    @FXML
    private void cryptFile() {
    	main.showCryptFile(true);
    }
    
    /**
     * Ouvre la dlg de déchiffrement d'un fichier
     */
    @FXML
    private void decryptFile() {
    	main.showCryptFile(false);
    }
    
    /**
     * Affiche la boite de dialogue à propos de...
     */
    @FXML
    private void openAppd() {
    	Dialog<String> dlg = new Dialog<String>();
    	dlg.setTitle(bundle.getString("appd"));
    	dlg.setHeaderText(ContexteStatic.nomAppli);
    	dlg.setGraphic(new ImageView("/rsc/icon.png"));
    	
    	ButtonType button = new ButtonType("OK",ButtonData.OK_DONE);
    	dlg.getDialogPane().getButtonTypes().add(button);
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
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
