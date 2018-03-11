package fr.quintipio.simplyPassword.util;

import fr.quintipio.simplyPassword.model.MotDePasse;
import java.io.*;

/**
 * Classe d'utilitaire liés aux objets en général
 */
public class ObjectUtils {

    /**
     * Sérialize un objet
     * @param obj l'objet à sérializer
     * @return le byte[]
     * @throws IOException
     */
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    /**
     * Désérialize un objet
     * @param data les données à désérializer
     * @return l'objet à caster
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
    
    /**
     * Copier un mot de passe
     * @param mdp le mot de passe
     * @return le nouveau mot de passe
     */
    public static MotDePasse copyMotDePasse(MotDePasse mdp) {
        MotDePasse newMdp = new MotDePasse();
        if(mdp != null) {
            if(mdp.getTitre() != null) {
                newMdp.setTitre(new String(mdp.getTitre()));
            }
            
            if(mdp.getLogin() != null) {
                newMdp.setLogin(new String(mdp.getLogin()));
            }
            
            if(mdp.getMotDePasseObjet() != null) {
                newMdp.setMotDePasseObjet(new String(mdp.getMotDePasseObjet()));
            }
            if(mdp.getSiteWeb() != null) {
                newMdp.setSiteWeb(new String(mdp.getSiteWeb()));
            }
            
            if(mdp.getCommentaire() != null) {
                newMdp.setCommentaire(new String(mdp.getCommentaire()));
            }
            
            if(mdp.getIdIcone() != null) {
                newMdp.setIdIcone(new Integer(mdp.getIdIcone()));
            }
            newMdp.setDossierPossesseur( mdp.getDossierPossesseur());
        }
        return newMdp;
    }
}
