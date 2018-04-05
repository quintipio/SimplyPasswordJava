package fr.quintipio.simplyPassword.view

import fr.quintipio.simplyPassword.Main
import fr.quintipio.simplyPassword.business.PasswordBusiness
import fr.quintipio.simplyPassword.com.ComFile
import fr.quintipio.simplyPassword.contexte.ContexteStatic
import fr.quintipio.simplyPassword.model.MotDePasse
import fr.quintipio.simplyPassword.util.CryptUtils
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.net.URL
import java.util.*

/**
 * Controlleur de la boite de dialogue de gestion d'un mot de passe
 *
 */
class PasswordEditDialogController : Initializable {

    ///Element JFX
    @FXML
    private lateinit var titreField: TextField 
    @FXML
    private lateinit var loginField: TextField 
    @FXML
    private lateinit var mdpField: PasswordField 
    @FXML
    private lateinit var mdpTextField: TextField 
    @FXML
    private lateinit var webField: TextField 
    @FXML
    private lateinit var commentaireField: TextField 
    @FXML
    private lateinit var affcheMdpCheckbox: CheckBox 
    @FXML
    private lateinit var mdpProgress: ProgressBar 
    @FXML
    private lateinit var validButton: Button 
    @FXML
    private lateinit var recupButton: Button 

    private var bundle: ResourceBundle? = null
    var dialogStage: Stage? = null
    var main: Main? = null

    //le mot de passe à retourner
    var motdePasse : MotDePasse? = null


    //INIT GETTER ET SETTER
    /**
     * méthode de chargement
     */
    override fun initialize(location: URL, resources: ResourceBundle) {
        bundle = resources
        mdpField.isVisible = true
        mdpTextField.isVisible = false
        mdpProgress.progress = 0.0
        titreField.textProperty().addListener { _, _, _ -> checkValiderDisable() }
        loginField.textProperty().addListener { _, _, _ -> checkValiderDisable() }
        mdpField.textProperty().addListener { _, _, _ -> checkValiderDisable() }
    }


    /**
     * Charge un mot de passe à modifier
     * @param motdePasse le mot de passe
     */
    fun modifMotdePasse(motdePasse: MotDePasse?) {
        if (motdePasse != null) {
            this.motdePasse = motdePasse
            titreField.text = motdePasse.titre
            loginField.text = motdePasse.login
            ecrireMdp(motdePasse.motDePasseObjet, true)
            webField.text = motdePasse.siteWeb
            commentaireField.text = motdePasse.commentaire
            recupButton.isVisible = false
        } else {
            this.motdePasse = MotDePasse()
            titreField.text = ""
            loginField.text = ""
            mdpField.text = ""
            mdpTextField.text = ""
            webField.text = ""
            commentaireField.text = ""
            recupButton.isVisible = true
            validButton.isDisable = true
        }
    }


    //Methodes liés au FXML
    /**
     * Permet la récupération d'un mot de passe
     */
    @FXML
    private fun recupMdp() {
        try {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter(ContexteStatic.extensionPartage.toUpperCase() + " (*" + ContexteStatic.extensionPartage + ")", "*" + ContexteStatic.extensionPartage))
            val file = fileChooser.showOpenDialog(dialogStage)
            if (file != null) {
                val fichier = ComFile(file.path)
                val mdp = PasswordBusiness.dechiffrerPartage(fichier.readFileToByteArray()!!)
                modifMotdePasse(mdp)
            }
        } catch (ex: Exception) {
            Main.showError(ex)
        }

    }

    /**
     * Affiche ou masque le mot de passe
     */
    @FXML
    private fun afficheMasqueMdp() {
        mdpField.isVisible = !affcheMdpCheckbox.isSelected
        mdpTextField.isVisible = affcheMdpCheckbox.isSelected
    }

    /**
     * Ouvre la dlg pour générer un mot de passe
     */
    @FXML
    private fun genereMdp() {
        val mdp = main!!.showGenereMotDePasse(dialogStage!!)
        if (mdp != null) {
            ecrireMdp(mdp, true)
            mdpField.isVisible = false
            mdpTextField.isVisible = true
            affcheMdpCheckbox.isSelected = true
        }
    }

    /**
     * Copie les données du PasswordField vers le TextField
     */
    @FXML
    private fun ecrireMdpPasswordField() {
        mdpTextField.text = mdpField.text
        ecrireMdp(mdpField.text, false)
    }

    /**
     * Copie les données du PasswordField vers le TextField
     */
    @FXML
    private fun ecrireMdpTextField() {
        mdpField.text = mdpTextField.text
        ecrireMdp(mdpTextField.text, false)
    }

    /**
     * Bouton valider (vérifie les données, et charge le mot de passe)
     */
    @FXML
    private fun valider() {
        if (validate()) {
            motdePasse?.titre = titreField.text
            motdePasse?.login = loginField.text
            motdePasse?.motDePasseObjet = mdpField.text
            motdePasse?.siteWeb = webField.text
            motdePasse?.commentaire = commentaireField.text
            dialogStage!!.close()
        }

    }

    /**
     * Ferme la fenetre
     */
    @FXML
    private fun annuler() {
        this.motdePasse = null
        dialogStage!!.close()
    }


    //OUTILS DU CONTROLEUR
    /**
     * Ecrit un mot de passe dans tout les champs et calcul sa force
     * @param mdp le mot de passe
     */
    private fun ecrireMdp(mdp: String, ecrire: Boolean) {
        if (ecrire) {
            mdpField.text = mdp
            mdpTextField.text = mdp
        }
        mdpProgress.progress = CryptUtils.calculerForceMotDePasse(mdp).toDouble() / 100
    }

    /**
     * Controle les données entràs par l'utilisateur et affiche les erreurs
     * @return true si ok
     */
    private fun validate(): Boolean {
        var errorMessage = ""

        if (titreField.text.isBlank()) {
            errorMessage += bundle!!.getString("titreInvalide") + "\n"
        }
        if (loginField.text.isBlank()) {
            errorMessage += bundle!!.getString("titreInvalide") + "\n"
        }
        if (mdpField.text.isBlank()) {
            errorMessage += bundle!!.getString("mdpInvalide") + "\n"
        }

        return if (errorMessage.isEmpty()) true else {
            // Show the error message.
            val alert = Alert(AlertType.ERROR)
            alert.initOwner(dialogStage)
            alert.title = bundle!!.getString("erreur")
            alert.headerText = bundle!!.getString("erreurVide")
            alert.contentText = errorMessage
            alert.showAndWait()
            false
        }
    }

    /**
     * Vérifie si oui ou non le bouton valider est actif
     */
    private fun checkValiderDisable() {
        validButton.isDisable = mdpField.text.isBlank() || loginField.text.isBlank() || titreField.text.isBlank()
    }
}
