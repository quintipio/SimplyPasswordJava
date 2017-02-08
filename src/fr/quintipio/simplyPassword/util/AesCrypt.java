package fr.quintipio.simplyPassword.util;


import fr.quintipio.simplyPassword.impl.ICrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;

/**
 * Classe pour chiffrer et déchiffrer en aes
 */
public class AesCrypt implements ICrypt {

    private byte[]     salt                = new byte[20];
    private byte[]     ivBytes             = null;

    private static final int PASSWORD_ITERATIONS = 65536;
    private static final int KEY_LENGTH          = 128;

    /**
     * Constructeur
     */
    public AesCrypt() {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.setSeed(secureRandom.generateSeed(16));
        secureRandom.nextBytes(salt);
    }


    /**
     * Créer un objet de chiffrement
     * @param encryptMode true si c'est pour chiffrer sinon false
     * @param password le mot de passe
     * @return l'objet pour chiffrer et déchiffrer
     * @throws Exception
     */
    private Cipher createCipher(boolean encryptMode, String password) throws Exception {

        if (!encryptMode && ivBytes == null) {
            throw new IllegalStateException("ivBytes is null");
        }

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PASSWORD_ITERATIONS, KEY_LENGTH);

        SecretKey secretKey = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        int mode = encryptMode ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;

        if (ivBytes == null) {

            cipher.init(mode, secret);
            AlgorithmParameters params = cipher.getParameters();
            ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();

        } else {

            cipher.init(mode, secret, new IvParameterSpec(ivBytes));
        }

        return cipher;
    }

    /**
     * Encode en AES
     * @param plainText le texte
     * @param password le mot de passe
     * @return le texte chiffré
     * @throws Exception
     */
    @Override
    public String encode(String plainText,String password) throws Exception {

        Cipher cipher = createCipher(true,password);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return new String(encryptedBytes);

    }

    /**
     * Décode en AES
     * @param encodedText le texte
     * @param password le mot de passe
     * @return le texte déchiffrer
     * @throws Exception
     */
    @Override
    public String decode(String encodedText,String password) throws Exception {

        Cipher cipher = createCipher(false,password);
        return new String(cipher.doFinal(encodedText.getBytes()), "UTF-8");
    }

    /**
     * encode en AES
     * @param data les données
     * @param password le mot de passe
     * @return les données chiffrées
     * @throws Exception
     */
    @Override
    public byte[] encodeByteArray(byte[] data, String password) throws Exception {
        Cipher cipher = createCipher(true,password);
        return cipher.doFinal(data);
    }

    /**
     * Décode en AES
     * @param data les données
     * @param password le mot de passe
     * @return les données déchifrées
     * @throws Exception
     */
    @Override
    public byte[] decodeByteArray(byte[] data, String password) throws Exception {
        Cipher cipher = createCipher(false,password);
        return cipher.doFinal(data);
    }
}
