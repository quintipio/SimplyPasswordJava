package fr.quintipio.simplyPassword.business;

import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.impl.ICrypt;
import fr.quintipio.simplyPassword.model.Dossier;
import fr.quintipio.simplyPassword.util.AesCrypt;
import fr.quintipio.simplyPassword.util.Base64Crypt;
import fr.quintipio.simplyPassword.util.ObjectUtils;

/**
 * Classe de gestion des mots de passes et des dossiers
 */
public class PasswordBusiness {

    private static Dossier dossierMere;

    private static ComFile fichier;

    private static String motDePasse;

    /**
     * Sauvegarde dans un fichier
     */
    public static void save() throws Exception {
        ICrypt crypt = Base64Crypt.wrap(new AesCrypt());
        byte[] data = ObjectUtils.serialize(dossierMere);
        byte[] dataCipher = crypt.encodeByteArray(data,motDePasse);
        fichier.writeFile(dataCipher,true);
    }

    /**
     * Vérifie si un fichier existe bien
     * @return true si ok
     */
    public static boolean isFichier() {
        return fichier != null;
    }

    /**
     * Vérifie si un mot de passe existe bien
     * @return true si ok
     */
    public static boolean isMotDePasse() {
        return motDePasse != null && !motDePasse.isEmpty();
    }

    /**
     * Charge les données à partir d'un fichier
     * @param path le chemin du fichier à charger
     * @param nouveauMotDePasse le mot de passe de déchiffrement
     * @throws Exception
     */
    public static void load(String path,String nouveauMotDePasse) throws Exception {
        ComFile nouveauFichier = new ComFile(path);
        ICrypt crypt = Base64Crypt.wrap(new AesCrypt());
        byte[] data = nouveauFichier.readFileToByteArray();
        byte[] dataDecipher = crypt.decodeByteArray(data,motDePasse);
        dossierMere = (Dossier)ObjectUtils.deserialize(dataDecipher);
        fichier = nouveauFichier;
        motDePasse = nouveauMotDePasse;

    }
}
