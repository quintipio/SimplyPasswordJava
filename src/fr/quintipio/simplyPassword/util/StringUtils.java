package fr.quintipio.simplyPassword.util;

public class StringUtils {

	/**
	 * V�rifie si une chaine de caract�re est vide
	 * @param chaine la chaine
	 * @return true si vide
	 */
	public static boolean stringEmpty(String chaine) {
		if(chaine == null) {
			return true;
		}
		else {
			return chaine.isEmpty() || chaine.length() == 0;
		}
	}
	

}
