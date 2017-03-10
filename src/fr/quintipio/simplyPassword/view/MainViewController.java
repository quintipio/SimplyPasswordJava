package fr.quintipio.simplyPassword.view;

import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.model.Dossier;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.model.ObservableMotDePasse;
import java.io.File;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;

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
    private TableColumn<ObservableMotDePasse, String> commentColumn;
    @FXML
    private TextField rechercherTextField;
    @FXML
    private CheckBox checkMdpAfficher;
    @FXML
    private ProgressBar countdownProgressbar;
    private ResourceBundle bundle;
    private Main main;
    
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
    
    //pour le timer
    private Timer timer;
    private int nbSecondepasse;

    
    //INIT GETTER SETTER
    
	public void setMain(Main main) {
		this.main = main;
	}
    /**
     * Méthode d'initialisation
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //PasswordBusiness.init();
        bundle = resources;
        genererTreeView();
        startTableMdp();
        afficherOuPasMdp();
        
        collerMenuItem = new MenuItem(resources.getString("coller"));
        collerMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.V,KeyCombination.CONTROL_DOWN));
        collerMenuItem.setOnAction(t -> {
            try {
				if(copie) {
					if(selectedDossier != null) {
						selectedDossier.getValue().getListeMotDePasse().add(selectedMotDePasseToMove);
				        selectedMotDePasseToMove.setDossierPossesseur(selectedDossier.getValue());
				        PasswordBusiness.setModif(true);
				        supprimerCollerElement();
				        ouvrirDossier(selectedDossier.getValue());
					}
				}
				if(coupe) {
					if(selectedDossier != null) {
						 selectedDossier.getValue().getListeMotDePasse().add(selectedMotDePasseToMove);
				         selectedMotDePasseToMove.getDossierPossesseur().getListeMotDePasse().remove(selectedMotDePasseToMove);
				         selectedMotDePasseToMove.setDossierPossesseur(selectedDossier.getValue());
				         PasswordBusiness.setModif(true);
				         supprimerCollerElement();
				         ouvrirDossier(selectedDossier.getValue());
					}
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
    }


    /**
     * Génère l'arbre des dossiers en ajoutant les évènements
     */
    private void genererTreeView() {
        //créer les items
        TreeItem<Dossier> rootItem =genererTreeItem(PasswordBusiness.getDossierMere());
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
        dossierTreeView.setEditable(true);
        dossierTreeView.setOnEditCommit(t -> {
            PasswordBusiness.setModif(true);
        });


        //créer le menu contextuel
        dossierContexteMenu = new ContextMenu();
        
        MenuItem creerDossier = new MenuItem(bundle.getString("creerDossier"));
        creerDossier.setOnAction(t -> {
        	try {
				if(selectedDossier != null) {
					Dossier dossierTmp = new Dossier(bundle.getString("creerDossier"),selectedDossier.getValue());
				    selectedDossier.getValue().getSousDossier().add(dossierTmp);
				    selectedDossier.getChildren().add(new TreeItem<>(dossierTmp));
				    PasswordBusiness.setModif(true);
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem modifierDossier = new MenuItem(bundle.getString("modifierDossier"));
        modifierDossier.setOnAction(t -> {
        	try {
				if(selectedDossier != null) {
					dossierTreeView.edit(selectedDossier);
				    PasswordBusiness.setModif(true);
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem supprimerDossier = new MenuItem(bundle.getString("supprimerDossier"));
        supprimerDossier.setOnAction(t -> {
        	 try {
				if(selectedDossier != null && selectedDossier.getValue().getDossierParent() != null) {
					 Alert al = new Alert(AlertType.CONFIRMATION);
				     al.setTitle(bundle.getString("supprimerDossier"));
				     al.setHeaderText(bundle.getString("supprimerDossier"));
				     al.setContentText(bundle.getString("confirmSupprimerDossier"));
				     Optional<ButtonType> res = al.showAndWait();
				     if(res.get() == ButtonType.OK) {
				    	 selectedDossier.getValue().getDossierParent().getSousDossier().remove(selectedDossier.getValue());
				         TreeItem<Dossier> tmp = selectedDossier.getParent();
				         selectedDossier.getParent().getChildren().remove(selectedDossier);
				         PasswordBusiness.setModif(true);
				         selectedDossier = tmp;
				     }
				 }
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem importerDossier = new MenuItem(bundle.getString("importerDossier"));
        importerDossier.setOnAction(t -> {
        	if(selectedDossier != null) {
            	main.showImportExport(selectedDossier.getValue(), false);
        	}
        	
        });
        MenuItem exporterDossier = new MenuItem(bundle.getString("exporterDossier"));
        exporterDossier.setOnAction(t ->{
        	if(selectedDossier != null) {
        		 main.showImportExport(selectedDossier.getValue(), true);
        	}
        });
        
        dossierContexteMenu.getItems().add(creerDossier);
        dossierContexteMenu.getItems().add(modifierDossier);
        dossierContexteMenu.getItems().add(supprimerDossier);
        dossierContexteMenu.getItems().add(new SeparatorMenuItem());
        dossierContexteMenu.getItems().add(importerDossier);
        dossierContexteMenu.getItems().add(exporterDossier);
        dossierTreeView.setContextMenu(dossierContexteMenu);
        selectedDossier = rootItem;
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
        commentColumn.setCellValueFactory(cellData -> cellData.getValue().commentaireProperty());

        //évènement de sélection
        mdpTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedMotDepasse = newValue;
        });
        mdpTable.setEditable(false);

        //menu contextuel
        mdpContexteMenu = new ContextMenu();
        MenuItem modifMdp = new MenuItem(bundle.getString("modifMdp"));
        modifMdp.setAccelerator(new KeyCodeCombination(KeyCode.M,KeyCombination.CONTROL_DOWN));
        modifMdp.setOnAction(t -> {
        	try {
				if(selectedMotDepasse != null) {
					MotDePasse mdp = main.showEditPassword(selectedMotDepasse.getMdpOri());
					if(mdp != null) {
						mdp.setDossierPossesseur(selectedDossier.getValue());
						selectedDossier.getValue().getListeMotDePasse().remove(selectedMotDepasse.getMdpOri());
						selectedDossier.getValue().getListeMotDePasse().add(mdp);
						ouvrirDossier(selectedDossier.getValue());
				        PasswordBusiness.setModif(true);
					}
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem supMdp = new MenuItem(bundle.getString("supprimerMdp"));
        supMdp.setOnAction(t -> {
        	try {
				if(selectedMotDepasse != null) {
					Alert al = new Alert(AlertType.CONFIRMATION);
				    al.setTitle(bundle.getString("supprimerMdp"));
				    al.setHeaderText(bundle.getString("supprimerMdp"));
				    al.setContentText(bundle.getString("confirmSupprimerMdp"));
				    Optional<ButtonType> res = al.showAndWait();
				    if(res.get() == ButtonType.OK) {
				    	selectedDossier.getValue().getListeMotDePasse().remove(selectedMotDepasse.getMdpOri());
				        ouvrirDossier(selectedDossier.getValue());
				        PasswordBusiness.setModif(true);
				    }
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem shareMdp = new MenuItem(bundle.getString("partage"));
        shareMdp.setAccelerator(new KeyCodeCombination(KeyCode.P,KeyCombination.CONTROL_DOWN));
        shareMdp.setOnAction(t -> {
        	try {
				if(selectedMotDepasse != null) {
				        try {
				            FileChooser fileChooser = new FileChooser();
				            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extensionPartage.toUpperCase()+" (*"+ ContexteStatic.extensionPartage+")", "*"+ContexteStatic.extensionPartage));
				            File file = fileChooser.showSaveDialog(main.getPrimaryStage());
				            if(file != null) {
				                    byte[] data = PasswordBusiness.genererPartage(selectedMotDepasse.getMdpOri());
				                    ComFile fichier = new ComFile(file.getPath());
				                    fichier.writeFile(data,true);
				        }
				        }catch(Exception ex) {
				            ex.printStackTrace();
				            Alert alert = new Alert(AlertType.ERROR);
				            alert.initOwner(main.getPrimaryStage());
				            alert.setTitle(bundle.getString("erreur"));
				            alert.setHeaderText(bundle.getString("erreur"));
				            alert.setContentText(bundle.getString("erreurPartage"));
				            alert.showAndWait();
				        }
				        
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem copieLogin = new MenuItem(bundle.getString("copieLogin"));
        copieLogin.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN,KeyCombination.SHIFT_DOWN));
        copieLogin.setOnAction(t -> {
        	try {
				if(selectedMotDepasse != null) {
					copyToClipBoard(selectedMotDepasse.getLogin());
					startTimer();
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem copieMdp = new MenuItem(bundle.getString("copieMdp"));
        copieMdp.setAccelerator(new KeyCodeCombination(KeyCode.C,KeyCombination.CONTROL_DOWN,KeyCombination.SHIFT_DOWN));
        copieMdp.setOnAction(t -> {
        	try {
				if(selectedMotDepasse != null) {
					copyToClipBoard(selectedMotDepasse.getMdpOri().getMotDePasseObjet());
					startTimer();
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem copier = new MenuItem(bundle.getString("copier"));
        copier.setAccelerator(new KeyCodeCombination(KeyCode.C,KeyCombination.CONTROL_DOWN));
        copier.setOnAction(t -> {
        	try {
				if(selectedMotDepasse != null) {
					selectedMotDePasseToMove = selectedMotDepasse.getMdpOri();
				    copie = true;
				    coupe = false;
				    ajouterCollerElement();
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        
        MenuItem couper = new MenuItem(bundle.getString("couper"));
        couper.setAccelerator(new KeyCodeCombination(KeyCode.X,KeyCombination.CONTROL_DOWN));
        couper.setOnAction(t -> {
        	try {
				if(selectedMotDepasse != null) {
					selectedMotDePasseToMove = selectedMotDepasse.getMdpOri();
				    copie = false;
				    coupe = true;
				    ajouterCollerElement();
				}
			} catch (Exception e) {
				Main.showError(e);
			}
        });
        mdpContexteMenu.getItems().add(copieLogin);
        mdpContexteMenu.getItems().add(copieMdp);
        mdpContexteMenu.getItems().add(new SeparatorMenuItem());
        mdpContexteMenu.getItems().add(modifMdp);
        mdpContexteMenu.getItems().add(supMdp);
        mdpContexteMenu.getItems().add(new SeparatorMenuItem());
        mdpContexteMenu.getItems().add(copier);
        mdpContexteMenu.getItems().add(couper);
        mdpContexteMenu.getItems().add(new SeparatorMenuItem());
        mdpContexteMenu.getItems().add(shareMdp);
        mdpTable.setContextMenu(mdpContexteMenu);

        mdpTable.setItems(listeMdp);
    }

    
    
    
    //OUVRIR MDP ET DOSSIER
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
    
    
    //COPIER COLLER
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
    
    
    
    
    //PRESSE PAPIER
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
     * Démarre un timer de compte à rebours, fait diminuer la progress bar, et efface les données du presse-papier au bout d'un certain temps
     */
    private void startTimer() {
    	try {
			if(timer != null) {
				timer.cancel();
			}
			countdownProgressbar.setProgress(1);
			nbSecondepasse = 0;
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						if(nbSecondepasse < ContexteStatic.dureeTimerCopieClipboard) {
							nbSecondepasse++;
							double recul = (double)1/(double)ContexteStatic.dureeTimerCopieClipboard;
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									try {
										countdownProgressbar.setProgress(countdownProgressbar.getProgress()-recul);
									}
									catch(Exception e) {
										
									}
								}
							});
						}
						else {
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										try {
											copyToClipBoard("");
											countdownProgressbar.setProgress(1);
										}
										catch(Exception e) {
											
										}
									}
								});
								this.cancel();
								timer.cancel();
								timer.purge();
							
						}
					} catch (Exception e) {
					}
				}
			}, 0,1000);
		} catch (Exception e) {
		}
    }
    
    
    
    
    
    //METHODE FXML
    /**
     * Lance une recherche de mots de passe
     */
    @FXML
    private void rechercher() {
        try {
			if(rechercherTextField.getText() != null && rechercherTextField.getText().length() > 0) {
			    ouvrirMotsDePasse(PasswordBusiness.recherche(rechercherTextField.getText(),PasswordBusiness.getDossierMere()));
			}
			else {
			    ouvrirDossier(selectedDossier.getValue());
			}
		} catch (Exception e) {
			Main.showError(e);
		}
    }
    
    /**
     * Charge la boite de dialogue pour un nouveau mot de passe
     */
    @FXML
    private void nouveauMdp() {
    	MotDePasse mdp = main.showEditPassword(null);
    	if(mdp != null) {
    		mdp.setDossierPossesseur(selectedDossier.getValue());
    		selectedDossier.getValue().getListeMotDePasse().add(mdp);
    		ouvrirDossier(selectedDossier.getValue());
            PasswordBusiness.setModif(true);
    	}
    }

    /**
     * Affiche ou masque les mots de passe du passe tableau
     */
    @FXML
    private void afficherOuPasMdp() {
        try {
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
		} catch (Exception e) {
			Main.showError(e);
		}
    }
    
    
    
    //OUTILS
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
}
