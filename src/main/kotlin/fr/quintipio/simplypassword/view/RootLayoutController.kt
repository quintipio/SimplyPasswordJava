package fr.quintipio.simplypassword.view

import fr.quintipio.simplypassword.Main
import fr.quintipio.simplypassword.business.ParamBusiness
import fr.quintipio.simplypassword.business.PasswordBusiness
import fr.quintipio.simplypassword.contexte.ContexteStatic
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.control.ButtonType
import javafx.scene.control.ChoiceDialog
import javafx.scene.layout.GridPane
import javafx.scene.control.Dialog
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.stage.FileChooser

import java.net.URL
import java.util.ArrayList
import java.util.Calendar
import java.util.ResourceBundle

/**
 * Controleur de la fenetre Racine
 */
class RootLayoutController : Initializable {

    ///GETTER ET SETTER
    var main: Main? = null
    private var bundle: ResourceBundle? = null

    ///INIT

    override fun initialize(location: URL, resources: ResourceBundle) {
        bundle = resources
    }


    ///ACTION

    /**
     * Quitter l'application
     */
    @FXML
    private fun close() {
        if (main!!.askSave()) {
            main!!.primaryStage.close()
        }
    }

    /**
     * Sauvegarder sous un nouveau fichier
     */
    @FXML
    private fun saveAs() {
        try {
            //ouverture d'une dlg de fichier
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase() + " (*" + ContexteStatic.extension + ")", "*" + ContexteStatic.extension))
            val file = fileChooser.showSaveDialog(main!!.primaryStage)
            if (file != null) {
                if (file.path.endsWith(ContexteStatic.extension)) {
                    PasswordBusiness.createFichier(file.path, true)
                } else {
                    PasswordBusiness.createFichier(file.path + ContexteStatic.extension, true)
                }
            }

            //sauvegarde
            if (file != null && file.path != null && !file.path.isEmpty()) {
                save()
            }
        } catch (e: Exception) {
            Main.showError(e)
        }

    }

    /**
     * Sauvegarder (demande un mot de passe, si aucun, demande un emplacement de sauvegarde si aucun
     */
    @FXML
    private fun save() {
        main!!.save()
    }

    /**
     * Charger un fichier
     */
    @FXML
    private fun load() {
        try {

            if (main!!.askSave()) {
                //chargement du fichier
                val fileChooser = FileChooser()
                fileChooser.extensionFilters.add(FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase() + " (*" + ContexteStatic.extension + ")", "*" + ContexteStatic.extension))
                val file = fileChooser.showOpenDialog(main!!.primaryStage)
                if (file != null) {
                    if (file.path.endsWith(ContexteStatic.extension)) {
                        PasswordBusiness.createFichier(file.path, true)
                    } else {
                        PasswordBusiness.createFichier(file.path + ContexteStatic.extension, true)
                    }

                    main!!.ouvrirFenetre(true)
                }
            }

        } catch (e: Exception) {
            Main.showError(e)
        }

    }

    /**
     * Réinitialiser
     */
    @FXML
    private fun newApp() {
        try {
            if (main!!.askSave()) {
                PasswordBusiness.reset()
                main!!.ouvrirFenetre(false)
            }
        } catch (e: Exception) {
            Main.showError(e)
        }

    }

    /**
     * Ouvre la fenêtre pour changer le mot de passe maître
     */
    @FXML
    private fun changeMdpMaitre() {
        main!!.showChangeMasterPassword()
    }

    /**
     * Ouvre la boite de dialogue pour changer de langue
     */
    @FXML
    private fun changeLangue() {
        try {
            val listeLangue = ArrayList<String>()
            for (lang in ContexteStatic.listeLangues) {
                listeLangue.add(bundle!!.getString(lang))
            }

            var choixDefaut = 0
            if (ParamBusiness.getParamLangue().contentEquals(listeLangue[1])) {
                choixDefaut = 1
            }

            val dialog = ChoiceDialog(listeLangue[choixDefaut], listeLangue)
            dialog.title = bundle!!.getString("changerlangue")
            dialog.headerText = bundle!!.getString("selectLangue")
            dialog.contentText = bundle!!.getString("langue")

            val result = dialog.showAndWait()
            if(result.isPresent) {
                appliquerLangue(result.get(),listeLangue)
            }
        } catch (e: Exception) {
            Main.showError(e)
        }

    }

    /**
     * Applique une nouvelle langue
     * @param langue la langue sélectionné
     * @param liste la liste des langues
     */
    private fun appliquerLangue(langue: String, liste: List<String>) {

        if (langue == liste[0]) {
            ParamBusiness.parametreLangue = ContexteStatic.listeLangues[0]
            main!!.ouvrirFenetre(false)
        }
        if (langue == liste[1]) {
            ParamBusiness.parametreLangue = ContexteStatic.listeLangues[1]
            main!!.ouvrirFenetre(false)
        }
        ParamBusiness.ecrireFichierParamUser()

    }

    /**
     * Ouvre la dlg de chiffrement d'un fichier
     */
    @FXML
    private fun cryptFile() {
        main!!.showCryptFile(true)
    }

    /**
     * Ouvre la dlg de déchiffrement d'un fichier
     */
    @FXML
    private fun decryptFile() {
        main!!.showCryptFile(false)
    }

    /**
     * Affiche la boite de dialogue à propos de...
     */
    @FXML
    private fun openAppd() {
        val dlg = Dialog<String>()
        dlg.title = bundle!!.getString("appd")
        dlg.headerText = ContexteStatic.nomAppli
        dlg.graphic = ImageView(javaClass.getResource("/rsc/icon.png").toExternalForm())

        val button = ButtonType("OK", ButtonData.OK_DONE)
        dlg.dialogPane.buttonTypes.add(button)

        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        val laa = Label()
        laa.text = bundle!!.getString("version")
        val lab = Label()
        lab.text = ContexteStatic.version
        val lba = Label()
        lba.text = bundle!!.getString("developpeur")
        val lbb = Label()
        lbb.text = ContexteStatic.developpeur
        val lca = Label()
        lca.text = bundle!!.getString("copyright")
        val lcb = Label()
        lcb.text = ContexteStatic.developpeur + " " + Calendar.getInstance().get(Calendar.YEAR)

        grid.add(laa, 0, 0)
        grid.add(lab, 1, 0)
        grid.add(lba, 0, 1)
        grid.add(lbb, 1, 1)
        grid.add(lca, 0, 2)
        grid.add(lcb, 1, 2)
        dlg.dialogPane.content = grid
        dlg.show()
    }

}
