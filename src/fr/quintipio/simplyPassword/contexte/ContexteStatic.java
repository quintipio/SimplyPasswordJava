package fr.quintipio.simplyPassword.contexte;

/**
 * Param√®tres fixe de l'application
 */
public class ContexteStatic {

	/**
	 * Nom de l'application
	 */
	public static String nomAppli = "Simply Password";
	
	/**
	 * NumÈro de version
	 */
	public static String version = "0.9";
	
	/**
	 * Nom du dÈveloppeur
	 */
	public static String developpeur = "Quentin Delfour";
	
    /**
     * Chemin du r√©pertoire ou se trouve les textes de l'application
     */
    public static String bundle = "bundle/strings";

    /**
     * Extension accept√© pour charger et sauvegarder les donn√©es
     */
    public static String extension = ".spj";
    
    /**
     * DurÈe en seconde de la copie d'un identifiant ou d'un mot de passe dans le presse papier
     */
    public static int dureeTimerCopieClipboard = 20;
}
