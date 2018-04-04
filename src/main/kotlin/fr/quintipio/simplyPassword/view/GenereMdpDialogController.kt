package fr.quintipio.simplyPassword.view

import fr.quintipio.simplyPassword.Main
import fr.quintipio.simplyPassword.util.CryptUtils
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.stage.Stage

class GenereMdpDialogController {

    //ELEMENT FXML
    @FXML
    private lateinit var lettreCheck: CheckBox
    @FXML
    private lateinit var chiffreCheck: CheckBox
    @FXML
    private lateinit var specCheck: CheckBox
    @FXML
    private lateinit var longueurSlider: Slider
    @FXML
    private lateinit var longueurLabel: Label
    @FXML
    private lateinit var validerButton: Button

    private var dialogStage: Stage? = null


    /**
     * Retourne le mot de passe généré
     * @return
     */
    var motdePasse: String? = null
        private set

    /**
     * Fourni la fenêtre généré
     * @param dialogStage
     */
    fun setDialogStage(dialogStage: Stage) {
        this.dialogStage = dialogStage
    }

    /**
     * Initialise la fenêtre
     */
    @FXML
    private fun initialize() {
        lettreCheck.isSelected = true
        chiffreCheck.isSelected = true
        specCheck.isSelected = true
        longueurSlider.value = 11.0

        longueurSlider.valueProperty().addListener { _ ->
            val d = longueurSlider.value
            val i = d.toInt()
            longueurLabel.text = i.toString()
        }
        longueurLabel.text = "11"
    }

    /**
     * Bouton valider
     */
    @FXML
    private fun valider() {
        try {
            val d = longueurSlider.value
            motdePasse = CryptUtils.genereMotdePasse(d.toInt(), lettreCheck.isSelected, chiffreCheck.isSelected, specCheck.isSelected)
            dialogStage!!.close()
        } catch (e: Exception) {
            Main.showError(e)
        }

    }

    /**
     * Bouton Annuler
     */
    @FXML
    private fun annuler() {
        motdePasse = null
        dialogStage!!.close()
    }

    /**
     * Vérifie si le bouton valider peut être clicable
     */
    @FXML
    private fun verifChecked() {
        validerButton.isDisable = !lettreCheck.isSelected && !chiffreCheck.isSelected && !specCheck.isSelected
    }


}
