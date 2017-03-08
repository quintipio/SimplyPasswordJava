package fr.quintipio.simplyPassword.view;

import java.net.URL;
import java.util.ResourceBundle;

import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.util.CryptUtils;
import fr.quintipio.simplyPassword.util.StringUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GererMasterPasswordDialogController implements Initializable {

	//ELEMENT FXML
	@FXML
	private PasswordField oldMdp;
	@FXML
	private PasswordField newMdp;
	@FXML
	private PasswordField confMdp;
	@FXML
	private ProgressBar progressMdp;
	@FXML
	private Label oldMdplabel;
	@FXML
	private Button validButton;

	private ResourceBundle bundle;
    private Stage dialogStage;
    
	
	//INIT, GETTER SETTER
	/**
	 * Lancement du controlleur
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		oldMdplabel.setVisible(PasswordBusiness.isMotDePasse());
		oldMdp.setVisible(PasswordBusiness.isMotDePasse());
		progressMdp.setProgress(0);
		validButton.setDisable(true);
		if(PasswordBusiness.isMotDePasse()) {
			oldMdp.textProperty().addListener((observable,oldValue,newValue) ->checkDisableButtonValid());
		}
		newMdp.textProperty().addListener((observable,oldValue,newValue) ->checkDisableButtonValid());
		confMdp.textProperty().addListener((observable,oldValue,newValue) ->checkDisableButtonValid());
		
	}
	
	/**
	 * Donne l'objet de la boite de dialogue
	 * @param dialogStage la boite de dialogue
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	@FXML
	private void changeProgress() {
		progressMdp.setProgress(((double)CryptUtils.calculerForceMotDePasse(newMdp.getText()))/100);
	}
	
	/**
	 * Valide le changement de mot de passe ou affiche les erreurs
	 * @return true si ok
	 */
	private boolean validate() {
		String errorMessage = "";
		
		if(PasswordBusiness.isMotDePasse() && StringUtils.stringEmpty(oldMdp.getText())) {
            errorMessage += bundle.getString("oldMdpVide")+"\n";
		}
		
		if(PasswordBusiness.isMotDePasse() && !StringUtils.stringEmpty(oldMdp.getText()) && oldMdp.getText().contentEquals(PasswordBusiness.getMotDePasse())) {
            errorMessage += bundle.getString("oldMdpDif")+"\n";
		}
		
		if(StringUtils.stringEmpty(newMdp.getText())) {
            errorMessage += bundle.getString("newMdpVide")+"\n";
		}
		
		if(StringUtils.stringEmpty(confMdp.getText())) {
            errorMessage += bundle.getString("confirmmdpVide")+"\n";
		}
		
		if(!StringUtils.stringEmpty(confMdp.getText()) && !StringUtils.stringEmpty(newMdp.getText()) && !newMdp.getText().contentEquals(confMdp.getText())) {
            errorMessage += bundle.getString("mdpDif")+"\n";
		}
		
		if(!StringUtils.stringEmpty(confMdp.getText()) && !StringUtils.stringEmpty(newMdp.getText()) && newMdp.getText().length() < 8) {
            errorMessage += bundle.getString("mdpCours")+"\n";
		}
		
		if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(bundle.getString("erreur"));
            alert.setHeaderText(bundle.getString("erreur"));
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
	}
	
	
	/**
	 * Rend disponible ou no le bouton de validation
	 */
	private void checkDisableButtonValid() {
		 validButton.setDisable((PasswordBusiness.isMotDePasse() && StringUtils.stringEmpty(oldMdp.getText())) || (PasswordBusiness.isMotDePasse() && !StringUtils.stringEmpty(oldMdp.getText()) && !PasswordBusiness.getMotDePasse().contentEquals(oldMdp.getText()))
					|| StringUtils.stringEmpty(newMdp.getText()) || (!StringUtils.stringEmpty(newMdp.getText()) && newMdp.getText().length() < 8)
					|| StringUtils.stringEmpty(confMdp.getText()) || (!StringUtils.stringEmpty(confMdp.getText()) && confMdp.getText().length() < 8) 
					|| (!StringUtils.stringEmpty(confMdp.getText()) && !StringUtils.stringEmpty(newMdp.getText()) && !confMdp.getText().contentEquals(newMdp.getText()))
					|| (!StringUtils.stringEmpty(newMdp.getText()) && !StringUtils.stringEmpty(oldMdp.getText()) && oldMdp.getText().contentEquals(newMdp.getText())));
	}
	
	/**
	 * Valide le changement
	 */
	@FXML
	private void valider() {
		if(validate()) {
			PasswordBusiness.setMotDePasse(newMdp.getText());
			PasswordBusiness.setModif(true);
			dialogStage.close();
		}
	}
	
	/**
	 * Annule le changement
	 */
	@FXML
	private void annuler() {
		dialogStage.close();
	}

}
