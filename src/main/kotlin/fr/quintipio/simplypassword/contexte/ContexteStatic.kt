package fr.quintipio.simplypassword.contexte


/**
 * Paramètres fixe de l'application
 */
object ContexteStatic {

    /**
     * Nom de l'application
     */
    const val nomAppli = "Simply Password"

    /**
     * Numàro de version
     */
    const val version = "1.4"

    /**
     * Nom du développeur
     */
    const  val developpeur = "Quentin Delfour"

    /**
     * Chemin du répertoire ou se trouve les textes de l'application
     */
    const val bundle = "bundle/strings"

    /**
     * Extension accepté pour charger et sauvegarder les données
     */
    const val extension = ".spj"

    /**
     * Extension pour les fichiers de partage de mot de passe
     */
    const val extensionPartage = ".spp"

    /**
     * Extension pour les fichiers exporter chiffré
     */
    const val extensionExport = ".spe"

    /**
     * Duràe en seconde de la copie d'un identifiant ou d'un mot de passe dans le presse papier
     */
    const val dureeTimerCopieClipboard = 20

    /**
     * La liste des langues disponibles
     */
    val listeLangues = arrayOf("fr", "en")

    /**
     * La langue par défaut de l'application
     */
    var langueDefaut = listeLangues[0]
}