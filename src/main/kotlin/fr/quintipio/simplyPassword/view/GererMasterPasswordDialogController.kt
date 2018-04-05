package fr.quintipio.simplyPassword.view

import fr.quintipio.simplyPassword.Main
import fr.quintipio.simplyPassword.business.PasswordBusiness
import fr.quintipio.simplyPassword.util.CryptUtils
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.stage.Stage
import java.net.URL
import java.util.*

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

        if (PasswordBusiness.isMotDePasse() && oldMdp.text.isBlank()) {
            errorMessage += bundle!!.getString("oldMdpVide") + "\n"
        }

        if (PasswordBusiness.isMotDePasse() && oldMdp.text != PasswordBusiness.motDePasse) {
            errorMessage += bundle!!.getString("oldMdpDif") + "\n"
        }

        if (newMdp.text.isBlank()) {
            errorMessage += bundle!!.getString("newMdpVide") + "\n"
        }

        if (confMdp.text.isBlank()) {
            errorMessage += bundle!!.getString("confirmmdpVide") + "\n"
        }

        if (confMdp.text.isNotBlank() && newMdp.text.isNotBlank() && newMdp.text != confMdp.text) {
            errorMessage += bundle!!.getString("mdpDif") + "\n"
        }

        if (confMdp.text.isNotBlank() && newMdp.text.isNotBlank() && newMdp.text.length < 8) {
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
        validButton.isDisable = (PasswordBusiness.isMotDePasse() && oldMdp.text.isBlank()
                || PasswordBusiness.isMotDePasse() && oldMdp.text.isNotBlank() && PasswordBusiness.motDePasse != oldMdp.text
                || newMdp.text.isBlank() || newMdp.text.isNotBlank() && newMdp.text.length < 8
                || confMdp.text.isBlank() || confMdp.text.isNotBlank() && confMdp.text.length < 8
                || confMdp.text.isNotBlank() && newMdp.text.isNotBlank() && confMdp.text != newMdp.text
                || newMdp.text.isNotBlank() && oldMdp.text.isNotBlank() && oldMdp.text == newMdp.text)
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
