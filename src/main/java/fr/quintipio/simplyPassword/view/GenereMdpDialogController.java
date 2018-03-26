package fr.quintipio.simplyPassword.view;
import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.util.CryptUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class GenereMdpDialogController {
	
	//ELEMENT FXML
	@FXML
	private CheckBox lettreCheck;
	@FXML
	private CheckBox chiffreCheck;
	@FXML
	private CheckBox specCheck;
	@FXML
	private Slider longueurSlider;
	@FXML
	private Label longueurLabel;
	@FXML
	private Button validerButton;
	
	private Stage dialogStage;
	
	
	private String motdePasse;
	
	
	
	/**
	 * Retourne le mot de passe généré
	 * @return
	 */
	public String getMotdePasse() {
		return motdePasse;
	}

	/**
	 * Fourni la fenêtre généré
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	/**
	 * Initialise la fenêtre
	 */
	@FXML
	private void initialize() {
		lettreCheck.setSelected(true);
		chiffreCheck.setSelected(true);
		specCheck.setSelected(true);
		longueurSlider.setValue(11);

		longueurSlider.valueProperty().addListener(observable -> {
            Double d = longueurSlider.getValue();
            Integer i = d.intValue();
            longueurLabel.setText(i.toString());
        });
		longueurLabel.setText("11");
	}
	
	/**
	 * Bouton valider
	 */
	@FXML
	private void valider() {
		try {
            Double d = longueurSlider.getValue();
			motdePasse = CryptUtils.genereMotdePasse( d.intValue(), lettreCheck.isSelected(), chiffreCheck.isSelected(), specCheck.isSelected());
			dialogStage.close();
		} catch (Exception e) {
			Main.showError(e);
		}
	}
	
	/**
	 * Bouton Annuler
	 */
	@FXML
	private void annuler() {
		motdePasse = null;
		dialogStage.close();
	}
	
	/**
	 * Vérifie si le bouton valider peut être clicable
	 */
	@FXML
	private void verifChecked() {
		validerButton.setDisable(!lettreCheck.isSelected() && !chiffreCheck.isSelected() && !specCheck.isSelected());
	}
	

}
