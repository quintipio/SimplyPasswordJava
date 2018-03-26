package fr.quintipio.simplyPassword.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptUtils {

		//POUR AES
		private static final String CIPHER_SPEC = "AES/CBC/PKCS5Padding";
		private static final String KEYGEN_SPEC = "PBKDF2WithHmacSHA1";
		private static final int SALT_LENGTH = 16; 
		private static final int AUTH_KEY_LENGTH = 8;
		private static final int ITERATIONS = 32768;
		private static final int BUFFER_SIZE = 1024;
	
	
		private static char[] listeLettreMinuscule = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'
        , 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        private static char[] listeLettreMajuscule = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K'
        , 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        private static char[] listeChiffre = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
	private static char[] listeCaractereSpeciaux = { '²', '&', 'é', '"', '#', '\'', '{', '-', '|', 'è', '_', '\\', 'ç', 'à', '@', ')', '(', '[', ']', '=', '+', '}', '£', '$', '¤', '%', 'ù', 'µ', '*', '?', ',', '.', ';', '/', ':', '§', '!', '€', '>', '<'};
	
	/**
	 * Calcul la force approximative d'un mot de passe
	 * @param motDePasse le mot de passe à calculer
	 * @return la force du mot de passe comprise entre 0 et 100
	 */
	public static int calculerForceMotDePasse(String motDePasse) {
		int somme = 0;
		int nbTypePresent = 0;
		boolean minusculePresent = false;
		boolean majusculePresent = false;
		boolean chiffrePresent = false;
		boolean speciauxPresent = false;
		
		if(motDePasse == null) {
			motDePasse = "";
		}
		
		for (char character : motDePasse.toCharArray()) {
			for (char c : listeLettreMinuscule) {
				if(c == character) {
					minusculePresent = true;
	                somme += 4;
	                break;
				}
			}
			
			for (char c : listeLettreMajuscule) {
				if(c == character) {
					majusculePresent = true;
	                somme += 4;
	                break;
				}
			}
			
			for (char c : listeChiffre) {
				if(c == character) {
					chiffrePresent = true;
	                somme += 2;
	                break;
				}
			}
			
			for (char c : listeCaractereSpeciaux) {
				if(c == character) {
					speciauxPresent = true;
	                somme += 7;
	                break;
				}
			}
		}
		
		if (speciauxPresent) { nbTypePresent++; }
        if (minusculePresent) { nbTypePresent++; }
        if (majusculePresent) { nbTypePresent++; }
        if (chiffrePresent) { nbTypePresent++; }
        
        switch (nbTypePresent)
        {
            case 1: somme = ((int)(somme * 0.75)); break;
            case 2: somme = ((int)(somme * 1.3)); break;
            case 3: somme = ((int)(somme * 1.7)); break;
            case 4: somme = ((somme * 2)); break;
        }

        if (somme > 100) somme = 100;
        return somme;
	}
	
	
	/**
	 * Genere un mot de passe aléatoire composer de caractères majuscules, minuscules, de chiffres et de caractères spéciaux
	 * @param longueur longueur du mot de passe souhaité, si 0 sera de 12 caractères
	 * @param lettre autorise les lettres minuscules et majuscules dans le mot de passe
	 * @param chiffre autorise les chiffres dans le mot de passe
	 * @param caracSpeciaux autorise les caractères spéciaux dans le mot de passe
	 * @return le mot de passe généré
	 */
	public static String genereMotdePasse(int longueur, boolean lettre, boolean chiffre, boolean caracSpeciaux){
		int length = (longueur == 0) ? 12 : longueur;
        StringBuilder password = new StringBuilder();
        Random rnd = new Random();
        for (int i = 0; i < length; i++)
        {
            boolean caracBienCree = false;
            do
            {
                int typeTab = rnd.nextInt(4);
                switch (typeTab)
                {
                    case 0:
                        if (lettre)
                        {
                            password.append(listeLettreMinuscule[rnd.nextInt(listeLettreMinuscule.length)]);
                            caracBienCree = true;
                        }
                        break;
                    case 1:
                        if (lettre)
                        {
                            password.append(listeLettreMajuscule[rnd.nextInt(listeLettreMajuscule.length)]);
                            caracBienCree = true;
                        }
                        break;
                    case 2:
                        if (chiffre)
                        {
                            password.append(listeChiffre[rnd.nextInt(listeChiffre.length)]);
                            caracBienCree = true;
                        }
                        break;
                    case 3:
                        if (caracSpeciaux)
                        {
                            password.append(listeCaractereSpeciaux[rnd.nextInt(listeCaractereSpeciaux.length)]);
                            caracBienCree = true;
                        }
                        break;
                }
            } while (!caracBienCree);
        }
        return password.toString();
	}
	
	
	
	
	//PARTIE AES
	
	
	/**
	 * @return a new pseudorandom salt of the specified length
	 */
	private static byte[] generateSalt(int length) {
		Random r = new SecureRandom();
		byte[] salt = new byte[length];
		r.nextBytes(salt);
		return salt;
	}

	/**
	 * Derive an AES encryption key and authentication key from given password and salt,
	 * using PBKDF2 key stretching. The authentication key is 64 bits long.
	 * @param keyLength
	 *   length of the AES key in bits (128, 192, or 256)
	 * @param password
	 *   the password from which to derive the keys
	 * @param salt
	 *   the salt from which to derive the keys
	 * @return a Keys object containing the two generated keys
	 */
	private static Keys keygen(int keyLength, char[] password, byte[] salt) {
		SecretKeyFactory factory;
		try {
			factory = SecretKeyFactory.getInstance(KEYGEN_SPEC);
		} catch (NoSuchAlgorithmException impossible) { return null; }
		// derive a longer key, then split into AES key and authentication key
		KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, keyLength + AUTH_KEY_LENGTH * 8);
		SecretKey tmp = null;
		try {
			tmp = factory.generateSecret(spec);
		} catch (InvalidKeySpecException impossible) { }
		byte[] fullKey = tmp.getEncoded();
		SecretKey authKey = new SecretKeySpec( // key for password authentication
				Arrays.copyOfRange(fullKey, 0, AUTH_KEY_LENGTH), "AES");
		SecretKey encKey = new SecretKeySpec( // key for AES encryption
				Arrays.copyOfRange(fullKey, AUTH_KEY_LENGTH, fullKey.length), "AES");
		return new Keys(encKey, authKey);
	}

	/**
	 * Encrypts a stream of data. The encrypted stream consists of a header
	 * followed by the raw AES data. The header is broken down as follows:<br/>
	 * <ul>
	 *   <li><b>keyLength</b>: AES key length in bytes (valid for 16, 24, 32) (1 byte)</li>
	 *   <li><b>salt</b>: pseudorandom salt used to derive keys from password (16 bytes)</li>
	 *   <li><b>authentication key</b> (derived from password and salt, used to
	 *     check validity of password upon decryption) (8 bytes)</li>
	 *   <li><b>IV</b>: pseudorandom AES initialization vector (16 bytes)</li>
	 * </ul>
	 * 
	 * @param keyLength
	 *   key length to use for AES encryption (must be 128, 192, or 256)
	 * @param password
	 *   password to use for encryption
	 * @param input
	 *   an arbitrary byte stream to encrypt
	 * @param output
	 *   stream to which encrypted data will be written
	 * @throws CryptUtils.InvalidKeyLengthException
	 *   if keyLength is not 128, 192, or 256
	 * @throws CryptUtils.StrongEncryptionNotAvailableException
	 *   if keyLength is 192 or 256, but the Java runtime's jurisdiction
	 *   policy files do not allow 192- or 256-bit encryption
	 * @throws IOException
	 */
	public static void encrypt(int keyLength, char[] password, InputStream input, OutputStream output)
			throws InvalidKeyLengthException, StrongEncryptionNotAvailableException, IOException {
		// Check validity of key length
		if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
			throw new InvalidKeyLengthException(keyLength);
		}
		
		// generate salt and derive keys for authentication and encryption
		byte[] salt = generateSalt(SALT_LENGTH);
		Keys keys = keygen(keyLength, password, salt);
		
		// initialize AES encryption
		Cipher encrypt = null;
		try {
			encrypt = Cipher.getInstance(CIPHER_SPEC);
			encrypt.init(Cipher.ENCRYPT_MODE, keys.encryption);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException impossible) { }
		  catch (InvalidKeyException e) { // 192 or 256-bit AES not available
			throw new StrongEncryptionNotAvailableException(keyLength);
		}
		
		// get initialization vector
		byte[] iv = null;
		try {
			iv = encrypt.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		} catch (InvalidParameterSpecException impossible) { }
		
		// write authentication and AES initialization data
		output.write(keyLength / 8);
		output.write(salt);
		output.write(keys.authentication.getEncoded());
		output.write(iv);

		// read data from input into buffer, encrypt and write to output
		byte[] buffer = new byte[BUFFER_SIZE];
		int numRead;
		byte[] encrypted = null;
		while ((numRead = input.read(buffer)) > 0) {
			encrypted = encrypt.update(buffer, 0, numRead);
			if (encrypted != null) {
				output.write(encrypted);
			}
		}
		try { // finish encryption - do final block
			encrypted = encrypt.doFinal();
		} catch (IllegalBlockSizeException | BadPaddingException impossible) { }
		if (encrypted != null) {
			output.write(encrypted);
		}
	}

	/**
	 * Decrypts a stream of data that was encrypted by {@link #encrypt}.
	 * @param password
	 *   the password used to encrypt/decrypt the stream
	 * @param input
	 *   stream of encrypted data to be decrypted
	 * @param output
	 *   stream to which decrypted data will be written
	 * @return the key length for the decrypted stream (128, 192, or 256)
	 * @throws CryptUtils.InvalidPasswordException
	 *   if the given password was not used to encrypt the data
	 * @throws CryptUtils.InvalidAESStreamException
	 *   if the given input stream is not a valid AES-encrypted stream
	 * @throws CryptUtils.StrongEncryptionNotAvailableException
	 *   if the stream is 192 or 256-bit encrypted, and the Java runtime's
	 *   jurisdiction policy files do not allow for AES-192 or 256
	 * @throws IOException
	 */
	public static int decrypt(char[] password, InputStream input, OutputStream output)
			throws InvalidPasswordException, InvalidAESStreamException, IOException,
			StrongEncryptionNotAvailableException {
		int keyLength = input.read() * 8;
		// Check validity of key length
		if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
			throw new InvalidAESStreamException();
		}
		
		// read salt, generate keys, and authenticate password
		byte[] salt = new byte[SALT_LENGTH];
		input.read(salt);
		Keys keys = keygen(keyLength, password, salt);
		byte[] authRead = new byte[AUTH_KEY_LENGTH];
		input.read(authRead);
		if (!Arrays.equals(keys.authentication.getEncoded(), authRead)) {
			throw new InvalidPasswordException();
		}
		
		// initialize AES decryption
		byte[] iv = new byte[16]; // 16-byte I.V. regardless of key size
		input.read(iv);
		Cipher decrypt = null;
		try {
			decrypt = Cipher.getInstance(CIPHER_SPEC);
			decrypt.init(Cipher.DECRYPT_MODE, keys.encryption, new IvParameterSpec(iv));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException impossible) { }
		  catch (InvalidKeyException e) { // 192 or 256-bit AES not available
			throw new StrongEncryptionNotAvailableException(keyLength);
		}
		
		// read data from input into buffer, decrypt and write to output
		byte[] buffer = new byte[BUFFER_SIZE];
		int numRead;
		byte[] decrypted;
		while ((numRead = input.read(buffer)) > 0) {
			decrypted = decrypt.update(buffer, 0, numRead);
			if (decrypted != null) {
				output.write(decrypted);
			}
		}
		try { // finish decryption - do final block
			decrypted = decrypt.doFinal();
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new InvalidAESStreamException(e);
		}
		if (decrypted != null) {
			output.write(decrypted);
		}

		return keyLength;
	}

	/**
	 * A tuple of encryption and authentication keys returned by {@link #keygen}
	 */
	private static class Keys {
		public final SecretKey encryption, authentication;
		public Keys(SecretKey encryption, SecretKey authentication) {
			this.encryption = encryption;
			this.authentication = authentication;
		}
	}
	
	
	//******** EXCEPTIONS thrown by encrypt and decrypt ********
	
	/**
	 * Thrown if an attempt is made to decrypt a stream with an incorrect password.
	 */
	public static class InvalidPasswordException extends Exception { }
	
	/**
	 * Thrown if an attempt is made to encrypt a stream with an invalid AES key length.
	 */
	public static class InvalidKeyLengthException extends Exception {
		InvalidKeyLengthException(int length) {
			super("Invalid AES key length: " + length);
		}
	}
	
	/**
	 * Thrown if 192- or 256-bit AES encryption or decryption is attempted,
	 * but not available on the particular Java platform.
	 */
	public static class StrongEncryptionNotAvailableException extends Exception {
		public StrongEncryptionNotAvailableException(int keySize) {
			super(keySize + "-bit AES encryption is not available on this Java platform.");
		}
	}
	
	/**
	 * Thrown if an attempt is made to decrypt an invalid AES stream.
	 */
	public static class InvalidAESStreamException extends Exception {
		public InvalidAESStreamException() { super(); }

		public InvalidAESStreamException(Exception e) { super(e); }
	}
	
}
