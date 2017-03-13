package fr.quintipio.simplyPassword.contexte;

/**
 * Paramètres fixe de l'application
 */
public class ContexteStatic {

    /**
     * Nom de l'application
     */
    public static final String nomAppli = "Simply Password";

    /**
     * Numàro de version
     */
    public static final String version = "1.0";

    /**
     * Nom du développeur
     */
    public static final String developpeur = "Quentin Delfour";
	
    /**
     * Chemin du répertoire ou se trouve les textes de l'application
     */
    public static final String bundle = "bundle/strings";

    /**
     * Extension accepté pour charger et sauvegarder les données
     */
    public static final String extension = ".spj";

    /**
     * Extension pour les fichiers de partage de mot de passe
     */
    public static final String extensionPartage = ".spp";
    
    /**
     * Extension pour les fichiers exporter chiffré
     */
    public static final String extensionExport = ".spe";
    
    /**
     * Duràe en seconde de la copie d'un identifiant ou d'un mot de passe dans le presse papier
     */
    public static final int dureeTimerCopieClipboard = 20;
    
    /**
     * La liste des langues disponibles
     */
    public static final String[] listeLangues = {"fr","en"};
    
    /**
     * La langue par défaut de l'application
     */
    public static String langueDefaut = listeLangues[0];
}
