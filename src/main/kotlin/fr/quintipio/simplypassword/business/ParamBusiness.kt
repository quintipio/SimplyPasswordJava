package fr.quintipio.simplypassword.business

import fr.quintipio.simplypassword.com.ComFile
import fr.quintipio.simplypassword.contexte.ContexteStatic
import java.io.File
import java.util.*


object ParamBusiness {


    var parametreLangue: String = ""
        set(value) {field = value.toLowerCase()}

    private const val paramAppliName = "param"
    private const val paramLive = "live:"
    private const val paramLiveYes = "o"
    private const val paramLiveNo = "n"
    private const val paramSeparator = ";"

    private const val paramUserName = ".SimplyPasswordConf.ini"
    private const val paramUserFile = "fichierChiffre"
    private const val paramUserLang = "lang"

    private var fileParamAppli: ComFile = ComFile(this.paramAppliName)
    private var fileParamUser: ComFile? = null



    fun getParamLangue(): String {
        if (parametreLangue.isBlank()) {
            val bundle = ResourceBundle.getBundle(ContexteStatic.bundle)
            val loc = bundle.locale
            parametreLangue = if (ContexteStatic.listeLangues.contains(loc.language)) loc.language else ContexteStatic.langueDefaut
        }
        return parametreLangue
    }

    ////METHODE PARAM APPLI
    /**
     * Vérifie si le fichier de paramètre appli existe, sinon cràation, et vérification des autorisations de lecture
     */
    private fun checkParamAppli(): Boolean {
        if (!fileParamAppli.file.exists()) {
            fileParamAppli.writeFile(paramLive + paramLiveNo + paramSeparator, true)
        }
        return fileParamAppli.file.exists() && fileParamAppli.file.canRead()
    }

    /**
     * Vérifie si l'application est en mode live ou non
     * @return true si en mode live
     */
    fun isModeLive(): Boolean {
        if (checkParamAppli()) {
            val data = fileParamAppli.readFileToString()
            if (data.isNotBlank()) {
                if (data.contains(paramLive)) {
                    val res = data.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (res.size > 1) {
                        val resb = res[1].split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        return resb[0].contentEquals(paramLiveYes)
                    }
                }
            }
        }
        return true
    }

    ///PARAMETRE UTILISATEUR
    /**
     * Retourne le chemin d'accàs du répertoire utilisateur et créer le répertoire si nécéssaire en fonction de l'os
     * @return le path du répertoire utilisateur avec concaténé le répertoire de l'appli
     */
    private fun getUserParamDirectory(): String {
        try {
            val res = System.getProperty("os.name").toLowerCase()
            val retour: String
            retour = when {
                res.contains("win") -> System.getProperty("user.home") + "\\AppData\\Local\\" + ContexteStatic.nomAppli.replace(" ".toRegex(), "")
                res.contains("mac") -> System.getProperty("user.home")
                res.contains("nix") -> System.getProperty("user.home")
                res.contains("nux") -> System.getProperty("user.home")
                res.contains("sunos") -> System.getProperty("user.home")
                else -> System.getProperty("user.home") + "/" + ContexteStatic.nomAppli.replace(" ".toRegex(), "")
            }

            val folder = ComFile(retour)
            if (!folder.file.exists()) {
                folder.file.mkdir()

            }
            return if(res.contains("win")) {
                retour + "\\" + paramUserName
            } else {
                "$retour/$paramUserName"
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            return ""
        }

    }

    /**
     * Génère le fichier de paramètre utilisateur
     */
    fun ecrireFichierParamUser() {
        if (!isModeLive()) {
            if (fileParamUser == null) {
                fileParamUser = ComFile(getUserParamDirectory())
            }

            var data = "<" + paramUserFile + ">" + (if (PasswordBusiness.fichier != null) PasswordBusiness.fichier!!.file.absolutePath else "") + "</" + paramUserFile + ">"
            data += "<" + paramUserLang + ">" + getParamLangue() + "</" + paramUserLang + ">"
            fileParamUser!!.file.delete()
            fileParamUser!!.writeFile(data, true)
        }
    }

    /**
     * Met en place les paramètres utilisateurs dans l'application
     */
    fun getDonneesParamUser() {
        if (fileParamUser == null) {
            fileParamUser = ComFile(getUserParamDirectory())
        }

        if (!fileParamUser!!.file.exists()) {
            ecrireFichierParamUser()
        }

        if (fileParamUser!!.file.canRead()) {
            val data = fileParamUser!!.readFileToString()

            var start = data.indexOf("<$paramUserFile>")
            var stop = data.lastIndexOf("</$paramUserFile>")
            var res = data.substring(start, stop)
            res = res.replace("<$paramUserFile>".toRegex(), "")
            res = res.replace("</$paramUserFile>".toRegex(), "")
            if (File(res).exists()) {
                PasswordBusiness.createFichier(res, false)
            }

            start = data.indexOf("<$paramUserLang>")
            stop = data.lastIndexOf("</$paramUserLang>")
            res = data.substring(start, stop)
            res = res.replace("<$paramUserLang>".toRegex(), "")
            res = res.replace("</$paramUserLang>".toRegex(), "")
            parametreLangue = res
        }
    }
}