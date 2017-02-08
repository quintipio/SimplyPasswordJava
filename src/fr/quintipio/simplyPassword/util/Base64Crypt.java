package fr.quintipio.simplyPassword.util;

import fr.quintipio.simplyPassword.impl.ICrypt;

import java.util.Base64;

/**
 * Utilitaire pour chiffrer et déchiffrer en base 64
 */
public class Base64Crypt implements ICrypt {

    /**
     * Objet de chiffrement
     */
    private ICrypt realCrypt;

    /**
     * Constructeur
     * @param crypt l'outil de chiffrement de base
     */
    private Base64Crypt(ICrypt crypt) {
        this.realCrypt = crypt;
    }

    /**
     * intancie un nouvel objet pour chiffrer en base 64
     * @param real l'outil de chiffrement de base
     * @return l'outil de chiffrement pour la base 64
     */
    public static ICrypt wrap(ICrypt real) {
        return new Base64Crypt(real);
    }

    /**
     * Encode un string en AES avec base 64
     * @param plainText le texte à chiffrer
     * @param password le mot de passe
     * @return la chaine chiffré
     * @throws Exception
     */
    @Override
    public String encode(String plainText,String password) throws Exception {
        String encoded = realCrypt.encode(plainText,password);
        return Base64.getEncoder().encodeToString(encoded.getBytes());
    }

    /**
     * décode une string en base 64
     * @param encodedText le texte à décoder
     * @param password le mot de passe
     * @return la chainé décodé
     * @throws Exception
     */
    @Override
    public String decode(String encodedText,String password) throws Exception {
        byte[] encodedBytes = Base64.getDecoder().decode(encodedText);
        return realCrypt.decode(new String(encodedBytes),password);
    }

    /**
     * Encode un byte[] en base 64 n AES
     * @param data les données à chiffrer
     * @param password le mot de passe
     * @return le byt[] chiffrer
     * @throws Exception
     */
    @Override
    public byte[] encodeByteArray(byte[] data, String password) throws Exception {
        byte[] encoded = realCrypt.encodeByteArray(data,password);
        return Base64.getEncoder().encode(encoded);
    }

    /**
     * Décode un byte[] en base 64 en AES
     * @param data les données à déchiffrer
     * @param password le mot de passe
     * @return le byte[] déchiffrer
     * @throws Exception
     */
    @Override
    public byte[] decodeByteArray(byte[] data, String password) throws Exception {
        byte[] encodedBytes = Base64.getDecoder().decode(data);
        return realCrypt.decodeByteArray(encodedBytes,password);
    }
}
