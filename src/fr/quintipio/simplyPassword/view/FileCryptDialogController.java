package fr.quintipio.simplyPassword.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.util.CryptUtils;
import fr.quintipio.simplyPassword.util.StringUtils;
import fr.quintipio.simplyPassword.util.CryptUtils.InvalidPasswordException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileCryptDialogController implements Initializable {

	//ELEMENT FXML
	@FXML
	private Label labelHaut;
	@FXML
	private Label labelBas;
	@FXML
	private TextField fieldHaut;
	@FXML
	private TextField fieldBas;
	@FXML
	private PasswordField passwordA;
	@FXML
	private PasswordField passwordB;
	@FXML
	private ProgressBar progressMdp;
	@FXML
	private ProgressBar progressChiffrement;
	@FXML
	private Button validButton;
	@FXML
	private Button parcourirB;
	@FXML
	private Button parcourirA;
	
	
	private boolean cryptFichier;
	private String nameFileIn;
	
	private ResourceBundle bundle;
    private Stage dialogStage;
	
    
	///INIT GETTER ET SETTER
	
    /**
     * Initialisation de la vue
     */
    @Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		fieldHaut.textProperty().addListener((observable, oldValue, newValue) -> {
			checkButtonValid(); 
			parcourirB.setDisable(StringUtils.stringEmpty(newValue));
		});
		fieldBas.textProperty().addListener(t -> checkButtonValid());
		passwordA.textProperty().addListener((observable, oldValue, newValue) -> {
			checkButtonValid();
			progressMdp.setProgress((double)CryptUtils.calculerForceMotDePasse(newValue)/(double)100);
		});
		passwordB.textProperty().addListener(t -> checkButtonValid());
	}

	/**
	 * Initililisation des paramètres de la vue
	 */
	public void init(boolean cryptFichier) {
		this.cryptFichier = cryptFichier;
		if(cryptFichier) {
			validButton.setText(bundle.getString("chiffrer"));
			labelHaut.setText(bundle.getString("fichierAChiffrer"));
			labelBas.setText(bundle.getString("fichierChiffrer"));
		} 
		else {
			validButton.setText(bundle.getString("dechiffrer"));
			labelHaut.setText(bundle.getString("fichierADechiffrer"));
			labelBas.setText(bundle.getString("fichierDechiffrer"));
			
		}
		parcourirB.setDisable(true);
		progressMdp.setProgress(0);
		progressChiffrement.setProgress(0);
		fieldHaut.setText("");
		fieldBas.setText("");
		validButton.setDisable(true);
	}
	

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	
	
	//METHOD FXML
	/**
	 * Bouton pour sélectionner un fichier d'entrée
	 */
	@FXML
	private void parcourirA() {
		try {
			FileChooser fileChooser = new FileChooser();
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*","*.*"));
	        File file = fileChooser.showOpenDialog(dialogStage);
	        if(file != null) {
	        	fieldHaut.setText(file.getPath());
	        	nameFileIn = file.getName();
	        }
		}
		catch(Exception ex) {
            Main.showError(ex);
		}
	}
	
	/**
	 * Bouton pour sélectionner un fichier de sortie
	 */
	@FXML
	private void parcourirB() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*","*.*"));
			fileChooser.setInitialFileName(nameFileIn);
			File file = fileChooser.showSaveDialog(dialogStage);
			if(file != null) {
				fieldBas.setText(file.getPath());
			}
		} catch (Exception e) {
				Main.showError(e);
		}
	}
	
	/**
	 * Bouton de validation
	 */
	@FXML
	private void valid() {
		try {
			File fileIn = new File(fieldHaut.getText());
			File fileOut = new File(fieldBas.getText());
			if(validate(fileIn)) {
				progressChiffrement.setProgress(-1);
				disablePanel(true);
				byte[] data = Files.readAllBytes(fileIn.toPath());
				ByteArrayInputStream input = new ByteArrayInputStream(data);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
				if(cryptFichier) {
	                CryptUtils.encrypt(128,passwordA.getText().toCharArray(), input, output);
	        	}
	        	else {
	                CryptUtils.decrypt(passwordA.getText().toCharArray(), input, output);
	        	}
				Files.write(fileOut.toPath(),output.toByteArray(),StandardOpenOption.CREATE);
			}
			dialogStage.close();
		}
		catch(InvalidPasswordException e) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(bundle.getString("erreur"));
            alert.setHeaderText(bundle.getString("erreur"));
            alert.setContentText(bundle.getString("erreurMdp"));
            alert.showAndWait();
		}
		catch(Exception ex) {
			Main.showError(ex);
		}
		progressChiffrement.setProgress(-1);
		disablePanel(false);
	}
	
	/**
	 * Méthode de validation de l'action de chiffrement / déchiffrement
	 * @param fileIn le fichier d'entrée
	 * @return true si ok
	 */
	private boolean validate(File fileIn) {
		String erreur = "";
		
		if(!fileIn.exists()) {
			erreur+=bundle.getString("erreurFichierNotExist")+"\r\n";
		}
		
		if(fileIn.exists() && fileIn.length() > 100000000) {
			erreur+=bundle.getString("erreurFichierGros")+"\r\n";
		}
		
		if(!StringUtils.stringEmpty(erreur)) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(bundle.getString("erreur"));
            alert.setHeaderText(bundle.getString("erreur"));
            alert.setContentText(erreur);
            alert.showAndWait();
            return false;
		}
		return true;
	}
	
	/**
	 * Bloque ou non tout les champs de la vue
	 * @param disable true si bloqué
	 */
	private void disablePanel(boolean disable) {
		parcourirA.setDisable(disable);
		parcourirB.setDisable(disable);
		validButton.setDisable(disable);
		passwordA.setDisable(disable);
		passwordB.setDisable(disable);
	}
	
	/**
	 * Bouton d'annulation et/ou de fermeture de la dlg
	 */
	@FXML
	private void annule() {
		dialogStage.close();
	}
	
	//METHODE INTERNE AU CONTROLEUR
	/**
	 * Autorise ou non le disable du bouton de validation
	 */
	private void checkButtonValid() {
		validButton.setDisable(StringUtils.stringEmpty(fieldHaut.getText()) || StringUtils.stringEmpty(fieldBas.getText()) || 
				passwordA.getText().length() < 8 || passwordB.getText().length() < 8 || 
				(!StringUtils.stringEmpty(passwordA.getText()) && !StringUtils.stringEmpty(passwordB.getText()) && !passwordA.getText().contentEquals(passwordB.getText())));
	}
	
	
}
