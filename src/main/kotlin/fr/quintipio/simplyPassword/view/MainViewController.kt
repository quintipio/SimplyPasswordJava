package fr.quintipio.simplyPassword.view

import fr.quintipio.simplyPassword.Main
import fr.quintipio.simplyPassword.business.PasswordBusiness
import fr.quintipio.simplyPassword.com.ComFile
import fr.quintipio.simplyPassword.contexte.ContexteStatic
import fr.quintipio.simplyPassword.model.Dossier
import fr.quintipio.simplyPassword.model.MotDePasse
import fr.quintipio.simplyPassword.model.ObservableMotDePasse

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.cell.TextFieldTreeCell
import javafx.scene.input.*
import javafx.util.StringConverter

import java.net.URL
import java.util.ResourceBundle
import java.util.Timer
import java.util.TimerTask
import java.util.stream.Collectors
import javafx.stage.FileChooser

class MainViewController : Initializable {

    //Elements FXML
    @FXML
    private lateinit var dossierTreeView: TreeView<Dossier>
    @FXML
    private lateinit var mdpTable: TableView<ObservableMotDePasse> 
    @FXML
    private lateinit var titreColumn: TableColumn<ObservableMotDePasse, String> 
    @FXML
    private lateinit var loginColumn: TableColumn<ObservableMotDePasse, String> 
    @FXML
    private lateinit var mdpColumn: TableColumn<ObservableMotDePasse, String> 
    @FXML
    private lateinit var webColumn: TableColumn<ObservableMotDePasse, String> 
    @FXML
    private lateinit var commentColumn: TableColumn<ObservableMotDePasse, String> 
    @FXML
    private lateinit var rechercherTextField: TextField 
    @FXML
    private lateinit var checkMdpAfficher: CheckBox 
    @FXML
    private lateinit var countdownProgressbar: ProgressBar 

    private var bundle: ResourceBundle? = null
    private var main: Main? = null

    private val listeMdp = FXCollections.observableArrayList<ObservableMotDePasse>()

    //ContexteMenu
    private var dossierContexteMenu: ContextMenu? = null
    private var mdpContexteMenu: ContextMenu? = null

    //Elements de mise en mémoire des sélections
    private var selectedDossier: TreeItem<Dossier>? = null
    private var selectedMotDepasse: ObservableMotDePasse? = null

    ///Pour le déplacement des objets
    private var selectedMotDePasseToMove: MotDePasse? = null
    private var coupe: Boolean = false
    private var copie: Boolean = false
    private var collerMenuItem: MenuItem? = null
    private var selectedDossierToMove: Dossier? = null
    private var collerDossierMenuItem: MenuItem? = null

    //pour le timer
    private var timer: Timer? = null
    private var nbSecondepasse: Int = 0


    //INIT GETTER SETTER

    fun setMain(main: Main) {
        this.main = main
    }

    /**
     * Méthode d'initialisation
     */
    override fun initialize(location: URL, resources: ResourceBundle) {
        //PasswordBusiness.init();
        bundle = resources
        genererTreeView()
        startTableMdp()
        afficherOuPasMdp()

        collerMenuItem = MenuItem(resources.getString("coller"))
        collerMenuItem!!.accelerator = KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN)
        collerMenuItem!!.setOnAction {
            try {
                if (selectedDossier != null) {
                    val newMdp = copyMotDePasse(selectedMotDePasseToMove)
                    newMdp.dossierPossesseur = selectedDossier!!.value
                    selectedDossier!!.value.listeMotDePasse.add(newMdp)

                    if (coupe) {
                        selectedMotDePasseToMove?.dossierPossesseur?.listeMotDePasse?.remove(selectedMotDePasseToMove!!)
                    }


                    PasswordBusiness.modif = true
                    supprimerCollerElement()
                    ouvrirDossier(selectedDossier!!.value)
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        collerDossierMenuItem = MenuItem(resources.getString("collerDossier"))
        collerDossierMenuItem!!.setOnAction {
            if (selectedDossier?.value != null && !isDossierEnfant(selectedDossierToMove!!, selectedDossier!!.value) && selectedDossier!!.value !== selectedDossierToMove) {
                selectedDossier?.value?.sousDossier?.add(selectedDossierToMove!!)
                selectedDossierToMove!!.dossierParent?.sousDossier?.remove(selectedDossierToMove!!)
                selectedDossierToMove!!.dossierParent = selectedDossier?.value
                selectedDossierToMove = null
                PasswordBusiness.modif = true
                supprimerCollerDossierElement()
                main!!.ouvrirFenetre(false)
            }
        }
    }


    /**
     * Génère l'arbre des dossiers en ajoutant les évènements
     */
    private fun genererTreeView() {
        //créer les items
        val rootItem = genererTreeItem(PasswordBusiness.dossierMere)
        dossierTreeView.root = rootItem
        dossierTreeView.isEditable = true
        //converter pour la l'édtion d'un dossier
        dossierTreeView.setCellFactory {
            TextFieldTreeCell<Dossier>(object : StringConverter<Dossier>() {

                override fun toString(ob: Dossier): String {
                    return ob.toString()
                }

                override fun fromString(string: String): Dossier {
                    selectedDossier!!.value.titre = string
                    return selectedDossier!!.value
                }
            })
        }
        dossierTreeView.refresh()

        //créer l'évènement de sélection
        dossierTreeView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            selectedDossier = newValue
            ouvrirDossier(selectedDossier!!.value)
        }
        dossierTreeView.isEditable = true
        dossierTreeView.setOnEditCommit { PasswordBusiness.modif = true }


        //créer le menu contextuel
        dossierContexteMenu = ContextMenu()

        val creerDossier = MenuItem(bundle!!.getString("creerDossier"))
        creerDossier.setOnAction {
            try {
                if (selectedDossier != null) {
                    val dossierTmp = Dossier(titre = bundle!!.getString("creerDossier"))
                    dossierTmp.dossierParent = selectedDossier!!.value
                    selectedDossier!!.value.sousDossier.add(dossierTmp)
                    selectedDossier!!.children.add(TreeItem<Dossier>(dossierTmp))
                    PasswordBusiness.modif = true
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val modifierDossier = MenuItem(bundle!!.getString("modifierDossier"))
        modifierDossier.setOnAction {
            try {
                if (selectedDossier != null) {
                    dossierTreeView.edit(selectedDossier)
                    PasswordBusiness.modif = true
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val supprimerDossier = MenuItem(bundle!!.getString("supprimerDossier"))
        supprimerDossier.setOnAction {
            try {
                if (selectedDossier?.value?.dossierParent != null) {
                    val al = Alert(AlertType.CONFIRMATION)
                    al.title = bundle!!.getString("supprimerDossier")
                    al.headerText = bundle!!.getString("supprimerDossier")
                    al.contentText = bundle!!.getString("confirmSupprimerDossier")
                    val res = al.showAndWait()
                    if (res.get() == ButtonType.OK) {
                        selectedDossier?.value?.dossierParent?.sousDossier?.remove(selectedDossier!!.value)
                        val tmp = selectedDossier!!.parent
                        selectedDossier?.parent?.children?.remove(selectedDossier)
                        PasswordBusiness.modif = true
                        selectedDossier = tmp
                    }
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val importerDossier = MenuItem(bundle!!.getString("importerDossier"))
        importerDossier.setOnAction {
            if (selectedDossier != null) {
                main!!.showImportExport(selectedDossier!!.value, false)
            }

        }
        val exporterDossier = MenuItem(bundle!!.getString("exporterDossier"))
        exporterDossier.setOnAction {
            if (selectedDossier != null) {
                main!!.showImportExport(selectedDossier!!.value, true)
            }
        }
        val couperDossier = MenuItem(bundle!!.getString("couperDossier"))
        couperDossier.setOnAction {
            if (selectedDossier?.value?.dossierParent != null) {
                selectedDossierToMove = selectedDossier!!.value
                ajouterCollerDossierElement()

            }
        }

        dossierContexteMenu!!.items.add(creerDossier)
        dossierContexteMenu!!.items.add(modifierDossier)
        dossierContexteMenu!!.items.add(supprimerDossier)
        dossierContexteMenu!!.items.add(SeparatorMenuItem())
        dossierContexteMenu!!.items.add(exporterDossier)
        dossierContexteMenu!!.items.add(importerDossier)
        dossierTreeView.contextMenu = this.dossierContexteMenu
        dossierContexteMenu!!.items.add(couperDossier)
        selectedDossier = rootItem
    }

    /**
     * Démarre la table d'affichage des mots de passe avec les évènements
     */
    private fun startTableMdp() {
        //mise en place des colonnes
        titreColumn.setCellValueFactory { cellData -> cellData.value.titre }
        loginColumn.setCellValueFactory { cellData -> cellData.value.login }
        mdpColumn.setCellValueFactory { cellData -> cellData.value.motDePasseObjet }
        webColumn.setCellValueFactory { cellData -> cellData.value.siteWeb }
        commentColumn.setCellValueFactory { cellData -> cellData.value.commentaire }
        titreColumn.isSortable = true
        titreColumn.sortType = TableColumn.SortType.ASCENDING

        mdpTable.isEditable = false
        mdpTable.items = listeMdp
        mdpTable.sortOrder.add(titreColumn)
        mdpTable.sort()

        //évènement de sélection
        mdpTable.selectionModel.selectedItemProperty().addListener { _, _, newValue -> selectedMotDepasse = newValue }

        //menu contextuel
        mdpContexteMenu = ContextMenu()
        val modifMdp = MenuItem(bundle!!.getString("modifMdp"))
        modifMdp.accelerator = KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN)
        modifMdp.setOnAction {
            try {
                if (selectedMotDepasse != null) {
                    val mdp = main!!.showEditPassword(selectedMotDepasse!!.mdpOri)
                    if (mdp != null) {
                        mdp.dossierPossesseur = selectedDossier!!.value
                        selectedDossier!!.value.listeMotDePasse.remove(selectedMotDepasse!!.mdpOri)
                        selectedDossier!!.value.listeMotDePasse.add(mdp)
                        ouvrirDossier(selectedDossier!!.value)
                        PasswordBusiness.modif = true
                    }
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val supMdp = MenuItem(bundle!!.getString("supprimerMdp"))
        supMdp.setOnAction {
            try {
                if (selectedMotDepasse != null) {
                    val al = Alert(AlertType.CONFIRMATION)
                    al.title = bundle!!.getString("supprimerMdp")
                    al.headerText = bundle!!.getString("supprimerMdp")
                    al.contentText = bundle!!.getString("confirmSupprimerMdp")
                    val res = al.showAndWait()
                    if (res.get() == ButtonType.OK) {
                        selectedDossier!!.value.listeMotDePasse.remove(selectedMotDepasse!!.mdpOri)
                        ouvrirDossier(selectedDossier!!.value)
                        PasswordBusiness.modif = true
                    }
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val shareMdp = MenuItem(bundle!!.getString("partage"))
        shareMdp.accelerator = KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN)
        shareMdp.setOnAction {
            try {
                if (selectedMotDepasse != null) {
                    try {
                        val fileChooser = FileChooser()
                        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter(ContexteStatic.extensionPartage.toUpperCase() + " (*" + ContexteStatic.extensionPartage + ")", "*" + ContexteStatic.extensionPartage))
                        val file = fileChooser.showSaveDialog(main!!.primaryStage)
                        if (file != null) {
                            val data = PasswordBusiness.genererPartage(selectedMotDepasse!!.mdpOri)
                            val fichier = ComFile(file.path)
                            fichier.writeFile(data, true)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        val alert = Alert(AlertType.ERROR)
                        alert.initOwner(main!!.primaryStage)
                        alert.title = bundle!!.getString("erreur")
                        alert.headerText = bundle!!.getString("erreur")
                        alert.contentText = bundle!!.getString("erreurPartage")
                        alert.showAndWait()
                    }

                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val copieLogin = MenuItem(bundle!!.getString("copieLogin"))
        copieLogin.accelerator = KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        copieLogin.setOnAction {
            try {
                if (selectedMotDepasse != null) {
                    copyToClipBoard(selectedMotDepasse!!.getLogin())
                    startTimer()
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val copieMdp = MenuItem(bundle!!.getString("copieMdp"))
        copieMdp.accelerator = KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        copieMdp.setOnAction {
            try {
                if (selectedMotDepasse != null) {
                    copyToClipBoard(selectedMotDepasse!!.mdpOri.motDePasseObjet)
                    startTimer()
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val copier = MenuItem(bundle!!.getString("copier"))
        copier.accelerator = KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN)
        copier.setOnAction {
            try {
                if (selectedMotDepasse != null) {
                    selectedMotDePasseToMove = selectedMotDepasse!!.mdpOri
                    copie = true
                    coupe = false
                    ajouterCollerElement()
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        val couper = MenuItem(bundle!!.getString("couper"))
        couper.accelerator = KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN)
        couper.setOnAction {
            try {
                if (selectedMotDepasse != null) {
                    selectedMotDePasseToMove = selectedMotDepasse!!.mdpOri
                    copie = false
                    coupe = true
                    ajouterCollerElement()
                }
            } catch (e: Exception) {
                Main.showError(e)
            }
        }

        mdpTable.setOnMouseClicked { t ->
            if (t.button == MouseButton.PRIMARY) {
                if (t.clickCount >= 1) {
                    try {
                        //pour éviter un bug de sélection de dossier dans la recherche, en cas de sélection d'un mot de passe, on recherche son dossier dans l'arbre pour le sélectionner
                        if (selectedMotDepasse != null) {
                            val dossierTrouve = findDossier(selectedMotDepasse?.dossierPossesseur, dossierTreeView.root)
                            selectedDossier = dossierTrouve ?: dossierTreeView.root
                            expandTreeView(dossierTreeView.root)
                            val msm = dossierTreeView.selectionModel
                            msm.select(dossierTreeView.getRow(selectedDossier))
                        }
                    } catch (e: Exception) {
                        Main.showError(e)
                    }

                }
                if (t.clickCount == 2) {
                    try {
                        if (selectedMotDepasse != null) {
                            val mdp = main!!.showEditPassword(selectedMotDepasse!!.mdpOri)
                            if (mdp != null) {
                                mdp.dossierPossesseur = selectedDossier!!.value
                                selectedDossier!!.value.listeMotDePasse.remove(selectedMotDepasse!!.mdpOri)
                                selectedDossier!!.value.listeMotDePasse.add(mdp)
                                ouvrirDossier(selectedDossier!!.value)
                                PasswordBusiness.modif = true
                            }
                        }
                    } catch (e: Exception) {
                        Main.showError(e)
                    }

                }

            }
        }

        mdpContexteMenu!!.items.add(copieLogin)
        mdpContexteMenu!!.items.add(copieMdp)
        mdpContexteMenu!!.items.add(SeparatorMenuItem())
        mdpContexteMenu!!.items.add(modifMdp)
        mdpContexteMenu!!.items.add(supMdp)
        mdpContexteMenu!!.items.add(SeparatorMenuItem())
        mdpContexteMenu!!.items.add(copier)
        mdpContexteMenu!!.items.add(couper)
        mdpContexteMenu!!.items.add(SeparatorMenuItem())
        mdpContexteMenu!!.items.add(shareMdp)
        mdpTable.contextMenu = mdpContexteMenu


    }


    //OUVRIR MDP ET DOSSIER
    /**
     * Ouvre un dossier et affiche les mots de passe
     * @param dossier le dossier à ouvrir
     */
    private fun ouvrirDossier(dossier: Dossier) {
        ouvrirMotsDePasse(dossier.listeMotDePasse)
    }

    /**
     * Ouvre une liste de mot de passe dans le tableau
     * @param listeMdp la liste des mots de passe à afficher
     */
    private fun ouvrirMotsDePasse(listeMdp: List<MotDePasse>) {
        this.listeMdp.clear()
        this.listeMdp.addAll(listeMdp.stream().map( { ObservableMotDePasse(it) }).collect(Collectors.toList<ObservableMotDePasse>()))
        this.listeMdp.sortWith(Comparator { mdpA, mdpB -> mdpA.getTitre()!!.compareTo(mdpB.getTitre()!!, ignoreCase = true) })
        afficherOuPasMdp()
    }


    //COPIER COLLER
    /**
     * Ajoute le menu item coller dans les contexteMennu pour coller un mot de passe
     */
    private fun ajouterCollerElement() {
        mdpContexteMenu!!.items.add(collerMenuItem)
    }

    /**
     * Supprime le menu item coller dans les contexteMennu
     */
    private fun supprimerCollerElement() {
        mdpContexteMenu!!.items.remove(collerMenuItem)
    }

    /**
     * Ajoute le menu item coller d'un dossier dans les contexteMennu pour coller un dossier
     */
    private fun ajouterCollerDossierElement() {
        dossierContexteMenu!!.items.add(collerDossierMenuItem)
    }

    /**
     * Supprime le menu item coller d'un dossier dans les contexteMennu
     */
    private fun supprimerCollerDossierElement() {
        dossierContexteMenu!!.items.remove(collerDossierMenuItem)
    }


    //PRESSE PAPIER
    /**
     * Copie du texte dans le presse papier
     * @param data les données à copier
     */
    private fun copyToClipBoard(data: String?) {
        val clipboard = Clipboard.getSystemClipboard()
        val content = ClipboardContent()
        content.putString(data)
        clipboard.setContent(content)
    }

    /**
     * Démarre un timer de compte à rebours, fait diminuer la progress bar, et efface les données du presse-papier au bout d'un certain temps
     */
    private fun startTimer() {
        try {
            if (timer != null) {
                timer!!.cancel()
            }
            countdownProgressbar.progress = 1.0
            nbSecondepasse = 0
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    try {
                        if (nbSecondepasse < ContexteStatic.dureeTimerCopieClipboard) {
                            nbSecondepasse++
                            val recul = 1.toDouble() / ContexteStatic.dureeTimerCopieClipboard.toDouble()
                            Platform.runLater {
                                try {
                                    countdownProgressbar.progress = countdownProgressbar.progress - recul
                                } catch (e: Exception) {

                                }
                            }
                        } else {
                            Platform.runLater {
                                try {
                                    copyToClipBoard("")
                                    countdownProgressbar.progress = 1.0
                                } catch (e: Exception) {

                                }
                            }
                            this.cancel()
                            timer!!.cancel()
                            timer!!.purge()

                        }
                    } catch (e: Exception) {
                    }

                }
            }, 0, 1000)
        } catch (e: Exception) {
        }

    }


    //METHODE FXML
    /**
     * Lance une recherche de mots de passe
     */
    @FXML
    private fun rechercher() = try {
        if ( rechercherTextField.text?.length!! > 0) {
            ouvrirMotsDePasse(PasswordBusiness.recherche(rechercherTextField.text, PasswordBusiness.dossierMere))
        } else {
            ouvrirDossier(selectedDossier!!.value)
        }
    } catch (e: Exception) {
        Main.showError(e)
    }

    /**
     * Charge la boite de dialogue pour un nouveau mot de passe
     */
    @FXML
    private fun nouveauMdp() {
        val mdp = main!!.showEditPassword(null)
        if (mdp != null) {
            mdp.dossierPossesseur = selectedDossier!!.value
            selectedDossier!!.value.listeMotDePasse.add(mdp)
            ouvrirDossier(selectedDossier!!.value)
            PasswordBusiness.modif = true
        }
    }

    /**
     * Affiche ou masque les mots de passe du passe tableau
     */
    @FXML
    private fun afficherOuPasMdp() {
        try {
            if (checkMdpAfficher.isSelected) {
                for (mdp in listeMdp) {
                    mdp.setMotDePasseObjet(mdp.mdpOri.motDePasseObjet)
                }
            } else {
                for (mdp in listeMdp) {
                    mdp.setMotDePasseObjet("********")
                }
            }
        } catch (e: Exception) {
            Main.showError(e)
        }

    }


    //OUTILS
    /**
     * Génère un arbre des items de dossier
     * @param dossier le dossier dont on veut le tree item
     * @return le tree item
     */
    private fun genererTreeItem(dossier: Dossier): TreeItem<Dossier> {

        val dossierTreeItem = TreeItem<Dossier>(dossier)
        dossierTreeItem.isExpanded = true
        if (dossier.sousDossier.isNotEmpty()) {
            for (sousDossier in dossier.sousDossier) {
                val item = genererTreeItem(sousDossier)
                dossierTreeItem.children.add(item)
            }
        }
        return dossierTreeItem
    }

    /**
     * Méthode récursive pour vérifier qu'un dossier n'est pas compris dans un des sous dossiers de root
     * @param root le dossier racine à vérifier
     * @param search le dossier à recherche
     * @return true si présent
     */
    private fun isDossierEnfant(root: Dossier, search: Dossier): Boolean {
        var retour = false
        for (dossier in root.sousDossier) {
            if (dossier === search) {
                retour = true
                break
            }

            if (dossier.sousDossier.isNotEmpty()) {
                if (isDossierEnfant(dossier, search)) {
                    retour = true
                    break
                }
            }
        }
        return retour
    }

    /**
     * Recherche un dossier dans un treeItem<Dossier> à partir de l'objet dossier
     * @param dossierATrouver le dossier à trouver
     * @param dossierDeRecherche l'arbre de dossier dans lequel on recherche
     * @return le dossier trouve sinon null
    </Dossier> */
    private fun findDossier(dossierATrouver: Dossier?, dossierDeRecherche: TreeItem<Dossier>): TreeItem<Dossier>? {
        var trouve: TreeItem<Dossier>? = null
        for (tosearch in dossierDeRecherche.children) {
            if (tosearch.value == dossierATrouver) {
                trouve = tosearch
                break
            } else {
                trouve = findDossier(dossierATrouver, tosearch)
                if (trouve != null) {
                    break
                }
            }
        }
        return trouve
    }

    /**
     * Déploi l'arbre du treeView
     * @param item le dossier à déployer
     */
    private fun expandTreeView(item: TreeItem<*>?) {
        if (item != null && !item.isLeaf) {
            item.isExpanded = true
            for (child in item.children) {
                expandTreeView(child)
            }
        }
    }

    /**
     * Copier un mot de passe
     * @param mdp le mot de passe
     * @return le nouveau mot de passe
     */
    private fun copyMotDePasse(mdp: MotDePasse?): MotDePasse {
        val newMdp = MotDePasse()
        if (mdp != null) {
                newMdp.titre = mdp.titre
                newMdp.login = mdp.login
                newMdp.motDePasseObjet = mdp.motDePasseObjet
                newMdp.siteWeb = mdp.siteWeb
                newMdp.commentaire = mdp.commentaire
                newMdp.idIcone = mdp.idIcone
                newMdp.dossierPossesseur = mdp.dossierPossesseur
        }
        return newMdp
    }
}
