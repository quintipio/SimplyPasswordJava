package fr.quintipio.simplyPassword.view;

import java.net.URL;
import java.util.ResourceBundle;

import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.util.CryptUtils;
import fr.quintipio.simplyPassword.util.StringUtils;
import java.io.File;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controlleur de la boite de dialogue de gestion d'un mot de passe
 *
 */
public class PasswordEditDialogController implements Initializable {

	///Element JFX
	@FXML
	private TextField titreField;
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField mdpField;
	@FXML
	private TextField mdpTextField;
	@FXML
	private TextField webField;
	@FXML
	private TextField commentaireField;
	@FXML
	private CheckBox affcheMdpCheckbox;
	@FXML
	private ProgressBar mdpProgress;
	@FXML
	private Button validButton;
    @FXML
    private Button recupButton;
	
	private ResourceBundle bundle;
        private Stage dialogStage;
        private Main main;
	
    //le mot de passe à retourner
	private MotDePasse motdePasse;
	
	
	//INIT GETTER ET SETTER
	/**
	 * méthode de chargement
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;
		mdpField.setVisible(true);
		mdpTextField.setVisible(false);
		mdpProgress.setProgress(0);
		titreField.textProperty().addListener((observable,oldValue,newValue) -> checkValiderDisable());
		loginField.textProperty().addListener((observable,oldValue,newValue) -> checkValiderDisable());
		mdpField.textProperty().addListener((observable,oldValue,newValue) -> checkValiderDisable());
	}
	
	/**
	 * Donne la classe de base de l'appli
	 * @param main classe de base
	 */
	public void setMain(Main main) {
		this.main = main;
	}
	
	/**
	 * Donne l'objet de la boite de dialogue
	 * @param dialogStage la boite de dialogue
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	/**
	 * Retourne le mot de passe en cours
	 * @return le mot de passe
	 */
	public MotDePasse getMotdePasse() {
		return motdePasse;
	}

	/**
	 * Charge un mot de passe à modifier
	 * @param motdePasse le mot de passe
	 */
	public void setMotdePasse(MotDePasse motdePasse) {
		if(motdePasse != null) {
			this.motdePasse = motdePasse;
			titreField.setText(motdePasse.getTitre());
			loginField.setText(motdePasse.getLogin());
			ecrireMdp(motdePasse.getMotDePasseObjet(),true);
			webField.setText(motdePasse.getSiteWeb());
			commentaireField.setText(motdePasse.getCommentaire());
            recupButton.setVisible(false);
		}
		else {
			this.motdePasse = null;
			titreField.setText("");
			loginField.setText("");
			mdpField.setText("");
			mdpTextField.setText("");
			webField.setText("");
			commentaireField.setText("");
            recupButton.setVisible(true);
            validButton.setDisable(true);
		}
	}
	
	
	
	
	
	//Methodes liés au FXML
	/**
         * Permet la récupération d'un mot de passe
         */
        @FXML
        private void recupMdp() {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extensionPartage.toUpperCase()+" (*"+ ContexteStatic.extensionPartage+")", "*"+ContexteStatic.extensionPartage));
                File file = fileChooser.showOpenDialog(dialogStage);
                if(file != null) {
                    ComFile fichier = new ComFile(file.getPath());
                    MotDePasse mdp = PasswordBusiness.dechiffrerPartage(fichier.readFileToByteArray());
                    setMotdePasse(mdp);
                }
            }
            catch(Exception ex) {
            	Main.showError(ex);
            }
            
        }
        
	/**
	 * Affiche ou masque le mot de passe
	 */
	@FXML
	private void afficheMasqueMdp() {
		mdpField.setVisible(!affcheMdpCheckbox.isSelected());
		mdpTextField.setVisible(affcheMdpCheckbox.isSelected());
	}
	
	/**
	 * Ouvre la dlg pour générer un mot de passe
	 */
	@FXML
	private void genereMdp() {
		String mdp = main.showGenereMotDePasse(dialogStage);
		if(mdp != null) {
			ecrireMdp(mdp,true);
			mdpField.setVisible(false);
			mdpTextField.setVisible(true);
			affcheMdpCheckbox.setSelected(true);
		}
	}
	
	/**
	 * Copie les données du PasswordField vers le TextField
	 */
	@FXML
	private void ecrireMdpPasswordField() {
		mdpTextField.setText(mdpField.getText());
		ecrireMdp(mdpField.getText(), false);
	}
	
	/**
	 * Copie les données du PasswordField vers le TextField
	 */
	@FXML
	private void ecrireMdpTextField() {
		mdpField.setText(mdpTextField.getText());
		ecrireMdp(mdpTextField.getText(), false);
	}
	
	/**
	 * Bouton valider (vérifie les données, et charge le mot de passe)
	 */
	@FXML
	private void valider() {
		if(validate()) {
			motdePasse = new MotDePasse();
			motdePasse.setTitre(titreField.getText());
			motdePasse.setLogin(loginField.getText());
			motdePasse.setMotDePasseObjet(mdpField.getText());
			motdePasse.setSiteWeb(webField.getText());
			motdePasse.setCommentaire(commentaireField.getText());
			dialogStage.close();
		}
		
	}
	
	/**
	 * Ferme la fenetre
	 */
	@FXML
	private void annuler() {
		motdePasse = null;
		dialogStage.close();
	}
	
	
	
	
	//OUTILS DU CONTROLEUR
	/**
	 * Ecrit un mot de passe dans tout les champs et calcul sa force
	 * @param mdp le mot de passe
	 */
	private void ecrireMdp(String mdp,boolean ecrire) {
		if(ecrire) {
			mdpField.setText(mdp);
			mdpTextField.setText(mdp);
		}
		mdpProgress.setProgress(((double)CryptUtils.calculerForceMotDePasse(mdp))/100);
	}
		
	/**
	 * Controle les données entràs par l'utilisateur et affiche les erreurs
	 * @return true si ok
	 */
	private boolean validate() {
		String errorMessage = "";

        if (StringUtils.stringEmpty(titreField.getText())) {
            errorMessage += bundle.getString("titreInvalide")+"\n";
        }
        if (StringUtils.stringEmpty(loginField.getText())) {
            errorMessage += bundle.getString("titreInvalide")+"\n";
        }
        if (StringUtils.stringEmpty(mdpField.getText())) {
            errorMessage += bundle.getString("mdpInvalide")+"\n";
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(bundle.getString("erreur"));
            alert.setHeaderText(bundle.getString("erreurVide"));
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
	}
	
	/**
	 * Vérifie si oui ou non le bouton valider est actif
	 */
	private void checkValiderDisable() {
		validButton.setDisable(StringUtils.stringEmpty(mdpField.getText()) || StringUtils.stringEmpty(loginField.getText()) || StringUtils.stringEmpty(titreField.getText()));
	}
}
