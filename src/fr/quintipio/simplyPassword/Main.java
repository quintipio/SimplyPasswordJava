package fr.quintipio.simplyPassword;

import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.view.GenereMdpDialogController;
import fr.quintipio.simplyPassword.view.GererMasterPasswordDialogController;
import fr.quintipio.simplyPassword.view.MainViewController;
import fr.quintipio.simplyPassword.view.PasswordEditDialogController;
import fr.quintipio.simplyPassword.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Classe de dÃ©marrage de l'application
 */
public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    
    private Locale langueLocale;


    /**
     * MÃ©hode de lancement
     * @param args les paramÃ¨tres de lancement
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
     * DÃ©marrage de la fenÃªtre principale
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(ContexteStatic.nomAppli);
        this.primaryStage.getIcons().add(new Image("file:/rsc/icon.png"));
        ouvrirFenetre(new Locale("fr","FR"));
    }

    /**
     * Lance la fenÃªtre mÃ¨re
     */
    public void initRootLayout(Locale locale) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle(ContexteStatic.bundle,locale));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMain(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    
    //OUVETURE DE FENETRES
    /**
     * Affiche le contenu de la fenêtre principale
     */
    public void showMainView(Locale locale) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/MainView.fxml"));
            loader.setResources(ResourceBundle.getBundle(ContexteStatic.bundle,locale));
            AnchorPane mainView =  loader.load();

            MainViewController controller = loader.getController();
            controller.setMain(this);
            rootLayout.setCenter(mainView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Affiche la boite de dialogue pour ajouter ou modifier un mot de passe
     * @param mdp le mot de passe à modifier
     */
    public MotDePasse showEditPassword(MotDePasse mdp) {
    	try {
    		FXMLLoader loader = new FXMLLoader();
    		ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,langueLocale);
    		loader.setLocation(Main.class.getResource("view/PasswordEditDialog.fxml"));
            loader.setResources(bundle);
            AnchorPane page = loader.load();
            
            Stage dialogStage = new Stage();
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
    		ex.printStackTrace();
    		return null;
    	}
    }
    
    /**
     * Affiche la boite de dialogue pour modifier le mot de passe maître
     */
    public void showChangeMasterPassword() {
    	try {
    		FXMLLoader loader = new FXMLLoader();
    		ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,langueLocale);
    		loader.setLocation(Main.class.getResource("view/GererMasterPasswordDialog.fxml"));
            loader.setResources(bundle);
            AnchorPane page = loader.load();
            
            Stage dialogStage = new Stage();
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
    		ex.printStackTrace();
    		return ;
    	}
    }
    
    /**
     * Affiche la fenêtre pour générer un mot de passe
     * @return
     */
    public String showGenereMotDePasse(Stage stage) {
    	try {
			FXMLLoader loader = new FXMLLoader();
    		ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle,langueLocale);
    		loader.setLocation(Main.class.getResource("view/GenereMdpDialog.fxml"));
            loader.setResources(bundle);
            AnchorPane page = loader.load();
            
            Stage dialogStage = new Stage();
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
			e.printStackTrace();
			return null;
		}
    }
    
    
    //OUTILS
    
    /**
     * Change la langue de l'application
     * @param locale premier para
     * @param langueB
     */
    public void ouvrirFenetre(Locale locale) {
    	 initRootLayout(locale);
    	 showMainView(locale);
    	 langueLocale = locale;
    }
    
}
