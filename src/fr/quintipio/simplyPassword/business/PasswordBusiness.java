package fr.quintipio.simplyPassword.business;

import fr.quintipio.simplyPassword.com.ComFile;
import fr.quintipio.simplyPassword.model.Dossier;
import fr.quintipio.simplyPassword.model.MotDePasse;
import fr.quintipio.simplyPassword.util.CryptUtils;
import fr.quintipio.simplyPassword.util.ObjectUtils;
import fr.quintipio.simplyPassword.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    private static final String clePartage = "p55sbev7/2>tV8m7^]Fm4#4jJp%),a3fCpxE.E8?Fd{a=yE*g2R/yt(yG6~vu<fK,^5eP9?~EV8$Bm8kc3L8X9.d7)bT#VH9JAjJ44!t279fR53M?3>rLX8.TmX77Y52)mTT5H7Ac27^mK99R+U@F@3Ac{-45n*r@PkJ4Y3Mg5sw2pr8CC9)95s9]Q4.~g*g2,m4t2_*95AT%C[KK7U;uA>^PgLLdU>}/aij&Luyf&~,3;6TX$&e_Z45;2E^SzyH";

    ///GETTER ET SETTER

    public static ComFile getFichier() {
        return fichier;
    }

    public static void setFichier(String path,boolean changerParam) {

        fichier = new ComFile(path);
        if(changerParam) {
            ParamBusiness.ecrireFichierParamUser();
        }
    }

    public static String getMotDePasse() {
        return motDePasse;
    }

    public static void setMotDePasse(String motDePasse) {
        PasswordBusiness.motDePasse = motDePasse;
    }

    public static Dossier getDossierMere() {return dossierMere;}
    
    public static void setDossierMere(Dossier dossierMere) {
    	PasswordBusiness.dossierMere = dossierMere;
    }

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
     * Charge les données à partir d'un fichier
     * @param path le chemin du fichier à charger
     * @param nouveauMotDePasse le mot de passe de déchiffrement
     * @throws Exception
     */
    public static void load(String path,String nouveauMotDePasse) throws Exception {
        ComFile nouveauFichier = new ComFile(path);
        byte[] data = nouveauFichier.readFileToByteArray();
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CryptUtils.decrypt(nouveauMotDePasse.toCharArray() , input, output);
        dossierMere = (Dossier)ObjectUtils.deserialize(output.toByteArray());
        fichier = nouveauFichier;
        motDePasse = nouveauMotDePasse;
    }
    
    /**
     * Sauvegarde dans un fichier
     */
    public static void save() throws Exception {
        byte[] data = ObjectUtils.serialize(dossierMere);
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CryptUtils.encrypt(128,motDePasse.toCharArray() , input, output);
        fichier.writeFile(output.toByteArray(),true);
        ParamBusiness.ecrireFichierParamUser();
        modif = false;
    }

    /**
     * Vérifie si un fichier existe bien
     * @return true si ok
     */
    public static boolean isFichier() {
        return fichier != null && !StringUtils.stringEmpty(fichier.getFile().getPath());
    }

    /**
     * Vérifie si un mot de passe existe bien
     * @return true si ok
     */
    public static boolean isMotDePasse() {
        return !StringUtils.stringEmpty(motDePasse);
    }

    

    /**
     * Réinitialise les données
     */
    public static void reset() {
        dossierMere = null;
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

    /**
     * Retourne un mot de passe chiffré prêt à être écrit dans un fichier
     * @param motDePasse le mot de passe à partager
     * @return le mot de passe chiffré en AES
     */
    public static byte[] genererPartage(MotDePasse motDePasse) throws Exception {
        byte[] data = ObjectUtils.serialize(motDePasse);
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CryptUtils.encrypt(128,clePartage.toCharArray() , input, output);
        return output.toByteArray();
    }

    /**
     * Déchiffre un byte[] et retourne le mot de passe
     * @param data les données à déchiffrer
     * @return le mot de passe
     */
    public static MotDePasse dechiffrerPartage(byte[] data) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        CryptUtils.decrypt(clePartage.toCharArray() , input, output);
        return (MotDePasse)ObjectUtils.deserialize(output.toByteArray());
    }
}
