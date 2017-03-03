package fr.quintipio.simplyPassword.business;


import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.contexte.ContexteStatic;
import fr.quintipio.simplyPassword.util.StringUtils;

public class ParamBusiness {

	/**
	 * Fichier de paramètre de l'appli
	 */
	private static ComFile fileParamAppli;
	/**
	 * Fichier de paramètre de l'utilisateur
	 */
	private static ComFile fileParamUser;
	
	private static String parametreLangue;
	
	private static String paramAppliName="param";
	private static String paramLive = "live:";
	private static String paramLiveYes = "o";
	private static String paramLiveNo = "o";
	private static String paramSeparator = ";";
	
	private static String paramUserName = "LocalConf.ini";
	private static String paramUserFile="fichierChiffre";
	private static String paramUserLang="lang";
	
	
	public static String getParametreLangue() {
		return parametreLangue;
	}
	
	
	////METHODE PARAM APPLI
	/**
	 * Vérifie si le fichier de paramètre appli existe, sinon création, et vérification des autorisations de lecture
	 */
	private static boolean checkParamAppli() {
		if(fileParamAppli == null) {
			fileParamAppli = new ComFile(paramAppliName);
		}
		
		if(!fileParamAppli.getFile().exists() && fileParamAppli.getFile().canWrite()) {
			fileParamAppli.writeFile(paramLive+paramLiveNo+paramSeparator,true);
		}
		
		return fileParamAppli.getFile().exists() && fileParamAppli.getFile().canRead();
	}
	
	/**
	 * Vérifie si l'application est en mode live ou non
	 * @return true si en mode live
	 */
	private static boolean isModeLive() {
		if(checkParamAppli()) {
			String data = fileParamAppli.readFileToString();
			if(!StringUtils.stringEmpty(data)) {
				if(data.contains(paramLive)) {
					String[] res = data.split(":");
					if(res.length > 1) {
						return res[1].contentEquals(paramLiveYes);
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Retourne le chemin d'accès du répertoire utilisateur
	 * @return
	 */
	private static String getUserParamDirectory() {
		String res = System.getProperty("os.name");
		if(res.indexOf("win") >= 0) {
			return System.getProperty("user.home")+"\\AppData\\Local\\"+ContexteStatic.nomAppli.replace(' ','_')+"\\"+paramUserName;
		}
		if(res.indexOf("mac") >= 0) {
			return System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replace(' ','_')+"\\"+paramUserName;
		}
		if(res.indexOf("nix") >= 0) {
			return System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replace(' ','_')+"\\"+paramUserName;
		}
		if(res.indexOf("sunos") >= 0) {
			return System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replace(' ','_')+"\\"+paramUserName;
		}
		return System.getProperty("user.home")+"\\"+ContexteStatic.nomAppli.replace(' ','_')+"\\"+paramUserName;
	}
	
	/**
	 * Génère le fichier de paramètre utilisateur
	 */
	public static void ecrireFichierParamUser() {
		if(!isModeLive()) {
			if(fileParamUser == null) {
				fileParamUser = new ComFile(getUserParamDirectory());
			}
			
			if(fileParamUser.getFile().canWrite()) {
				String data = "<"+paramUserFile+">"+((PasswordBusiness.getFichier() != null)?PasswordBusiness.getFichier().getFile().getAbsolutePath():"")+"</"+paramUserFile+">";
				data += "<"+paramUserLang+">"+parametreLangue+"</"+paramUserLang+">";
				fileParamUser.writeFile(data,true);
			}
		}
	}
	
	/**
	 * Met en place les paramètres dans l'application
	 */
	public static void getDonneesParamUser() {
		if(fileParamUser == null) {
			fileParamUser = new ComFile(getUserParamDirectory());
		}
		
		if(fileParamUser.getFile().canRead()) {
			String data =fileParamUser.readFileToString();

			int start = data.indexOf("<"+paramUserFile+">");
			int stop = data.lastIndexOf("</"+paramUserFile+">");
            String res = data.substring(start,stop);
            res = res.replaceAll("<"+paramUserFile+">","");
            res = res.replaceAll("</"+paramUserFile+">","");
            PasswordBusiness.setFichier(res);

            start = data.indexOf("<"+paramUserLang+">");
            stop = data.lastIndexOf("</"+paramUserLang+">");
            res = data.substring(start,stop);
            res = res.replaceAll("<"+paramUserLang+">","");
            res = res.replaceAll("</"+paramUserLang+">","");
            parametreLangue = res;
		}
	}
	
	/**
	 * Initialise les paramètres de l'application
	 */
	public static void init() {
		isModeLive();
        getDonneesParamUser();
	}
	
}
