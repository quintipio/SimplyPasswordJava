package fr.quintipio.simplyPassword.business

import fr.quintipio.simplyPassword.com.ComFile
import fr.quintipio.simplyPassword.model.Dossier
import fr.quintipio.simplyPassword.model.MotDePasse
import fr.quintipio.simplyPassword.util.CryptUtils
import java.io.*
import java.nio.charset.StandardCharsets
import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller


 object PasswordBusiness {

    var dossierMere: Dossier = Dossier()

    var fichier: ComFile? = null
        private set

    var motDePasse: String = ""

    var modif: Boolean = false

    private const val clePartage = "p55sbev7/2>tV8m7^]Fm4#4jJp%),a3fCpxE.E8?Fd{a=yE*g2R/yt(yG6~vu<fK,^5eP9?~EV8\$Bm8kc3L8X9.d7)bT#VH9JAjJ44!t279fR53M?3>rLX8.TmX77Y52)mTT5H7Ac27^mK99R+U@F@3Ac{-45n*r@PkJ4Y3Mg5sw2pr8CC9)95s9]Q4.~g*g2,m4t2_*95AT%C[KK7U;uA>^PgLLdU>}/aij&Luyf&~,3;6TX$&e_Z45;2E^SzyH"



    fun createFichier(path: String, changerParam: Boolean) {
        fichier = ComFile(path)
        if (changerParam) {
            ParamBusiness.ecrireFichierParamUser()
        }
    }

    /**
     * Charge les données à partir d'un fichier
     * @param path le chemin du fichier à charger
     * @param nouveauMotDePasse le mot de passe de déchiffrement
     * @throws Exception
     */
    @Throws(Exception::class)
    fun load(path: String, nouveauMotDePasse: String) {
        val nouveauFichier = ComFile(path)
        val data = nouveauFichier.readFileToByteArray()
        val input = ByteArrayInputStream(data)
        val output = ByteArrayOutputStream()
        CryptUtils.decrypt(nouveauMotDePasse.toCharArray(), input, output)

        val xml = String(output.toByteArray(),charset("UTF-8"))
        val context  = JAXBContext.newInstance(Dossier::class.java)
        val marshaller = context.createUnmarshaller()
        dossierMere = marshaller.unmarshal(StringReader(xml)) as Dossier
        construireElementParent(dossierMere,null)
        fichier = nouveauFichier
        motDePasse = nouveauMotDePasse
        modif = false
    }

    /**
     * Sauvegarde dans un fichier les mots de passe et dossiers
     */
    @Throws(Exception::class)
    fun save() {

        val context  = JAXBContext.newInstance(Dossier::class.java)
        val marshaller = context.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        val wr = StringWriter()
        marshaller.marshal(dossierMere,wr)
        val xml = wr.toString()

        val input = ByteArrayInputStream(xml.toByteArray(charset("UTF-8")))
        val output = ByteArrayOutputStream()
        CryptUtils.encrypt(128, motDePasse.toCharArray(), input, output)
        val pathTmp = fichier!!.file.absolutePath
        val fichierNew = ComFile(pathTmp + "_new")
        fichierNew.writeFile(output.toByteArray(), true)
        fichier!!.file.delete()
        fichierNew.file.renameTo(File(pathTmp))
        ParamBusiness.ecrireFichierParamUser()
        modif = false
    }

    /**
     * Vérifie si un fichier existe bien
     * @return true si ok
     */
    fun isFichier(): Boolean {
        return fichier?.file?.path?.isNotBlank() ?: false
    }

    /**
     * Vérifie si un mot de passe existe bien
     * @return true si ok
     */
    fun isMotDePasse(): Boolean {
        return motDePasse.isNotBlank()
    }


    /**
     * Réinitialise les données
     */
    fun reset() {
        dossierMere = Dossier()
        motDePasse = ""
        fichier = null
        modif = false
    }

    /**
     * Refait la hiérarchie des dossiers parents, mots de passe...
     * @param dossier le dossier à scanner
     * @param dossierParent le dossier parent à inscrire dans le dossier
     * @return le dossier
     */
    fun construireElementParent(dossier: Dossier, dossierParent: Dossier?): Dossier {
        dossier.dossierParent = dossierParent

        if (dossier.listeMotDePasse.isNotEmpty()) {
            dossier.listeMotDePasse.forEach { motDePasse -> motDePasse.dossierPossesseur = dossier }
        }


        if (dossier.sousDossier.isNotEmpty()) {
            dossier.sousDossier.forEach { dossier1 -> construireElementParent(dossier1, dossier) }
        }
        return dossier
    }

    /**
     * Lance une recherche de mot de passe sur les titres et les logins dans le dossier et ses sous dossiers
     * @param recherche le texte à rechercher
     * @param dossier le dossier dans lequel effectuer la recherche
     * @return les résultats
     */
    fun recherche(recherche: String, dossier: Dossier): List<MotDePasse> {
        val retour = mutableListOf<MotDePasse>()
        if (dossier.listeMotDePasse.isNotEmpty()) {
            retour.addAll(dossier.listeMotDePasse.filter { it.login.toLowerCase().contains(recherche.toLowerCase()) ||
                    it.commentaire.toLowerCase().contains(recherche.toLowerCase()) ||
                    it.titre.toLowerCase().contains(recherche.toLowerCase())})
         }
        if (dossier.sousDossier.isNotEmpty()) {
            for (dos in dossier.sousDossier) {
                retour.addAll(recherche(recherche, dos))
            }
        }
        return retour
    }


    /**
     * Déchiffre un byte[] et retourne le mot de passe
     * @param data les données à déchiffrer
     * @return le mot de passe
     */
    @Throws(Exception::class)
    fun dechiffrerPartage(data: ByteArray): MotDePasse {
        val input = ByteArrayInputStream(data)
        val output = ByteArrayOutputStream()
        CryptUtils.decrypt(clePartage.toCharArray(), input, output)

        val xml = String(output.toByteArray(), StandardCharsets.UTF_8)
        val context = JAXBContext.newInstance(MotDePasse::class.java)
        val un = context.createUnmarshaller()

        return un.unmarshal(StringReader(xml)) as MotDePasse
    }

    /**
     * Retourne un mot de passe chiffré prêt à être écrit dans un fichier
     * @param motDePasse le mot de passe à partager
     * @return le mot de passe chiffré en AES
     */
    @Throws(Exception::class)
    fun genererPartage(motDePasse: MotDePasse): ByteArray {

        val context = JAXBContext.newInstance(MotDePasse::class.java)
        val marshaller = context.createMarshaller()
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
        val wr = StringWriter()
        marshaller.marshal(motDePasse, wr)
        val xml = wr.toString()

        val input = ByteArrayInputStream(xml.toByteArray(StandardCharsets.UTF_8))
        val output = ByteArrayOutputStream()
        CryptUtils.encrypt(128, clePartage.toCharArray(), input, output)
        return output.toByteArray()
    }

}