package fr.quintipio.simplyPassword.view

import java.net.URL
import java.util.ResourceBundle

import fr.quintipio.simplyPassword.Main
import fr.quintipio.simplyPassword.business.PasswordBusiness
import fr.quintipio.simplyPassword.util.CryptUtils
import fr.quintipio.simplyPassword.util.StringUtils
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.PasswordField
import javafx.scene.control.ProgressBar
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.Label
import javafx.stage.Stage

class GererMasterPasswordDialogController : Initializable {

    //ELEMENT FXML
    @FXML
     private lateinit var oldMdp: PasswordField 
    @FXML
     private lateinit var newMdp: PasswordField 
    @FXML
     private lateinit var confMdp: PasswordField 
    @FXML
     private lateinit var progressMdp: ProgressBar 
    @FXML
     private lateinit var oldMdplabel: Label 
    @FXML
     private lateinit var validButton: Button 

    private var bundle: ResourceBundle? = null
    private var dialogStage: Stage? = null


    //INIT, GETTER SETTER
    /**
     * Lancement du controlleur
     */
    override fun initialize(location: URL, resources: ResourceBundle) {
        bundle = resources
        oldMdplabel.isVisible = PasswordBusiness.isMotDePasse()
        oldMdp.isVisible = PasswordBusiness.isMotDePasse()
        progressMdp.progress = 0.0
        validButton.isDisable = true
        if (PasswordBusiness.isMotDePasse()) {
            oldMdp.textProperty().addListener { _, _, _ -> checkDisableButtonValid() }
        }
        newMdp.textProperty().addListener {  _, _, _ -> checkDisableButtonValid() }
        confMdp.textProperty().addListener {  _, _, _ -> checkDisableButtonValid() }

    }

    /**
     * Donne l'objet de la boite de dialogue
     * @param dialogStage la boite de dialogue
     */
    fun setDialogStage(dialogStage: Stage) {
        this.dialogStage = dialogStage
    }

    /**
     * Change le niveau de la barre de progression
     */
    @FXML
    private fun changeProgress() {
        progressMdp.progress = CryptUtils.calculerForceMotDePasse(newMdp.text).toDouble() / 100
    }

    /**
     * Valide le changement de mot de passe ou affiche les erreurs
     * @return true si ok
     */
    private fun validate(): Boolean {
        var errorMessage = ""

        if (PasswordBusiness.isMotDePasse() && StringUtils.isEmpty(oldMdp.text)) {
            errorMessage += bundle!!.getString("oldMdpVide") + "\n"
        }

        if (PasswordBusiness.isMotDePasse() && oldMdp.text != PasswordBusiness.motDePasse) {
            errorMessage += bundle!!.getString("oldMdpDif") + "\n"
        }

        if (StringUtils.isEmpty(newMdp.text)) {
            errorMessage += bundle!!.getString("newMdpVide") + "\n"
        }

        if (StringUtils.isEmpty(confMdp.text)) {
            errorMessage += bundle!!.getString("confirmmdpVide") + "\n"
        }

        if (!StringUtils.isEmpty(confMdp.text) && !StringUtils.isEmpty(newMdp.text) && !newMdp.text!!.contentEquals(confMdp.text)) {
            errorMessage += bundle!!.getString("mdpDif") + "\n"
        }

        if (!StringUtils.isEmpty(confMdp.text) && !StringUtils.isEmpty(newMdp.text) && newMdp.text.length < 8) {
            errorMessage += bundle!!.getString("mdpCours") + "\n"
        }

        return if (errorMessage.isEmpty()) {
            true
        } else {
            // Show the error message.
            val alert = Alert(AlertType.ERROR)
            alert.initOwner(dialogStage)
            alert.title = bundle!!.getString("erreur")
            alert.headerText = bundle!!.getString("erreur")
            alert.contentText = errorMessage
            alert.showAndWait()
            false
        }
    }


    /**
     * Rend disponible ou no le bouton de validation
     */
    private fun checkDisableButtonValid() {
        validButton.isDisable = (PasswordBusiness.isMotDePasse() && StringUtils.isEmpty(oldMdp.text) || PasswordBusiness.isMotDePasse() && !StringUtils.isEmpty(oldMdp.text) && !PasswordBusiness.motDePasse.contentEquals(oldMdp.text)
                || StringUtils.isEmpty(newMdp.text) || !StringUtils.isEmpty(newMdp.text) && newMdp.text!!.length < 8
                || StringUtils.isEmpty(confMdp.text) || !StringUtils.isEmpty(confMdp.text) && confMdp.text!!.length < 8
                || !StringUtils.isEmpty(confMdp.text) && !StringUtils.isEmpty(newMdp.text) && !confMdp.text!!.contentEquals(newMdp.text)
                || !StringUtils.isEmpty(newMdp.text) && !StringUtils.isEmpty(oldMdp.text) && oldMdp.text!!.contentEquals(newMdp.text))
    }

    /**
     * Valide le changement
     */
    @FXML
    private fun valider() {
        try {
            if (validate()) {
                PasswordBusiness.motDePasse=newMdp.text
                PasswordBusiness.modif = true
                dialogStage!!.close()
            }
        } catch (e: Exception) {
            Main.showError(e)
        }

    }

    /**
     * Annule le changement
     */
    @FXML
    private fun annuler() {
        dialogStage!!.close()
    }

}
