package fr.quintipio.simplyPassword.view;

import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.model.Dossier;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.model.ObservableMotDePasse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainViewController implements Initializable {

    //Elements FXML
    @FXML
    private TreeView<Dossier> dossierTreeView;
    @FXML
    private TableView<ObservableMotDePasse> mdpTable;
    @FXML
    private TableColumn<ObservableMotDePasse, String> titreColumn;
    @FXML
    private TableColumn<ObservableMotDePasse, String> loginColumn;
    @FXML
    private TableColumn<ObservableMotDePasse, String> mdpColumn;
    @FXML
    private TableColumn<ObservableMotDePasse, String> webColumn;
    @FXML
    private TextField rechercherTextField;
    @FXML
    private CheckBox checkMdpAfficher;
    @FXML
    private ProgressBar countdownProgressbar;
    private ResourceBundle bundle;
    private ObservableList<ObservableMotDePasse> listeMdp = FXCollections.observableArrayList();

    //ContexteMenu
    private ContextMenu dossierContexteMenu;
    private ContextMenu mdpContexteMenu;

    //Elements de mise en mémoire des sélections
    private TreeItem<Dossier> selectedDossier;
    private ObservableMotDePasse selectedMotDepasse;

    ///Pour le déplacement des objets
    private MotDePasse selectedMotDePasseToMove;
    private boolean coupe;
    private boolean copie;
    private MenuItem collerMenuItem;

    /**
    Constructeur
     */
    public MainViewController() {
        PasswordBusiness.init();
    }


    /**
     * Méthode d'initialisation
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bundle = resources;
        genererTreeView();
        startTableMdp();
        afficherOuPasMdp();

        collerMenuItem = new MenuItem(resources.getString("coller"));
        collerMenuItem.setOnAction(t -> {
            if(copie) {
                selectedDossier.getValue().getListeMotDePasse().add(selectedMotDePasseToMove);
                selectedMotDePasseToMove.setDossierPossesseur(selectedDossier.getValue());
                PasswordBusiness.setModif(true);
                supprimerCollerElement();
                ouvrirDossier(selectedDossier.getValue());
            }
            if(coupe) {
                selectedDossier.getValue().getListeMotDePasse().add(selectedMotDePasseToMove);
                selectedMotDePasseToMove.getDossierPossesseur().getListeMotDePasse().remove(selectedMotDePasseToMove);
                selectedMotDePasseToMove.setDossierPossesseur(selectedDossier.getValue());
                PasswordBusiness.setModif(true);
                supprimerCollerElement();
                ouvrirDossier(selectedDossier.getValue());
            }
        });
    }


    /**
     * Génère l'arbre des dossiers en ajoutant les évènements
     */
    private void genererTreeView() {
        //créer les items
        TreeItem<Dossier> rootItem = genererTreeItem(PasswordBusiness.getDossierMere());
        dossierTreeView.setRoot(rootItem);
        dossierTreeView.setEditable(true);
        //converter pour la l'édtion d'un dossier
        dossierTreeView.setCellFactory(p -> new TextFieldTreeCell<>(new StringConverter<Dossier>(){

            @Override
            public String toString(Dossier object) {
                return object.toString();
            }

            @Override
            public Dossier fromString(String string) {
                selectedDossier.getValue().setTitre(string);
                return selectedDossier.getValue();
            }
        }));
        dossierTreeView.refresh();

        //créer l'évènement de sélection
        dossierTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedDossier = newValue;
            ouvrirDossier(selectedDossier.getValue());
        });


        //créer le menu contextuel
        dossierContexteMenu = new ContextMenu();
        MenuItem creerDossier = new MenuItem(bundle.getString("creerDossier"));
        creerDossier.setOnAction(t -> {
            Dossier dossierTmp = new Dossier(bundle.getString("creerDossier"),selectedDossier.getValue());
            selectedDossier.getValue().getSousDossier().add(dossierTmp);
            selectedDossier.getChildren().add(new TreeItem<>(dossierTmp));
            PasswordBusiness.setModif(true);
        });
        MenuItem modifierDossier = new MenuItem(bundle.getString("modifierDossier"));
        modifierDossier.setOnAction(t -> {
            dossierTreeView.edit(selectedDossier);
            PasswordBusiness.setModif(true);
        });
        MenuItem supprimerDossier = new MenuItem(bundle.getString("supprimerDossier"));
        supprimerDossier.setOnAction(t -> {
            selectedDossier.getValue().getDossierParent().getSousDossier().remove(selectedDossier.getValue());
            TreeItem tmp = selectedDossier.getParent();
            selectedDossier.getParent().getChildren().remove(selectedDossier);
            PasswordBusiness.setModif(true);
            selectedDossier = tmp;
        });
        MenuItem importerDossier = new MenuItem(bundle.getString("importerDossier"));
        MenuItem exporterDossier = new MenuItem(bundle.getString("exporterDossier"));
        dossierContexteMenu.getItems().add(creerDossier);
        dossierContexteMenu.getItems().add(modifierDossier);
        dossierContexteMenu.getItems().add(supprimerDossier);
        dossierContexteMenu.getItems().add(importerDossier);
        dossierContexteMenu.getItems().add(exporterDossier);
        dossierTreeView.setContextMenu(dossierContexteMenu);
        selectedDossier = rootItem;
    }

    /**
     * Génère un arbre des items de dossier
     * @param dossier le dossier dont on veut le tree item
     * @return le tree item
     */
    private TreeItem<Dossier> genererTreeItem(Dossier dossier) {

        TreeItem<Dossier> dossierTreeItem = new TreeItem<>(dossier);
        dossierTreeItem.setExpanded(true);
        if(dossier.getSousDossier() != null && dossier.getSousDossier().size() > 0) {
            for (Dossier sousDossier : dossier.getSousDossier()) {
                TreeItem<Dossier> item = genererTreeItem(sousDossier);
                dossierTreeItem.getChildren().add(item);
            }
        }
        return  dossierTreeItem;
    }

    /**
     * Ouvre un dossier et affiche les mots de passe
     * @param dossier le dossier à ouvrir
     */
    private void ouvrirDossier(Dossier dossier) {
        ouvrirMotsDePasse(dossier.getListeMotDePasse());
    }

    /**
     * Ouvre une liste de mot de passe dans le tableau
     * @param listeMdp la liste des mots de passe à afficher
     */
    private void ouvrirMotsDePasse(List<MotDePasse> listeMdp) {
        this.listeMdp.clear();
        this.listeMdp.addAll(listeMdp.stream().map(ObservableMotDePasse::new).collect(Collectors.toList()));
        afficherOuPasMdp();
    }

    /**
     * Démarre la table d'affichage des mots de passe avec les évènements
     */
    private void startTableMdp() {
        //mise en place des colonnes
        titreColumn.setCellValueFactory(cellData -> cellData.getValue().titreProperty());
        loginColumn.setCellValueFactory(cellData -> cellData.getValue().loginProperty());
        mdpColumn.setCellValueFactory(cellData -> cellData.getValue().motDePasseObjetProperty());
        webColumn.setCellValueFactory(cellData -> cellData.getValue().siteWebProperty());

        //évènement de sélection
        mdpTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedMotDepasse = newValue;
        });
        mdpTable.setEditable(false);

        //menu contextuel
        mdpContexteMenu = new ContextMenu();
        MenuItem modifMdp = new MenuItem(bundle.getString("modifMdp"));
        MenuItem supMdp = new MenuItem(bundle.getString("supprimerMdp"));
        supMdp.setOnAction(t -> {
            selectedDossier.getValue().getListeMotDePasse().remove(selectedMotDepasse.getMdpOri());
            ouvrirDossier(selectedDossier.getValue());
            PasswordBusiness.setModif(true);
        });
        MenuItem copieLogin = new MenuItem(bundle.getString("copieLogin"));
        copieLogin.setOnAction(t -> copyToClipBoard(selectedMotDepasse.getLogin()));
        MenuItem copieMdp = new MenuItem(bundle.getString("copieMdp"));
        copieMdp.setOnAction(t -> copyToClipBoard(selectedMotDepasse.getMdpOri().getMotDePasseObjet()));
        MenuItem copier = new MenuItem(bundle.getString("copier"));
        copier.setOnAction(t -> {
            selectedMotDePasseToMove = selectedMotDepasse.getMdpOri();
            copie = true;
            coupe = false;
            ajouterCollerElement();

        });
        MenuItem couper = new MenuItem(bundle.getString("couper"));
        couper.setOnAction(t -> {
            selectedMotDePasseToMove = selectedMotDepasse.getMdpOri();
            copie = false;
            coupe = true;
            ajouterCollerElement();

        });
        mdpContexteMenu.getItems().add(copieLogin);
        mdpContexteMenu.getItems().add(copieMdp);
        mdpContexteMenu.getItems().add(modifMdp);
        mdpContexteMenu.getItems().add(supMdp);
        mdpContexteMenu.getItems().add(copier);
        mdpContexteMenu.getItems().add(couper);
        mdpTable.setContextMenu(mdpContexteMenu);

        mdpTable.setItems(listeMdp);
    }

    /**
     * Ajoute le menu item coller dans les contexteMennu pour coller un mot de passe
     */
    private void ajouterCollerElement() {
        dossierContexteMenu.getItems().add(collerMenuItem);
        mdpContexteMenu.getItems().add(collerMenuItem);
    }

    /**
     * Supprime le menu item coller dans les contexteMennu
     */
    private void supprimerCollerElement() {
        dossierContexteMenu.getItems().remove(collerMenuItem);
        mdpContexteMenu.getItems().remove(collerMenuItem);
    }

    /**
     * Copie du texte dans le presse papier
     * @param data les données à copier
     */
    private void copyToClipBoard(String data) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);
    }

    /**
     * Lance une recherche de mots de passe
     */
    @FXML
    private void rechercher() {
        if(rechercherTextField.getText() != null && rechercherTextField.getText().length() > 0) {
            ouvrirMotsDePasse(PasswordBusiness.recherche(rechercherTextField.getText(),PasswordBusiness.getDossierMere()));
        }
        else {
            ouvrirDossier(selectedDossier.getValue());
        }
    }

    @FXML
    private void afficherOuPasMdp() {
        if(checkMdpAfficher.isSelected()) {
            for (ObservableMotDePasse mdp :
                    listeMdp) {
                mdp.setMotDePasseObjet(mdp.getMdpOri().getMotDePasseObjet());
            }
        }
        else {
            for (ObservableMotDePasse mdp :
                    listeMdp) {
                mdp.setMotDePasseObjet("********");
            }
        }
    }
}
