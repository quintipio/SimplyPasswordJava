package fr.quintipio.simplyPassword;

import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.view.MainViewController;
import fr.quintipio.simplyPassword.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
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
     * Getter de la page
     * @return la page
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
        this.primaryStage.setTitle("Simply Password");
        this.primaryStage.getIcons().add(new Image("file:/rsc/icone.png"));
        initRootLayout();
        showMainView();
    }

    /**
     * Lance la fenêtre mère
     */
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            loader.setResources(ResourceBundle.getBundle(ContexteStatic.bundle));
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

    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/MainView.fxml"));
            loader.setResources(ResourceBundle.getBundle(ContexteStatic.bundle));
            AnchorPane mainView =  loader.load();

            MainViewController controller = loader.getController();
            rootLayout.setCenter(mainView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
