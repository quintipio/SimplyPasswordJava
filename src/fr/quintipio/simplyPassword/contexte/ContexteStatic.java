package fr.quintipio.simplyPassword.contexte;

/**
 * Paramètres fixe de l'application
 */
public class ContexteStatic {

	/**
	 * Nom de l'application
	 */
	public static String nomAppli = "Simply Password";
	
	/**
	 * Numàro de version
	 */
	public static String version = "0.9";
	
	/**
	 * Nom du développeur
	 */
	public static String developpeur = "Quentin Delfour";
	
    /**
     * Chemin du répertoire ou se trouve les textes de l'application
     */
    public static String bundle = "bundle/strings";

    /**
     * Extension accepté pour charger et sauvegarder les données
     */
    public static String extension = ".spj";
    
    /**
     * Duràe en seconde de la copie d'un identifiant ou d'un mot de passe dans le presse papier
     */
    public static int dureeTimerCopieClipboard = 20;
    
    /**
     * La liste des langues disponibles
     */
    public static String[] listeLangues = {"fr","en"};
    
    /**
     * La langue par défaut de l'application
     */
    public static String langueDefaut = listeLangues[0];
}
