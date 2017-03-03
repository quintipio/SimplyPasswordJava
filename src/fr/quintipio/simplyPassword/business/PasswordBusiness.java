package fr.quintipio.simplyPassword.business;

import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.impl.ICrypt;
import fr.quintipio.simplyPassword.model.Dossier;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.util.AesCrypt;
import fr.quintipio.simplyPassword.util.Base64Crypt;
import fr.quintipio.simplyPassword.util.ObjectUtils;
import fr.quintipio.simplyPassword.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe de gestion des mots de passes et des dossiers
 */
public class PasswordBusiness {

    private static Dossier dossierMere;

    private static ComFile fichier;

    private static String motDePasse;

    private static boolean modif;

    ///GETTER ET SETTER

    public static ComFile getFichier() {
        return fichier;
    }

    public static void setFichier(String path) {

        fichier = new ComFile(path);
    }

    public static String getMotDePasse() {
        return motDePasse;
    }

    public static void setMotDePasse(String motDePasse) {
        PasswordBusiness.motDePasse = motDePasse;
    }

    public static Dossier getDossierMere() {return dossierMere;}

    public static boolean isModif() {
        return modif;
    }

    public static void setModif(boolean modif) {
        PasswordBusiness.modif = modif;
    }

    public static void  init() {


        dossierMere = new Dossier("Dossier racine",null);
        Dossier dossierA = new Dossier("Dossier A",dossierMere);
        Dossier dossierB = new Dossier("Dossier B",dossierMere);
        Dossier dossierC = new Dossier("Dossier C",dossierA);

        MotDePasse mdpa = new MotDePasse();
        mdpa.setTitre("piou");
        mdpa.setLogin("toto");
        mdpa.setMotDePasseObjet("toto");
        mdpa.setDossierPossesseur(dossierC);
        MotDePasse mdpb = new MotDePasse();
        mdpb.setTitre("pioupiou");
        mdpb.setLogin("tata");
        mdpb.setMotDePasseObjet("tata");
        mdpb.setDossierPossesseur(dossierC);


        dossierMere.getSousDossier().add(dossierA);
        dossierMere.getSousDossier().add(dossierB);
        dossierA.getSousDossier().add(dossierC);
        dossierC.getListeMotDePasse().add(mdpa);
        dossierA.getListeMotDePasse().add(mdpb);

    }
    ///METHODES DE GESTION
    /**
     * Sauvegarde dans un fichier
     */
    public static void save(String path) throws Exception {
        ICrypt crypt = Base64Crypt.wrap(new AesCrypt());
        byte[] data = ObjectUtils.serialize(dossierMere);
        byte[] dataCipher = crypt.encodeByteArray(data,motDePasse);
        if(path != null && path.isEmpty()) {
            fichier = new ComFile(path);
        }
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
        return !StringUtils.stringEmpty(motDePasse);
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

    /**
     * Réinitialise les données
     */
    public static void reset() {
        dossierMere = new Dossier();
        dossierMere.setTitre("Racine");
        motDePasse = null;
        fichier = null;
    }

    /**
     * Lance une recherche de mot de passe sur les titres et les logins dans le dossier et ses sous dossiers
     * @param recherche le texte à rechercher
     * @param dossier le dossier dans lequel effectuer la recherche
     * @return les résultats
     */
    public static List<MotDePasse> recherche(String recherche,Dossier dossier) {
        List<MotDePasse> retour = new ArrayList<>();
        if(dossier.getListeMotDePasse() != null && dossier.getListeMotDePasse().size() > 0) {
            retour.addAll(dossier.getListeMotDePasse().stream().filter(mdp -> mdp.getLogin().contains(recherche) || mdp.getTitre().contains(recherche)).collect(Collectors.toList()));
        }
        if(dossier.getSousDossier() != null && dossier.getSousDossier().size() > 0) {
            for (Dossier dos : dossier.getSousDossier()) {
                retour.addAll(recherche(recherche, dos));
            }
        }
        return retour;
    }
}
