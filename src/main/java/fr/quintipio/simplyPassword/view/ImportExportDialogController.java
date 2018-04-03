package fr.quintipio.simplyPassword.view;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import fr.quintipio.simplyPassword.Main;
import fr.quintipio.simplyPassword.business.PasswordBusiness;
import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.model.Dossier;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.util.CryptUtils;
import fr.quintipio.simplyPassword.util.CryptUtils.InvalidPasswordException;
import fr.quintipio.simplyPassword.util.StringUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ImportExportDialogController  implements Initializable  {

	///ELEMENT FXML
	@FXML
	private ComboBox<String> formatCombo;
	@FXML
	private TextField fichierText;
	@FXML
	private CheckBox checkRemplace;
	@FXML
	private Button validButton;
	@FXML
	private Button parcourirButton;

	///ELEMENT DU CONTROLEUR
	private Dossier dossierSelected;
	private boolean export;
	private ObservableList<String> listeFormat;
	private String extensionSelected;
	private boolean ok;

	private ResourceBundle bundle;
    private Stage dialogStage;
	
    
    ///INIT GETTER SETTER

    /**
     * Démarrage de la vue
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ok =false;
		listeFormat = FXCollections.observableArrayList("","CSV","XML",ContexteStatic.extensionExport.subSequence(1,ContexteStatic.extensionExport.length()).toString().toUpperCase());
		bundle = resources;
		formatCombo.getItems().addAll(listeFormat);
		validButton.setDisable(true);
		extensionSelected = "all";
	}
	
	/**
	 * A éxécuter une fois le controleur chargé et initialisé
	 */
	public void init() {
		if(!export) {
			formatCombo.setDisable(true);
		}else {
			parcourirButton.setDisable(true);
			checkRemplace.setDisable(true);
		}
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void setDossierSelected(Dossier dossierSelected) {
		this.dossierSelected = dossierSelected;
	}

	public void setExport(boolean export) {
		this.export = export;
	}
	
	public boolean isOk() {
		return ok;
	}
	
	///CHOIX POUR IMPORT EXPORT
	/**
	 * Evènement lors du choix d'un format
	 */
	@FXML
	private void selectChoix() {
		extensionSelected = !StringUtils.stringEmpty(formatCombo.getSelectionModel().getSelectedItem())?"."+formatCombo.getSelectionModel().getSelectedItem():"";
		if(export) {
			parcourirButton.setDisable(StringUtils.stringEmpty(extensionSelected));
			fichierText.setText("");
		}
		checkButtonEnable();
	}
	
	/**
	 * Ouvre un fichier pour un import ou un export
	 */
	@FXML
	private void openFile() {
		try {
			if(export) {
			    FileChooser fileChooser = new FileChooser();
			    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*"+extensionSelected, "*"+extensionSelected));
			    File file = fileChooser.showSaveDialog(dialogStage);
			    if(file != null) {
			    	if(file.getPath().toLowerCase().endsWith("."+listeFormat.get(1).toLowerCase()) || file.getPath().toLowerCase().endsWith("."+listeFormat.get(2).toLowerCase()) || file.getPath().toLowerCase().endsWith("."+listeFormat.get(3).toLowerCase())) {
			    		fichierText.setText(file.getPath());
			    	}
			    	else {
			    		fichierText.setText(file.getPath()+"."+extensionSelected);
			    	}
			    }
			}
			else {
			    FileChooser fileChooser = new FileChooser();
			    for (String string : listeFormat) {
					if(!StringUtils.stringEmpty(string)) {
			    		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*."+string, "*."+string));
					}
				}
			    File file = fileChooser.showOpenDialog(dialogStage);
			    if(file != null) {
			    	if(file.getPath().toLowerCase().endsWith("."+listeFormat.get(1).toLowerCase()) || file.getPath().toLowerCase().endsWith("."+listeFormat.get(2).toLowerCase()) ||file.getPath().toLowerCase().endsWith("."+listeFormat.get(3).toLowerCase())) {
			    		fichierText.setText(file.getPath());
			    		String extension = file.getPath().substring(file.getPath().length()-3);
			    		int index = listeFormat.indexOf(extension.toUpperCase());
			    		formatCombo.getSelectionModel().select(index);
			    	}
					else {
						Alert alert = new Alert(AlertType.ERROR);
			            alert.initOwner(dialogStage);
			            alert.setTitle(bundle.getString("erreur"));
			            alert.setHeaderText(bundle.getString("erreur"));
			            alert.setContentText(bundle.getString("formatNonPris"));
			            alert.showAndWait();
					}
			    }
			}
		} catch (Exception e) {
			Main.showError(e);
		}
		checkButtonEnable();
	}
	
	
	///VALIDATION
	/**
	 * Lance l'inport ou l'export
	 */
	@FXML
	private void valider() {
		try {
			ComFile file = new ComFile(fichierText.getText());
			boolean ok = false;
			//export
			if(export) {
				byte[] data = null;
				
				//csv
				if(extensionSelected.toUpperCase().contentEquals("."+listeFormat.get(1).toUpperCase())) {
					data = exportCsv(dossierSelected);
					ok =true;
				}
				
				//xml
				if(extensionSelected.toUpperCase().contentEquals("."+listeFormat.get(2).toUpperCase())) {
					data = exportXml(dossierSelected);
					ok =true;
				}
				
				//spj
				if(extensionSelected.toUpperCase().contentEquals("."+listeFormat.get(3).toUpperCase())) {
					String mdp = askPassword();
					if(!StringUtils.stringEmpty(mdp)) {
						data = exportSpe(dossierSelected, mdp);
						ok =true;
					}
				}
				
				//export
				file.writeFile(data,true);
			}
			//import
			else {
				byte[] data = file.readFileToByteArray();
				Dossier dossier = null;
				
				//csv
				if(extensionSelected.toUpperCase().contentEquals("."+listeFormat.get(1).toUpperCase())) {
					dossier = importCsv(data);
					ok =true;
				}
				
				//xml
				if(extensionSelected.toUpperCase().contentEquals("."+listeFormat.get(2).toUpperCase())) {
					dossier = importXml(data);
					ok =true;
				}
				
				//spj
				if(extensionSelected.toUpperCase().contentEquals("."+listeFormat.get(3).toUpperCase())) {
					String mdp = askPassword();
					if(!StringUtils.stringEmpty(mdp)) {
						dossier = importSpe(data, mdp);
						ok =true;
					}
				}
				
				//import
				if(checkRemplace.isSelected()) {
					dossierSelected.setTitre(dossier.getTitre());
					dossierSelected.setListeMotDePasse(dossier.getListeMotDePasse());
					dossierSelected.setSousDossier(dossier.getSousDossier());
				}
				else {
					dossier.setDossierParent(dossierSelected);
                                        if(dossierSelected.getSousDossier() == null) {
                                            dossierSelected.setSousDossier(new ArrayList<>());
                                        }
					dossierSelected.getSousDossier().add(dossier);
				}
				PasswordBusiness.setModif(true);
			}
			
			if(ok) {
				this.ok = true;
				Alert alert = new Alert(AlertType.INFORMATION);
	            alert.initOwner(dialogStage);
	            alert.setTitle(bundle.getString((export)?"okExp":"okImp"));
	            alert.setHeaderText(bundle.getString((export)?"okExp":"okImp"));
	            alert.setContentText(bundle.getString((export)?"okExp":"okImp"));
	            alert.showAndWait();
			}
			
			dialogStage.close();
		}
		catch(InvalidPasswordException e) {
			Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle(bundle.getString("erreur"));
            alert.setHeaderText(bundle.getString("erreur"));
            alert.setContentText(bundle.getString("erreurMdp"));
            alert.showAndWait();
		}
		catch(Exception ex) {
			Main.showError(ex);
		}
		
	}
	
	/**
	 * Ferme la fenetre
	 */
	@FXML
	private void annuler() {
		dialogStage.close();
	}

	
	///OUTILS
	/**
	 * Vérifie si le bouton valider est disponible
	 */
	private void checkButtonEnable() {
		validButton.setDisable(StringUtils.stringEmpty(formatCombo.getSelectionModel().getSelectedItem()) && StringUtils.stringEmpty(fichierText.getText().trim()));
	}
	
	
	///IMPORT OU EXPORT
	
	//CSV
	/**
	 * Converti les données d'un csv en Dossier
	 * @param data les données
	 * @return le dossier
	 */
	public Dossier importCsv(byte[] data) {
		Dossier dossierImport = new Dossier();
		dossierImport.setTitre(bundle.getString("import"));
		dossierImport.setListeMotDePasse(new ArrayList<>());
		dossierImport.setSousDossier(new ArrayList<>());
		
		String toRead = new String(data);
		String[] byLine = toRead.split(";");
		for (String string : byLine) {
			String stringTrim = string.substring(1,string.length()-1);
			String[] byElement = stringTrim.split("\",\"");
			
			MotDePasse mdp = new MotDePasse();
			mdp.setDossierPossesseur(dossierImport);
			mdp.setTitre(byElement[0]);
			mdp.setLogin(byElement[1]);
			mdp.setMotDePasseObjet(byElement[2]);
			if(byElement.length > 3) {
				mdp.setSiteWeb(byElement[3]);
			}
			if(byElement.length > 4) {
				mdp.setCommentaire(byElement[3]);
			}
			mdp.setIdIcone(0);
			dossierImport.getListeMotDePasse().add(mdp);
		}
		return dossierImport;
	}
	
	/**
	 * Exporte un dossier en String csv
	 * @param dossier le dossier
	 * @return la chaine à àcrire
	 */
	public byte[] exportCsv(Dossier dossier) {
		String data = convertDossierToCsv(dossier);
		return data.getBytes();
	}
	
	/**
	 * Màthode récursive pour ràcupàrer les mots de passe de sous dossies
	 * @param dossier le dossier à lire
	 * @return la chaine
	 */
	private String convertDossierToCsv(Dossier dossier) {
		StringBuilder data = new StringBuilder();
		if(dossier.getListeMotDePasse() != null && dossier.getListeMotDePasse().size() > 0) {
			for (MotDePasse mdp : dossier.getListeMotDePasse()) {
				data.append("\"").append(mdp.getTitre()).append("\",\"").append(mdp.getLogin()).append("\",\"").append(mdp.getMotDePasseObjet()).append("\",\"").append((mdp.getSiteWeb() != null) ? mdp.getSiteWeb() : " ").append("\",\"").append((mdp.getCommentaire() != null) ? mdp.getCommentaire() : " ").append("\";");
			}
		}
		if(dossier.getSousDossier() != null && dossier.getSousDossier().size() > 0) {
			for (Dossier dos : dossier.getSousDossier()) {
				data.append(convertDossierToCsv(dos));
			}
		}
		return data.toString();
	}
	
	
	//XML
	/**
	 * Converti des données xml en Dossier
	 * @param data les données
	 * @return le dossier
	 * @throws Exception
	 */
	public Dossier importXml(byte[] data) throws Exception {

		JAXBContext context = JAXBContext.newInstance(Dossier.class);
		Unmarshaller un = context.createUnmarshaller();
		InputStream is = new ByteArrayInputStream(data);
		Dossier dossierImport = (Dossier)un.unmarshal(is);
                dossierImport = PasswordBusiness.construireElementParent(dossierImport,null);
		return dossierImport;
	}
	
	/**
	 * Converti un dossier en xml
	 * @param dossier le dossier
	 * @return les données xml
	 * @throws Exception
	 */
	public byte[] exportXml(Dossier dossier) throws Exception  {
		JAXBContext context = JAXBContext.newInstance(Dossier.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
		StringWriter wr = new StringWriter();
		m.marshal(dossier,wr);
		return wr.toString().getBytes("UTF-8");
	}
	
	
	//SPJ
	/**
	 * Converti des données lues d'un fichier spj en dossier
	 * @param data les données
	 * @param mdp le mot de passe de déchiffrement
	 * @return le dossier
	 * @throws Exception
	 */
	public Dossier importSpe(byte[] data,String mdp) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CryptUtils.decrypt(mdp.toCharArray() , input, output);

		String xml = new String(output.toByteArray(),StandardCharsets.UTF_8);
		JAXBContext context = JAXBContext.newInstance(Dossier.class);
		Unmarshaller un = context.createUnmarshaller();

		Dossier dossierImport = new Dossier();
		dossierImport =  (Dossier)un.unmarshal(new StringReader(xml));
		dossierImport = PasswordBusiness.construireElementParent(dossierImport, null);
		return dossierImport;
	}
	
	/**
	 * Exporte un dossier en format Spj
	 * @param dossier le dossier à exporter
	 * @param mdp le mot de passe de chiffrement
	 * @return les données
	 * @throws Exception
	 */
	public byte[] exportSpe(Dossier dossier,String mdp) throws Exception {
		JAXBContext context  = JAXBContext.newInstance(Dossier.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter wr = new StringWriter();
		marshaller.marshal(dossier,wr);
		String xml = wr.toString();

		ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CryptUtils.encrypt(128,mdp.toCharArray() , input, output);
		return output.toByteArray();
	}
	

	/**
	 * Affiche une dlg pour demander le mot de passe
	 * @return le mot de passe
	 */
	public String askPassword() {
		Dialog<String> dlg = new Dialog<>();
		dlg.setTitle(bundle.getString("entrezMdp"));
		dlg.setHeaderText(bundle.getString("entrezMdpDechiffrement"));
		
		Label la = new Label();
    	la.setText(bundle.getString("mdp"));
    	PasswordField fieldA = new PasswordField();
    	
    	Label lb = new Label();
    	lb.setText(bundle.getString("confirmMdp"));
    	PasswordField fieldB = new PasswordField();
		
    	GridPane grid = new GridPane();
    	grid.add(la,0,0);
    	grid.add(fieldA, 1,0);
    	grid.add(lb,0,1);
    	grid.add(fieldB, 1,1);
    	dlg.getDialogPane().setContent(grid);
    	
    	ButtonType okButton = new ButtonType("OK",ButtonData.OK_DONE);
    	dlg.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
    	Node validButton = dlg.getDialogPane().lookupButton(okButton);
    	validButton.setDisable(true);
    	
    	fieldA.textProperty().addListener((observable,oldValue,newValue) -> validButton.setDisable(newValue.trim().isEmpty() || newValue.length() < 8 || !newValue.contentEquals(fieldB.getText())));
    	fieldB.textProperty().addListener((observable,oldValue,newValue) -> validButton.setDisable(newValue.trim().isEmpty() || newValue.length() < 8 || !newValue.contentEquals(fieldA.getText())));
    	Platform.runLater(fieldA::requestFocus);
    	
    	dlg.setResultConverter(dlgButton -> {
    		if(dlgButton == okButton) {
    			return fieldA.getText();
    		}
    		return null;
    	});
    	
    	Optional<String> res = dlg.showAndWait();

		return res.orElse(null);
	}
}
