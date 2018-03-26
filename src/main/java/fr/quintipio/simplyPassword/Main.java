package fr.quintipio.simplyPassword;

import fr.quintipio.simplyPassword.business.ParamBusiness;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.model.Dossier;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.util.CryptUtils.InvalidPasswordException;
import fr.quintipio.simplyPassword.view.FileCryptDialogController;
import fr.quintipio.simplyPassword.view.GenereMdpDialogController;
import fr.quintipio.simplyPassword.view.GererMasterPasswordDialogController;
import fr.quintipio.simplyPassword.view.ImportExportDialogController;
import fr.quintipio.simplyPassword.view.MainViewController;
import fr.quintipio.simplyPassword.view.PasswordEditDialogController;
import fr.quintipio.simplyPassword.view.RootLayoutController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


/**
 * Classe de démarrage de l'application
 */
public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;


    /**
     * Méhode de lancement
     * @param args les paramètres de lancement
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Getter de l'appli de base
     * @return l'appli de base
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Démarrage de la fenêtre principale
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(ContexteStatic.nomAppli);
        this.primaryStage.getIcons().add(new Image("/rsc/icon.png"));
        
        
        //chargement de la configuration
        if(!ParamBusiness.isModeLive()) {
        	ParamBusiness.getDonneesParamUser();
        }
        
        //évènement de fermeture
        this.primaryStage.setOnCloseRequest(t -> {
        	if(!askSave()) {
        		t.consume();
        	}
        });
        
        //démarrage de la fenêtre
        ouvrirFenetre(PasswordBusiness.isFichier());
    }

    /**
     * Lance la fenêtre mère
     * @param askPassword boolean pour ouvrir la fenetre de mot de passe pour déchiffrer un fichier (uniquement à true au démarrage)
     */
    public void initRootLayout(boolean askPassword) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RootLayout.fxml"),bundle);
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMain(this);

            //affiche la dlg des mots de passe, s'il faut ouvrir un fichier
            if(askPassword) {
            	boolean erreurMdp = false;
            	boolean getError= false;
            	do {
            		erreurMdp = false;
            		Dialog<String> dlg = new Dialog<>();
                	dlg.setTitle(ContexteStatic.nomAppli);
                	dlg.setHeaderText(PasswordBusiness.isFichier()?PasswordBusiness.getFichier().getFile().getAbsolutePath():ContexteStatic.nomAppli);
                	ImageView img = new ImageView("/rsc/key.png");
                	img.resize(64, 64);
                	dlg.setGraphic(img);
                	
                	
                	Label la = new Label();
                	la.setText(bundle.getString("entrezmdp"));
                	Label lb = new Label();
                	lb.setText(bundle.getString("erreurDechiffre"));
                	lb.setTextFill(Color.RED);
                	PasswordField field = new PasswordField();

                	GridPane grid = new GridPane();
                	grid.add(la,0,0);
                	grid.add(field, 1,0);
                	if(getError) {
                		grid.add(lb,0,1,2,1);
                	}
                	dlg.getDialogPane().setContent(grid);
                	
                	ButtonType okButton = new ButtonType("OK",ButtonData.OK_DONE);
                	dlg.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
                	Node validButton = dlg.getDialogPane().lookupButton(okButton);
                	validButton.setDisable(true);
                	
                	field.textProperty().addListener((observable,oldValue,newValue) -> validButton.setDisable(newValue.trim().isEmpty() || newValue.length() < 8));
                	Platform.runLater(field::requestFocus);
                	
                	dlg.setResultConverter(dlgButton -> {
                		if(dlgButton == okButton) {
                			return field.getText();
                		}
                		return null;
                	});
                	
                	Optional<String> mdp = dlg.showAndWait();
                	
                	if(mdp.isPresent()) {
                		try {
                    		PasswordBusiness.load(PasswordBusiness.getFichier().getFile().getPath(),mdp.get());
                		}
                		catch(InvalidPasswordException ex) {
                			erreurMdp = true;
                			getError = true;
                		}
                	}
                        else {
                            PasswordBusiness.setFichier(null, false);
                        }
            	}while(erreurMdp);
            }
            
            if(PasswordBusiness.getDossierMere() == null) {
            	PasswordBusiness.setDossierMere(new Dossier(bundle.getString("racine"),null));
            }
            
            primaryStage.show();
        } catch (Exception e) {
        	Main.showError(e);
        }
    }

    
    
    //OUVETURE DE FENETRES
    /**
     * Affiche le contenu de la fenêtre principale
     */
    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainView.fxml"),ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase())));
            AnchorPane mainView =  loader.load();

            MainViewController controller = loader.getController();
            controller.setMain(this);
            rootLayout.setCenter(mainView);
        } catch (IOException e) {
            Main.showError(e);
        }
    }
    
    /**
     * Affiche la boite de dialogue pour ajouter ou modifier un mot de passe
     * @param mdp le mot de passe à modifier sinon null
     */
    public MotDePasse showEditPassword(MotDePasse mdp) {
    	try {
            ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
    		FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/PasswordEditDialog.fxml"),bundle);
            AnchorPane page = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image("/rsc/icon.png"));
            dialogStage.setTitle((mdp == null)?bundle.getString("creerMdp"):bundle.getString("modifMdp"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            PasswordEditDialogController controller = loader.getController();
            controller.setMain(this);
            controller.setDialogStage(dialogStage);
            controller.setMotdePasse(mdp);
            
            dialogStage.showAndWait();
            
    		return controller.getMotdePasse();
    	}
    	catch(Exception ex) {
            Main.showError(ex);
    		return null;
    	}
    }
    
    /**
     * Affiche la boite de dialogue pour modifier le mot de passe maître
     */
    public void showChangeMasterPassword() {
    	try {
    	    ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/GererMasterPasswordDialog.fxml"),bundle);
            AnchorPane page = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image("/rsc/icon.png"));
            dialogStage.setTitle(bundle.getString("changerMdpMaitre"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            GererMasterPasswordDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            
            dialogStage.showAndWait();
            
    		return ;
    	}
    	catch(Exception ex) {
            Main.showError(ex);
    		return ;
    	}
    }
    
    /**
     * Affiche la boite de dialogue pour l'import/export
     * @param dossier le dossier dans lequel s'effectue l'import / export
     * @param export true si export sinon import
     */
    public void showImportExport(Dossier dossier, boolean export) {
    	try {
    	    ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/ImportExportDialog.fxml"),bundle);
            AnchorPane page = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image("/rsc/icon.png"));
            dialogStage.setTitle(bundle.getString(export?"export":"import"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            ImportExportDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setDossierSelected(dossier);
            controller.setExport(export);
            controller.init();
                        
            dialogStage.showAndWait();
            
            if(!export) {
            	if(controller.isOk()) {
                	ouvrirFenetre(false);
            	}
            }
            
    		return ;
    	}
    	catch(Exception ex) {
            Main.showError(ex);
    		return ;
    	}
    }
    
    /**
     * Affiche la boite de dialogue pour chiffrer/déchiffrer un fichier
     * @param cryptFile true si la dlg doit être en mode chiffrement de fichier sinon false
     */
    public void showCryptFile(boolean cryptFile) {
    	try {
    	    ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/FileCryptDialog.fxml"),bundle);
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image("/rsc/icon.png"));
            dialogStage.setTitle(bundle.getString(cryptFile?"cryptFichier":"decryptFichier"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            FileCryptDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.init(cryptFile);
            dialogStage.showAndWait();
    	}
    	catch(Exception ex) {
            Main.showError(ex);
    	}
    }
    
    /**
     * Affiche la fenêtre pour générer un mot de passe
     * @return
     */
    public String showGenereMotDePasse(Stage stage) {
    	try {
    	    ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/GenereMdpDialog.fxml"),bundle);
            AnchorPane page = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image("/rsc/icon.png"));
            dialogStage.setTitle(bundle.getString("genereMdp"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            GenereMdpDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);


            dialogStage.showAndWait();
            return controller.getMotdePasse();
            
		}
		catch(Exception e) {
            Main.showError(e);
			return null;
		}
    }
    
    
    //OUTILS
    
    /**
     * Change la langue de l'application
     * @param askPassword indique s'il faut demander le mot de passe à l'ouverture
     */
    public void ouvrirFenetre(boolean askPassword) {
    	 initRootLayout(askPassword);
    	 showMainView();
    }
    
    /**
     * Demande de confirmation de sauvegarde
     * @return true si fermeture autorisée sinon false
     */
    public boolean askSave() {
    	if(PasswordBusiness.isModif()) {

            ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle(bundle.getString("askConfirm"));
    		alert.setHeaderText(bundle.getString("askConfirm"));
    		alert.setContentText(bundle.getString("askSave"));
    		alert.getButtonTypes().setAll(ButtonType.YES,ButtonType.NO,ButtonType.CANCEL);
    		
    		Optional<ButtonType> result = alert.showAndWait();
    		
    		if(result.get() == ButtonType.YES) {
    			return save();
    		}

			if(result.get() == ButtonType.CANCEL) {
				return false;
			}
		}
		return true;
    }
    
    /**
     * Sauvegarde les données de l'appli
     * @return true si sauvegardée
     */
    public boolean save() {
    	if(!PasswordBusiness.isMotDePasse()) {
    		showChangeMasterPassword();
    	}

	    if(PasswordBusiness.isMotDePasse() && !PasswordBusiness.isFichier()) {
	        //ouverture d'une dlg de fichier
	        FileChooser fileChooser = new FileChooser();
	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(ContexteStatic.extension.toUpperCase()+" (*"+ ContexteStatic.extension+")", "*"+ContexteStatic.extension));
	        File file = fileChooser.showSaveDialog(primaryStage);
	        if(file != null) {
	            if (file.getPath().endsWith(ContexteStatic.extension)) {
	                PasswordBusiness.setFichier(file.getPath(),true);
	            }
	            else {
	                PasswordBusiness.setFichier(file.getPath()+ContexteStatic.extension,true);
	            }
	        }
	    }
	
	    if(PasswordBusiness.isFichier() && PasswordBusiness.isMotDePasse()) {
	        try {
	            PasswordBusiness.save();
	            return true;
	        }catch(Exception ex) {
	        		ex.printStackTrace();
	        }
	    }
	    return false;
    }
    
    /**
     * Affiche un message d'erreur avec une exception dans une dialog
     * @param ex l'exception
     */
    public static void showError(Exception ex) {
    	ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,new Locale(ParamBusiness.getParametreLangue().toLowerCase(),ParamBusiness.getParametreLangue().toUpperCase()));
    	Alert alert = new Alert(AlertType.ERROR);
    	alert.setTitle(bundle.getString("exception"));
    	alert.setHeaderText(bundle.getString("exception"));
    	
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	ex.printStackTrace(pw);
    	String exceptionText = sw.toString();
    	TextArea textArea = new TextArea(exceptionText);
    	textArea.setEditable(false);
    	textArea.setWrapText(true);
    	textArea.setMaxWidth(Double.MAX_VALUE);
    	textArea.setMaxHeight(Double.MAX_VALUE);
    	
    	GridPane.setVgrow(textArea, Priority.ALWAYS);
    	GridPane.setHgrow(textArea, Priority.ALWAYS);
    	
    	GridPane expContent = new GridPane();
    	expContent.setMaxWidth(Double.MAX_VALUE);
    	expContent.add(textArea, 0, 1);
    	
    	alert.getDialogPane().setExpandableContent(expContent);
    	alert.showAndWait();
     }
    
}
