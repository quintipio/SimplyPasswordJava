package fr.quintipio.simplyPassword.business;


import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;

import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.util.StringUtils;

public class ParamBusiness {

	
	private static ComFile fileParamAppli;
	private static ComFile fileParamUser;
	
	private static String parametreLangue;
	
	private static String paramAppliName="param";
	private static String paramLive = "live:";
	private static String paramLiveYes = "o";
	private static String paramLiveNo = "n";
	private static String paramSeparator = ";";
	
	private static String paramUserName = "LocalConf.ini";
	private static String paramUserFile="fichierChiffre";
	private static String paramUserLang="lang";
	
	
	///GESTION DE LA LANGUE
	/**
	 * Retourne la langue sélectionné, sinon retourne la langue par défaut
	 * @return le code langue sélectionné pour l'appli
	 */
	public static String getParametreLangue() {
		if(StringUtils.stringEmpty(parametreLangue)) {
			ResourceBundle bundle = ResourceBundle.getBundle(ContexteStatic.bundle);
			Locale loc = bundle.getLocale();
			if(Arrays.asList(ContexteStatic.listeLangues).contains(loc.getLanguage())) {
				parametreLangue = loc.getLanguage();
			}
			else {
				parametreLangue = ContexteStatic.langueDefaut;
			}
		}
		return parametreLangue;
	}
	
	/**
	 * Modifie la langue par défaut
	 * @param langue la nouvelle langue (doit être présente dans ContexteStatic.listeLangues[])
	 */
	public static void setParametreLangue(String langue) {
		if(langue != null) {
			parametreLangue = langue.toLowerCase();
		}
		else {
			parametreLangue = ContexteStatic.langueDefaut;
		}
	}
	
	
	////METHODE PARAM APPLI
	/**
	 * Vérifie si le fichier de paramètre appli existe, sinon cràation, et vérification des autorisations de lecture
	 */
	private static boolean checkParamAppli() {
		if(fileParamAppli == null) {
			fileParamAppli = new ComFile(paramAppliName);
		}
		
		if(!fileParamAppli.getFile().exists()) {
			fileParamAppli.writeFile(paramLive+paramLiveNo+paramSeparator,true);
		}
		
		return fileParamAppli.getFile().exists() && fileParamAppli.getFile().canRead();
	}
	
	/**
	 * Vérifie si l'application est en mode live ou non
	 * @return true si en mode live
	 */
	public static boolean isModeLive() {
		if(checkParamAppli()) {
			String data = fileParamAppli.readFileToString();
			if(!StringUtils.stringEmpty(data)) {
				if(data.contains(paramLive)) {
					String[] res = data.split(":");
					if(res.length > 1) {
                                                String[] resb = res[1].split(";");
                                                boolean b= resb[0].contentEquals(paramLiveYes);
						return resb[0].contentEquals(paramLiveYes);
					}
				}
			}
		}
		return false;
	}
	
	
	///PARAMETRE UTILISATEUR
	/**
	 * Retourne le chemin d'accàs du répertoire utilisateur et créer le répertoire si nécéssaire en fonction de l'os
	 * @return le path du répertoire utilisateur avec concaténé le répertoire de l'appli
	 */
	private static String getUserParamDirectory() {
		try {
		String res = System.getProperty("os.name").toLowerCase();
		String retour = "";
		if(res.indexOf("win") >= 0) {
			retour = System.getProperty("user.home")+"\\AppData\\Local\\"+ContexteStatic.nomAppli.replaceAll(" ","");
		}
		else if(res.indexOf("mac") >= 0) {
			retour =  System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replaceAll(" ","");
		}
		else if(res.indexOf("nix") >= 0) {
			retour =  System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replaceAll(" ","");
		}
		else if(res.indexOf("sunos") >= 0) {
			retour =  System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replaceAll(" ","");
		}
		else {
			retour = System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replaceAll(" ","");
		}
		
		ComFile folder = new ComFile(retour);
		if(!folder.getFile().exists()) {
				folder.getFile().mkdir();
			
		}
		return retour+"\\"+paramUserName;
		} catch (SecurityException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Génère le fichier de paramètre utilisateur
	 */
	public static void ecrireFichierParamUser() {
		if(!isModeLive()) {
			if(fileParamUser == null) {
				fileParamUser = new ComFile(getUserParamDirectory());
			}
			
			String data = "<"+paramUserFile+">"+((PasswordBusiness.getFichier() != null)?PasswordBusiness.getFichier().getFile().getAbsolutePath():"")+"</"+paramUserFile+">";
			data += "<"+paramUserLang+">"+getParametreLangue()+"</"+paramUserLang+">";
			fileParamUser.getFile().delete();
			fileParamUser.writeFile(data,true);
		}
	}
	
	/**
	 * Met en place les paramètres utilisateurs dans l'application
	 */
	public static void getDonneesParamUser() {
		if(fileParamUser == null) {
			fileParamUser = new ComFile(getUserParamDirectory());
		}
		
		if(!fileParamUser.getFile().exists()) {
			ecrireFichierParamUser();
		}
		
		if(fileParamUser.getFile().canRead()) {
			String data =fileParamUser.readFileToString();

			int start = data.indexOf("<"+paramUserFile+">");
			int stop = data.lastIndexOf("</"+paramUserFile+">");
            String res = data.substring(start,stop);
            res = res.replaceAll("<"+paramUserFile+">","");
            res = res.replaceAll("</"+paramUserFile+">","");
            if(new File(res).exists()) {
                PasswordBusiness.setFichier(res,false);
            }

            start = data.indexOf("<"+paramUserLang+">");
            stop = data.lastIndexOf("</"+paramUserLang+">");
            res = data.substring(start,stop);
            res = res.replaceAll("<"+paramUserLang+">","");
            res = res.replaceAll("</"+paramUserLang+">","");
            parametreLangue = res;
		}
	}
}
