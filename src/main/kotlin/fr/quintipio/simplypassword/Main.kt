package fr.quintipio.simplypassword

import fr.quintipio.simplypassword.business.ParamBusiness
import fr.quintipio.simplypassword.business.PasswordBusiness
import fr.quintipio.simplypassword.contexte.ContexteStatic
import fr.quintipio.simplypassword.model.Dossier
import fr.quintipio.simplypassword.model.MotDePasse
import fr.quintipio.simplypassword.util.InvalidPasswordException
import fr.quintipio.simplypassword.view.*
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ButtonBar.ButtonData
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*


/**
 * Classe de démarrage de l'application
 */
class Main : Application() {

    /**
     * Getter de l'appli de base
     * @return l'appli de base
     */
    lateinit var primaryStage: Stage
        private set

    private lateinit var rootLayout: BorderPane


    /**
     * Démarrage de la fenêtre principale
     * @param primaryStage
     * @throws Exception
     */
    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage
        this.primaryStage.title = ContexteStatic.nomAppli
        this.primaryStage.icons.add(Image(javaClass.getResource("/rsc/icon.png").toExternalForm()))

        //chargement de la configuration
        if (!ParamBusiness.isModeLive()) {
            ParamBusiness.getDonneesParamUser()
        }

        //évènement de fermeture
        this.primaryStage.setOnCloseRequest { t ->
            if (!askSave()) {
                t.consume()
            }
        }

        //démarrage de la fenêtre
        ouvrirFenetre(PasswordBusiness.isFichier())
    }

    /**
     * Lance la fenêtre mère
     * @param askPassword boolean pour ouvrir la fenetre de mot de passe pour déchiffrer un fichier (uniquement à true au démarrage)
     */
    private fun initRootLayout(askPassword: Boolean) {
        try {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val loader = FXMLLoader(javaClass.getResource("/view/RootLayout.fxml"), bundle)
            rootLayout = loader.load<BorderPane>()

            val scene = Scene(rootLayout)
            primaryStage.scene = scene

            val controller = loader.getController<RootLayoutController>()
            controller.main = this

            //affiche la dlg des mots de passe, s'il faut ouvrir un fichier
            if (askPassword) {
                var erreurMdp: Boolean
                var getError = false
                do {
                    erreurMdp = false
                    val dlg = Dialog<String>()
                    dlg.title = ContexteStatic.nomAppli
                    dlg.headerText = if (PasswordBusiness.isFichier()) PasswordBusiness.fichier!!.file.absolutePath else ContexteStatic.nomAppli
                    val img = ImageView(javaClass.getResource("/rsc/key.png").toExternalForm())
                    img.resize(64.0, 64.0)
                    dlg.graphic = img


                    val la = Label()
                    la.text = bundle.getString("entrezmdp")
                    val lb = Label()
                    lb.text = bundle.getString("erreurDechiffre")
                    lb.textFill = Color.RED
                    val field = PasswordField()

                    val grid = GridPane()
                    grid.add(la, 0, 0)
                    grid.add(field, 1, 0)
                    if (getError) {
                        grid.add(lb, 0, 1, 2, 1)
                    }
                    dlg.dialogPane.content = grid

                    val okButton = ButtonType("OK", ButtonData.OK_DONE)
                    dlg.dialogPane.buttonTypes.addAll(okButton, ButtonType.CANCEL)
                    val validButton = dlg.dialogPane.lookupButton(okButton)
                    validButton.isDisable = true

                    field.textProperty().addListener { _ ,_, newValue -> validButton.isDisable = newValue.trim { it <= ' ' }.isEmpty() || newValue.length < 8 }
                    Platform.runLater { field.requestFocus() }

                    dlg.setResultConverter {  if(it == okButton) field.text else null }

                    val mdp = dlg.showAndWait()

                    if (mdp.isPresent) {
                        try {
                            PasswordBusiness.load(PasswordBusiness.fichier!!.file.path, mdp.get())
                        } catch (ex: InvalidPasswordException) {
                            erreurMdp = true
                            getError = true
                        }

                    } else {
                        PasswordBusiness.createFichier("", false)
                    }
                } while (erreurMdp)
            }

            if(PasswordBusiness.dossierMere.sousDossier.size == 0 && PasswordBusiness.dossierMere.listeMotDePasse.size == 0) {
                PasswordBusiness.dossierMere = Dossier(titre = bundle.getString("racine"))
            }

            primaryStage.show()
        } catch (e: Exception) {
            Main.showError(e)
        }

    }


    //OUVETURE DE FENETRES
    /**
     * Affiche le contenu de la fenêtre principale
     */
    private fun showMainView() {
        try {
            val loader = FXMLLoader(Main::class.java.getResource("/view/MainView.fxml"), ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase())))
            val mainView = loader.load<AnchorPane>()

            val controller = loader.getController<MainViewController>()
            controller.setMain(this)
            rootLayout.center = mainView
        } catch (e: IOException) {
            Main.showError(e)
        }

    }

    /**
     * Affiche la boite de dialogue pour ajouter ou modifier un mot de passe
     * @param mdp le mot de passe à modifier sinon null
     */
    fun showEditPassword(mdp: MotDePasse?): MotDePasse? {
        try {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val loader = FXMLLoader(Main::class.java.getResource("/view/PasswordEditDialog.fxml"), bundle)
            val page = loader.load<AnchorPane>()

            val dialogStage = Stage()
            dialogStage.isResizable = false
            dialogStage.icons.add(Image(javaClass.getResource("/rsc/icon.png").toExternalForm()))
            dialogStage.title = if (mdp == null) bundle.getString("creerMdp") else bundle.getString("modifMdp")
            dialogStage.initModality(Modality.WINDOW_MODAL)
            dialogStage.initOwner(primaryStage)
            val scene = Scene(page)
            dialogStage.scene = scene

            val controller = loader.getController<PasswordEditDialogController>()
            controller.main = this
            controller.dialogStage = dialogStage
            controller.modifMotdePasse(mdp)

            dialogStage.showAndWait()

            return controller.motdePasse
        } catch (ex: Exception) {
            Main.showError(ex)
            return null
        }

    }

    /**
     * Affiche la boite de dialogue pour modifier le mot de passe maître
     */
    fun showChangeMasterPassword() {
        try {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val loader = FXMLLoader(Main::class.java.getResource("/view/GererMasterPasswordDialog.fxml"), bundle)
            val page = loader.load<AnchorPane>()

            val dialogStage = Stage()
            dialogStage.isResizable = false
            dialogStage.icons.add(Image(javaClass.getResource("/rsc/icon.png").toExternalForm()))
            dialogStage.title = bundle.getString("changerMdpMaitre")
            dialogStage.initModality(Modality.WINDOW_MODAL)
            dialogStage.initOwner(primaryStage)
            val scene = Scene(page)
            dialogStage.scene = scene

            val controller = loader.getController<GererMasterPasswordDialogController>()
            controller.setDialogStage(dialogStage)

            dialogStage.showAndWait()

            return
        } catch (ex: Exception) {
            Main.showError(ex)
            return
        }

    }

    /**
     * Affiche la boite de dialogue pour l'import/export
     * @param dossier le dossier dans lequel s'effectue l'import / export
     * @param export true si export sinon import
     */
    fun showImportExport(dossier: Dossier, export: Boolean) {
        try {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val loader = FXMLLoader(Main::class.java.getResource("/view/ImportExportDialog.fxml"), bundle)
            val page = loader.load<AnchorPane>()

            val dialogStage = Stage()
            dialogStage.isResizable = false
            dialogStage.icons.add(Image(javaClass.getResource("/rsc/icon.png").toExternalForm()))
            dialogStage.title = bundle.getString(if (export) "export" else "import")
            dialogStage.initModality(Modality.WINDOW_MODAL)
            dialogStage.initOwner(primaryStage)
            val scene = Scene(page)
            dialogStage.scene = scene

            val controller = loader.getController<ImportExportDialogController>()
            controller.setDialogStage(dialogStage)
            controller.setDossierSelected(dossier)
            controller.setExport(export)
            controller.init()

            dialogStage.showAndWait()

            if (!export) {
                if (controller.isOk) {
                    ouvrirFenetre(false)
                }
            }

            return
        } catch (ex: Exception) {
            Main.showError(ex)
            return
        }

    }

    /**
     * Affiche la boite de dialogue pour chiffrer/déchiffrer un fichier
     * @param cryptFile true si la dlg doit être en mode chiffrement de fichier sinon false
     */
    fun showCryptFile(cryptFile: Boolean) {
        try {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val loader = FXMLLoader(Main::class.java.getResource("/view/FileCryptDialog.fxml"), bundle)
            val page = loader.load<AnchorPane>()

            val dialogStage = Stage()
            dialogStage.isResizable = false
            dialogStage.icons.add(Image(javaClass.getResource("/rsc/icon.png").toExternalForm()))
            dialogStage.title = bundle.getString(if (cryptFile) "cryptFichier" else "decryptFichier")
            dialogStage.initModality(Modality.WINDOW_MODAL)
            dialogStage.initOwner(primaryStage)
            val scene = Scene(page)
            dialogStage.scene = scene

            val controller = loader.getController<FileCryptDialogController>()
            controller.setDialogStage(dialogStage)
            controller.init(cryptFile)
            dialogStage.showAndWait()
        } catch (ex: Exception) {
            Main.showError(ex)
        }

    }

    /**
     * Affiche la fenêtre pour générer un mot de passe
     * @return
     */
    fun showGenereMotDePasse(stage: Stage): String? {
        try {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val loader = FXMLLoader(Main::class.java.getResource("/view/GenereMdpDialog.fxml"), bundle)
            val page = loader.load<AnchorPane>()

            val dialogStage = Stage()
            dialogStage.isResizable = false
            dialogStage.icons.add(Image(javaClass.getResource("/rsc/icon.png").toExternalForm()))
            dialogStage.title = bundle.getString("genereMdp")
            dialogStage.initModality(Modality.WINDOW_MODAL)
            dialogStage.initOwner(stage)
            val scene = Scene(page)
            dialogStage.scene = scene

            val controller = loader.getController<GenereMdpDialogController>()
            controller.setDialogStage(dialogStage)


            dialogStage.showAndWait()
            return controller.motdePasse

        } catch (e: Exception) {
            Main.showError(e)
            return null
        }

    }


    //OUTILS

    /**
     * Change la langue de l'application
     * @param askPassword indique s'il faut demander le mot de passe à l'ouverture
     */
    fun ouvrirFenetre(askPassword: Boolean) {
        initRootLayout(askPassword)
        showMainView()
    }

    /**
     * Demande de confirmation de sauvegarde
     * @return true si fermeture autorisée sinon false
     */
    fun askSave(): Boolean {
        if (PasswordBusiness.modif) {

            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val alert = Alert(AlertType.WARNING)
            alert.title = bundle.getString("askConfirm")
            alert.headerText = bundle.getString("askConfirm")
            alert.contentText = bundle.getString("askSave")
            alert.buttonTypes.setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)

            val result = alert.showAndWait()

            if (result.get() == ButtonType.YES) {
                return save()
            }

            if (result.get() == ButtonType.CANCEL) {
                return false
            }
        }
        return true
    }

    /**
     * Sauvegarde les données de l'appli
     * @return true si sauvegardée
     */
    fun save(): Boolean {
        if (!PasswordBusiness.isMotDePasse()) {
            showChangeMasterPassword()
        }

        if (PasswordBusiness.isMotDePasse() && !PasswordBusiness.isFichier()) {
            //ouverture d'une dlg de fichier
            val fileChooser = FileChooser()
            fileChooser.extensionFilters.add(FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase() + " (*" + ContexteStatic.extension + ")", "*" + ContexteStatic.extension))
            val file = fileChooser.showSaveDialog(primaryStage)
            if (file != null) {
                if (file.path.endsWith(ContexteStatic.extension)) {
                    PasswordBusiness.createFichier(file.path, true)
                } else {
                    PasswordBusiness.createFichier(file.path + ContexteStatic.extension, true)
                }
            }
        }

        if (PasswordBusiness.isFichier() && PasswordBusiness.isMotDePasse()) {
            try {
                PasswordBusiness.save()
                return true
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
        return false
    }

   companion object {


        /**
         * Méhode de lancement
         * @param args les paramètres de lancement
         */
        fun main(args: Array<String>) {
            Application.launch(*args)
        }

        /**
         * Affiche un message d'erreur avec une exception dans une dialog
         * @param ex l'exception
         */
        fun showError(ex: Exception) {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle, Locale(ParamBusiness.getParamLangue().toLowerCase(), ParamBusiness.getParamLangue().toUpperCase()))
            val alert = Alert(AlertType.ERROR)
            alert.title = bundle.getString("exception")
            alert.headerText = bundle.getString("exception")

            val sw = StringWriter()
            val pw = PrintWriter(sw)
            ex.printStackTrace(pw)
            val exceptionText = sw.toString()
            val textArea = TextArea(exceptionText)
            textArea.isEditable = false
            textArea.isWrapText = true
            textArea.maxWidth = java.lang.Double.MAX_VALUE
            textArea.maxHeight = java.lang.Double.MAX_VALUE

            GridPane.setVgrow(textArea, Priority.ALWAYS)
            GridPane.setHgrow(textArea, Priority.ALWAYS)

            val expContent = GridPane()
            expContent.maxWidth = java.lang.Double.MAX_VALUE
            expContent.add(textArea, 0, 1)

            alert.dialogPane.expandableContent = expContent
            alert.showAndWait()
        }
    }

}
