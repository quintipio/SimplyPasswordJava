package fr.quintipio.simplypassword.view

import fr.quintipio.simplypassword.Main
import fr.quintipio.simplypassword.util.CryptUtils
import fr.quintipio.simplypassword.util.InvalidPasswordException
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*

class FileCryptDialogController : Initializable {

    //ELEMENT FXML
    @FXML
    private lateinit var labelHaut: Label 
    @FXML
    private lateinit var labelBas: Label 
    @FXML
    private lateinit var fieldHaut: TextField 
    @FXML
    private lateinit var fieldBas: TextField 
    @FXML
    private lateinit var passwordA: PasswordField 
    @FXML
    private lateinit var passwordB: PasswordField 
    @FXML
    private lateinit var progressMdp: ProgressBar 
    @FXML
    private lateinit var progressChiffrement: ProgressBar 
    @FXML
    private lateinit var validButton: Button 
    @FXML
    private lateinit var parcourirB: Button 
    @FXML
    private lateinit var parcourirA: Button 


    private var cryptFichier: Boolean = false
    private var nameFileIn: String? = null

    private var bundle: ResourceBundle? = null
    private var dialogStage: Stage? = null


    ///INIT GETTER ET SETTER

    /**
     * Initialisation de la vue
     */
    override fun initialize(location: URL, resources: ResourceBundle) {
        bundle = resources
        fieldHaut.textProperty().addListener { _, _, newValue ->
            checkButtonValid()
            parcourirB.isDisable = newValue.isBlank()
        }
        fieldBas.textProperty().addListener { _ -> checkButtonValid() }
        passwordA.textProperty().addListener {  _, _, newValue ->
            checkButtonValid()
            progressMdp.progress = CryptUtils.calculerForceMotDePasse(newValue).toDouble() / 100.toDouble()
        }
        passwordB.textProperty().addListener { _ -> checkButtonValid() }
    }

    /**
     * Initililisation des paramètres de la vue
     */
    fun init(cryptFichier: Boolean) {
        this.cryptFichier = cryptFichier
        if (cryptFichier) {
            validButton.text = bundle!!.getString("chiffrer")
            labelHaut.text = bundle!!.getString("fichierAChiffrer")
            labelBas.text = bundle!!.getString("fichierChiffrer")
        } else {
            validButton.text = bundle!!.getString("dechiffrer")
            labelHaut.text = bundle!!.getString("fichierADechiffrer")
            labelBas.text = bundle!!.getString("fichierDechiffrer")

        }
        parcourirB.isDisable = true
        progressMdp.progress = 0.0
        progressChiffrement.progress = 0.0
        fieldHaut.text = ""
        fieldBas.text = ""
        validButton.isDisable = true
    }


    fun setDialogStage(dialogStage: Stage) {
        this.dialogStage = dialogStage
    }


    //METHOD FXML
    /**
     * Bouton pour sélectionner un fichier d'entrée
     */
    @FXML
    private fun parcourirA() {
        try {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("*", "*.*"))
            val file = fileChooser.showOpenDialog(dialogStage)
            if (file != null) {
                fieldHaut.text = file.path
                nameFileIn = file.name
            }
        } catch (ex: Exception) {
            Main.showError(ex)
        }

    }

    /**
     * Bouton pour sélectionner un fichier de sortie
     */
    @FXML
    private fun parcourirB() {
        try {
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("*", "*.*"))
            fileChooser.initialFileName = nameFileIn
            val file = fileChooser.showSaveDialog(dialogStage)
            if (file != null) {
                fieldBas.text = file.path
            }
        } catch (e: Exception) {
            Main.showError(e)
        }

    }

    /**
     * Bouton de validation
     */
    @FXML
    private fun valid() {
        try {
            val fileIn = File(fieldHaut.text)
            val fileOut = File(fieldBas.text)
            if (validate(fileIn)) {
                progressChiffrement.progress = -1.0
                disablePanel(true)
                val data = Files.readAllBytes(fileIn.toPath())
                val input = ByteArrayInputStream(data)
                val output = ByteArrayOutputStream()
                if (cryptFichier) {
                    CryptUtils.encrypt(128, passwordA.text.toCharArray(), input, output)
                } else {
                    CryptUtils.decrypt(passwordA.text.toCharArray(), input, output)
                }
                Files.write(fileOut.toPath(), output.toByteArray(), StandardOpenOption.CREATE)
            }
            dialogStage!!.close()
        } catch (e: InvalidPasswordException) {
            val alert = Alert(AlertType.ERROR)
            alert.initOwner(dialogStage)
            alert.title = bundle!!.getString("erreur")
            alert.headerText = bundle!!.getString("erreur")
            alert.contentText = bundle!!.getString("erreurMdp")
            alert.showAndWait()
        } catch (ex: Exception) {
            Main.showError(ex)
        }

        progressChiffrement.progress = -1.0
        disablePanel(false)
    }

    /**
     * Méthode de validation de l'action de chiffrement / déchiffrement
     * @param fileIn le fichier d'entrée
     * @return true si ok
     */
    private fun validate(fileIn: File): Boolean {
        var erreur = ""

        if (!fileIn.exists()) {
            erreur += bundle!!.getString("erreurFichierNotExist") + "\r\n"
        }

        if (fileIn.exists() && fileIn.length() > 100000000) {
            erreur += bundle!!.getString("erreurFichierGros") + "\r\n"
        }

        if (erreur.isNotBlank()) {
            val alert = Alert(AlertType.ERROR)
            alert.initOwner(dialogStage)
            alert.title = bundle!!.getString("erreur")
            alert.headerText = bundle!!.getString("erreur")
            alert.contentText = erreur
            alert.showAndWait()
            return false
        }
        return true
    }

    /**
     * Bloque ou non tout les champs de la vue
     * @param disable true si bloqué
     */
    private fun disablePanel(disable: Boolean) {
        parcourirA.isDisable = disable
        parcourirB.isDisable = disable
        validButton.isDisable = disable
        passwordA.isDisable = disable
        passwordB.isDisable = disable
    }

    /**
     * Bouton d'annulation et/ou de fermeture de la dlg
     */
    @FXML
    private fun annule() {
        dialogStage!!.close()
    }

    //METHODE INTERNE AU CONTROLEUR
    /**
     * Autorise ou non le disable du bouton de validation
     */
    private fun checkButtonValid() {
        validButton.isDisable = fieldHaut.text.isBlank() || fieldBas.text.isBlank() ||
                passwordA.text.length < 8 || passwordB.text.length < 8 ||
                passwordA.text.isNotBlank() && passwordB.text.isNotBlank() && passwordA.text != passwordB.text
    }


}
