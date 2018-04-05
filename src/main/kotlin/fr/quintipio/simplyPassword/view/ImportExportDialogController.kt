package fr.quintipio.simplyPassword.view

import fr.quintipio.simplyPassword.Main
import fr.quintipio.simplyPassword.business.PasswordBusiness
import fr.quintipio.simplyPassword.com.ComFile
import fr.quintipio.simplyPassword.contexte.ContexteStatic
import fr.quintipio.simplyPassword.model.Dossier
import fr.quintipio.simplyPassword.model.MotDePasse
import fr.quintipio.simplyPassword.util.CryptUtils
import fr.quintipio.simplyPassword.util.InvalidPasswordException
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller

class ImportExportDialogController : Initializable {

    ///ELEMENT FXML
    @FXML
    private lateinit var formatCombo: ComboBox<String>
    @FXML
    private lateinit var fichierText: TextField
    @FXML
    private lateinit var checkRemplace: CheckBox
    @FXML
    private lateinit var validButton: Button
    @FXML
    private lateinit var parcourirButton: Button

    ///ELEMENT DU CONTROLEUR
    private var dossierSelected: Dossier? = null
    private var export: Boolean = false
    private var listeFormat: ObservableList<String>? = null
    private var extensionSelected: String? = null
    var isOk: Boolean = false
        private set

    private var bundle: ResourceBundle? = null
    private var dialogStage: Stage? = null


    ///INIT GETTER SETTER

    /**
     * Démarrage de la vue
     */
    override fun initialize(location: URL, resources: ResourceBundle) {
        isOk = false
        listeFormat = FXCollections.observableArrayList("", "CSV", "XML", ContexteStatic.extensionExport.subSequence(1, ContexteStatic.extensionExport.length).toString().toUpperCase())
        bundle = resources
        formatCombo.items.addAll(listeFormat!!)
        validButton.isDisable = true
        extensionSelected = "all"
    }

    /**
     * A éxécuter une fois le controleur chargé et initialisé
     */
    fun init() {
        if (!export) {
            formatCombo.setDisable(true)
        } else {
            parcourirButton.isDisable = true
            checkRemplace.isDisable = true
        }
    }

    fun setDialogStage(dialogStage: Stage) {
        this.dialogStage = dialogStage
    }

    fun setDossierSelected(dossierSelected: Dossier) {
        this.dossierSelected = dossierSelected
    }

    fun setExport(export: Boolean) {
        this.export = export
    }

    ///CHOIX POUR IMPORT EXPORT
    /**
     * Evènement lors du choix d'un format
     */
    @FXML
    private fun selectChoix() {
        extensionSelected = if (formatCombo.selectionModel.selectedItem.isNotBlank()) "." + formatCombo.selectionModel.selectedItem else ""
        if (export) {
            parcourirButton.isDisable = extensionSelected?.isBlank() ?: true
            fichierText.text = ""
        }
        checkButtonEnable()
    }

    /**
     * Ouvre un fichier pour un import ou un export
     */
    @FXML
    private fun openFile() {
        try {
            if (export) {
                val fileChooser = FileChooser()
                fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("*" + extensionSelected!!, "*" + extensionSelected!!))
                val file = fileChooser.showSaveDialog(dialogStage)
                if (file != null) {
                    if (file.path.toLowerCase().endsWith("." + listeFormat!![1].toLowerCase()) || file.path.toLowerCase().endsWith("." + listeFormat!![2].toLowerCase()) || file.path.toLowerCase().endsWith("." + listeFormat!![3].toLowerCase())) {
                        fichierText.text = file.path
                    } else {
                        fichierText.text = file.path + "." + extensionSelected
                    }
                }
            } else {
                val fileChooser = FileChooser()
                for (string in listeFormat!!) {
                    if (string.isNotBlank()) {
                        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("*.$string", "*.$string"))
                    }
                }
                val file = fileChooser.showOpenDialog(dialogStage)
                if (file != null) {
                    if (file.path.toLowerCase().endsWith("." + listeFormat!![1].toLowerCase()) || file.path.toLowerCase().endsWith("." + listeFormat!![2].toLowerCase()) || file.path.toLowerCase().endsWith("." + listeFormat!![3].toLowerCase())) {
                        fichierText.text = file.path
                        val extension = file.path.substring(file.path.length - 3)
                        val index = listeFormat!!.indexOf(extension.toUpperCase())
                        formatCombo.selectionModel.select(index)
                    } else {
                        val alert = Alert(AlertType.ERROR)
                        alert.initOwner(dialogStage)
                        alert.title = bundle!!.getString("erreur")
                        alert.headerText = bundle!!.getString("erreur")
                        alert.contentText = bundle!!.getString("formatNonPris")
                        alert.showAndWait()
                    }
                }
            }
        } catch (e: Exception) {
            Main.showError(e)
        }

        checkButtonEnable()
    }


    ///VALIDATION
    /**
     * Lance l'inport ou l'export
     */
    @FXML
    private fun valider() {
        try {
            val file = ComFile(fichierText.text)
            var ok = false
            //export
            if (export) {
                var data: ByteArray? = null

                //csv
                if (extensionSelected!!.toUpperCase().contentEquals("." + listeFormat!![1].toUpperCase())) {
                    data = exportCsv(dossierSelected)
                    ok = true
                }

                //xml
                if (extensionSelected!!.toUpperCase().contentEquals("." + listeFormat!![2].toUpperCase())) {
                    data = exportXml(dossierSelected)
                    ok = true
                }

                //spj
                if (extensionSelected!!.toUpperCase().contentEquals("." + listeFormat!![3].toUpperCase())) {
                    val mdp = askPassword()
                    if (mdp?.isNotBlank() == true) {
                        data = exportSpe(dossierSelected, mdp)
                        ok = true
                    }
                }

                //export
                file.writeFile(data!!, true)
            } else {
                val data = file.readFileToByteArray()
                var dossier: Dossier? = null

                //csv
                if (extensionSelected!!.toUpperCase().contentEquals("." + listeFormat!![1].toUpperCase())) {
                    dossier = importCsv(data!!)
                    ok = true
                }

                //xml
                if (extensionSelected!!.toUpperCase().contentEquals("." + listeFormat!![2].toUpperCase())) {
                    dossier = importXml(data!!)
                    ok = true
                }

                //spj
                if (extensionSelected!!.toUpperCase().contentEquals("." + listeFormat!![3].toUpperCase())) {
                    val mdp = askPassword()
                    if (mdp?.isNotBlank() == true) {
                        dossier = importSpe(data!!, mdp)
                        ok = true
                    }
                }

                //import
                if (checkRemplace.isSelected) {
                    dossierSelected!!.titre = dossier!!.titre
                    dossierSelected!!.listeMotDePasse = dossier.listeMotDePasse
                    dossierSelected!!.sousDossier = dossier.sousDossier
                } else {
                    dossier!!.dossierParent = dossierSelected
                    dossierSelected!!.sousDossier.add(dossier)
                }
                PasswordBusiness.modif  = true
            }//import

            if (ok) {
                this.isOk = true
                val alert = Alert(AlertType.INFORMATION)
                alert.initOwner(dialogStage)
                alert.title = bundle!!.getString(if (export) "okExp" else "okImp")
                alert.headerText = bundle!!.getString(if (export) "okExp" else "okImp")
                alert.contentText = bundle!!.getString(if (export) "okExp" else "okImp")
                alert.showAndWait()
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

    }

    /**
     * Ferme la fenetre
     */
    @FXML
    private fun annuler() {
        dialogStage!!.close()
    }


    ///OUTILS
    /**
     * Vérifie si le bouton valider est disponible
     */
    private fun checkButtonEnable() {
        validButton.isDisable = formatCombo.selectionModel.selectedItem.isBlank() && fichierText.text.trim { it <= ' ' }.isBlank()
    }


    ///IMPORT OU EXPORT

    //CSV
    /**
     * Converti les données d'un csv en Dossier
     * @param data les données
     * @return le dossier
     */
    private fun importCsv(data: ByteArray): Dossier {
        val dossierImport = Dossier(titre = bundle!!.getString("import"))

        val toRead = String(data)
        val byLine = toRead.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (string in byLine) {
            val stringTrim = string.substring(1, string.length - 1)
            val byElement = stringTrim.split("\",\"".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val mdp = MotDePasse(titre = byElement[0], login = byElement[1],
                    motDePasseObjet = byElement[2])
            mdp.dossierPossesseur = dossierImport
            if (byElement.size > 3) {
                mdp.siteWeb = byElement[3]
            }
            if (byElement.size > 4) {
                mdp.commentaire = byElement[4]
            }
            dossierImport.listeMotDePasse.add(mdp)
        }
        return dossierImport
    }

    /**
     * Exporte un dossier en String csv
     * @param dossier le dossier
     * @return la chaine à àcrire
     */
    private fun exportCsv(dossier: Dossier?): ByteArray {
        val data = convertDossierToCsv(dossier!!)
        return data.toByteArray()
    }

    /**
     * Màthode récursive pour ràcupàrer les mots de passe de sous dossies
     * @param dossier le dossier à lire
     * @return la chaine
     */
    private fun convertDossierToCsv(dossier: Dossier): String {
        val data = StringBuilder()
        if (dossier.listeMotDePasse.isNotEmpty()) {
            for (mdp in dossier.listeMotDePasse) {
                data.append("\"").append(mdp.titre).append("\",\"").append(mdp.login).append("\",\"").append(mdp.motDePasseObjet).append("\",\"").append(mdp.siteWeb).append("\",\"").append(mdp.commentaire).append("\";")
            }
        }
        if (dossier.sousDossier.isNotEmpty()) {
            for (dos in dossier.sousDossier) {
                data.append(convertDossierToCsv(dos))
            }
        }
        return data.toString()
    }


    //XML
    /**
     * Converti des données xml en Dossier
     * @param data les données
     * @return le dossier
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun importXml(data: ByteArray): Dossier {

        val context = JAXBContext.newInstance(Dossier::class.java)
        val un = context.createUnmarshaller()
        val `is` = ByteArrayInputStream(data)
        var dossierImport = un.unmarshal(`is`) as Dossier
        dossierImport = PasswordBusiness.construireElementParent(dossierImport, null)
        return dossierImport
    }

    /**
     * Converti un dossier en xml
     * @param dossier le dossier
     * @return les données xml
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun exportXml(dossier: Dossier?): ByteArray {
        val context = JAXBContext.newInstance(Dossier::class.java)
        val m = context.createMarshaller()
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        val wr = StringWriter()
        m.marshal(dossier, wr)
        return wr.toString().toByteArray(charset("UTF-8"))
    }


    //SPJ
    /**
     * Converti des données lues d'un fichier spj en dossier
     * @param data les données
     * @param mdp le mot de passe de déchiffrement
     * @return le dossier
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun importSpe(data: ByteArray, mdp: String?): Dossier {
        val input = ByteArrayInputStream(data)
        val output = ByteArrayOutputStream()
        CryptUtils.decrypt(mdp!!.toCharArray(), input, output)

        val xml = String(output.toByteArray(), StandardCharsets.UTF_8)
        val context = JAXBContext.newInstance(Dossier::class.java)
        val un = context.createUnmarshaller()

        var dossierImport : Dossier
        dossierImport = un.unmarshal(StringReader(xml)) as Dossier
        dossierImport = PasswordBusiness.construireElementParent(dossierImport, null)
        return dossierImport
    }

    /**
     * Exporte un dossier en format Spj
     * @param dossier le dossier à exporter
     * @param mdp le mot de passe de chiffrement
     * @return les données
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun exportSpe(dossier: Dossier?, mdp: String?): ByteArray {
        val context = JAXBContext.newInstance(Dossier::class.java)
        val marshaller = context.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        val wr = StringWriter()
        marshaller.marshal(dossier, wr)
        val xml = wr.toString()

        val input = ByteArrayInputStream(xml.toByteArray(StandardCharsets.UTF_8))
        val output = ByteArrayOutputStream()
        CryptUtils.encrypt(128, mdp!!.toCharArray(), input, output)
        return output.toByteArray()
    }


    /**
     * Affiche une dlg pour demander le mot de passe
     * @return le mot de passe
     */
    private fun askPassword(): String? {
        val dlg = Dialog<String>()
        dlg.title = bundle!!.getString("entrezMdp")
        dlg.headerText = bundle!!.getString("entrezMdpDechiffrement")

        val la = Label()
        la.text = bundle!!.getString("mdp")
        val fieldA = PasswordField()

        val lb = Label()
        lb.text = bundle!!.getString("confirmMdp")
        val fieldB = PasswordField()

        val grid = GridPane()
        grid.add(la, 0, 0)
        grid.add(fieldA, 1, 0)
        grid.add(lb, 0, 1)
        grid.add(fieldB, 1, 1)
        dlg.dialogPane.content = grid

        val okButton = ButtonType("OK", ButtonData.OK_DONE)
        dlg.dialogPane.buttonTypes.addAll(okButton, ButtonType.CANCEL)
        val validButton = dlg.dialogPane.lookupButton(okButton)
        validButton.isDisable = true

        fieldA.textProperty().addListener { _ , _, newValue -> validButton.isDisable = newValue.trim { it <= ' ' }.isEmpty() || newValue.length < 8 || !newValue!!.contentEquals(fieldB.text) }
        fieldB.textProperty().addListener { _ , _, newValue -> validButton.isDisable = newValue.trim { it <= ' ' }.isEmpty() || newValue.length < 8 || !newValue!!.contentEquals(fieldA.text) }
        Platform.runLater { fieldA.requestFocus() }

        dlg.setResultConverter {  if(it == okButton) fieldA.text else "" }



        val res = dlg.showAndWait()

        return res.orElse(null)
    }
}
