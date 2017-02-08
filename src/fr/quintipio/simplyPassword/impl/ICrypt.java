package fr.quintipio.simplyPassword.impl;

/**
 * Interface de gestion de chiffrement
 */
public interface ICrypt {

    String encode(String plainText,String password)     throws Exception;
    String decode(String encodedText,String password)   throws Exception;
    byte[] encodeByteArray(byte[] data,String password)   throws Exception;
    byte[] decodeByteArray(byte[] data,String password)   throws Exception;
}
