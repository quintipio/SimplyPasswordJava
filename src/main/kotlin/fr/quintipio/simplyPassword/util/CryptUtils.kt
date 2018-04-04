package fr.quintipio.simplyPassword.util

import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import java.security.spec.InvalidParameterSpecException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.NoSuchPaddingException
import javax.crypto.Cipher
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidAlgorithmParameterException
import java.util.Arrays

object CryptUtils {

    //POUR AES
    private const val CIPHER_SPEC = "AES/CBC/PKCS5Padding"
    private const val KEYGEN_SPEC = "PBKDF2WithHmacSHA1"
    private const val SALT_LENGTH = 16
    private const val AUTH_KEY_LENGTH = 8
    private const val ITERATIONS = 32768
    private const val BUFFER_SIZE = 1024


    private val listeLettreMinuscule = charArrayOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z')
    private val listeLettreMajuscule = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
    private val listeChiffre = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    private val listeCaractereSpeciaux = charArrayOf('²', '&', 'é', '"', '#', '\'', '{', '-', '|', 'è', '_', '\\', 'ç', 'à', '@', ')', '(', '[', ']', '=', '+', '}', '£', '$', '¤', '%', 'ù', 'µ', '*', '?', ',', '.', ';', '/', ':', '§', '!', '€', '>', '<')


    fun calculerForceMotDePasse(motDePasse : String) : Int {
        var somme = 0
        var nbTypePresent = 0
        var minusculePresent = false
        var majusculePresent = false
        var chiffrePresent = false
        var speciauxPresent = false

        for (character in motDePasse.toCharArray()) {
            for (c in listeLettreMinuscule) {
                if (c == character) {
                    minusculePresent = true
                    somme += 4
                    break
                }
            }

            for (c in listeLettreMajuscule) {
                if (c == character) {
                    majusculePresent = true
                    somme += 4
                    break
                }
            }

            for (c in listeChiffre) {
                if (c == character) {
                    chiffrePresent = true
                    somme += 2
                    break
                }
            }

            for (c in listeCaractereSpeciaux) {
                if (c == character) {
                    speciauxPresent = true
                    somme += 7
                    break
                }
            }
        }

        if (speciauxPresent) { nbTypePresent++; }
        if (minusculePresent) { nbTypePresent++; }
        if (majusculePresent) { nbTypePresent++; }
        if (chiffrePresent) { nbTypePresent++; }

        when (nbTypePresent) {
            1 -> somme = (somme * 0.75).toInt()
            2 -> somme = (somme * 1.3).toInt()
            3 -> somme = (somme * 1.7).toInt()
            4 -> somme *= 2
        }

        return if(somme > 100) 100 else somme
    }

    fun genereMotdePasse(longueur : Int, lettre : Boolean = true, chiffre : Boolean = true, caracSpeciaux : Boolean = true)  :String {
        val length = if(longueur == 0) 12 else longueur
        val password = StringBuilder()
        val random = Random()

        for (i in 0 until length) {
            var caracBienCree = false
            do {
                val typeTab = random.nextInt(4)
                when (typeTab) {
                    0 -> if (lettre) {
                        password.append(listeLettreMinuscule[random.nextInt(listeLettreMinuscule.size)])
                        caracBienCree = true
                    }
                    1 -> if (lettre) {
                        password.append(listeLettreMajuscule[random.nextInt(listeLettreMajuscule.size)])
                        caracBienCree = true
                    }
                    2 -> if (chiffre) {
                        password.append(listeChiffre[random.nextInt(listeChiffre.size)])
                        caracBienCree = true
                    }
                    3 -> if (caracSpeciaux) {
                        password.append(listeCaractereSpeciaux[random.nextInt(listeCaractereSpeciaux.size)])
                        caracBienCree = true
                    }
                }
            } while (!caracBienCree)
        }
        return password.toString()
    }

    private fun generateSalt(length: Int): ByteArray {
        val r = SecureRandom()
        val salt = ByteArray(length)
        r.nextBytes(salt)
        return salt
    }

    private fun keygen(keyLength: Int, password: CharArray, salt: ByteArray): Keys? {
        val factory: SecretKeyFactory
        try {
            factory = SecretKeyFactory.getInstance(KEYGEN_SPEC)
        } catch (impossible: NoSuchAlgorithmException) {
            return null
        }

        // derive a longer key, then split into AES key and authentication key
        val spec = PBEKeySpec(password, salt, ITERATIONS, keyLength + AUTH_KEY_LENGTH * 8)
        var tmp: SecretKey? = null
        try {
            tmp = factory.generateSecret(spec)
        } catch (impossible: InvalidKeySpecException) {
        }

        val fullKey = tmp!!.encoded
        val authKey = SecretKeySpec( // key for password authentication
                Arrays.copyOfRange(fullKey, 0, AUTH_KEY_LENGTH), "AES")
        val encKey = SecretKeySpec( // key for AES encryption
                Arrays.copyOfRange(fullKey, AUTH_KEY_LENGTH, fullKey.size), "AES")
        return Keys(encKey, authKey)
    }


    @Throws(InvalidKeyLengthException::class, StrongEncryptionNotAvailableException::class, IOException::class)
    fun encrypt(keyLength: Int, password: CharArray, input: InputStream, output: OutputStream) {

        if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
            throw InvalidKeyLengthException(keyLength)
        }

        val salt = generateSalt(SALT_LENGTH)
        val keys = keygen(keyLength, password, salt)

        var encrypt: Cipher? = null
        try {
            encrypt = Cipher.getInstance(CIPHER_SPEC)
            encrypt.init(Cipher.ENCRYPT_MODE, keys!!.encryption)
        } catch (impossible: NoSuchAlgorithmException) {
        } catch (impossible: NoSuchPaddingException) {
        } catch (e: InvalidKeyLengthException) { // 192 or 256-bit AES not available
            throw StrongEncryptionNotAvailableException(keyLength)
        }

        // get initialization vector
        var iv: ByteArray? = null
        try {
            iv = encrypt!!.parameters.getParameterSpec(IvParameterSpec::class.java).iv
        } catch (impossible: InvalidParameterSpecException) {
        }

        // write authentication and AES initialization data
        output.write(keyLength / 8)
        output.write(salt)
        output.write(keys!!.authentication.encoded)
        output.write(iv)

        // read data from input into buffer, encrypt and write to output
        val buffer = ByteArray(BUFFER_SIZE)
        var numRead :Int
        var encrypted: ByteArray? = null
        do{
            numRead = input.read(buffer)
            if(numRead > 0) {
                encrypted = encrypt!!.update(buffer, 0, numRead)
                if (encrypted != null) {
                    output.write(encrypted)
                }
            }
        }while (numRead > 0)
        try { // finish encryption - do final block
            encrypted = encrypt!!.doFinal()
        } catch (impossible: IllegalBlockSizeException) {
        } catch (impossible: BadPaddingException) {
        }

        if (encrypted != null) {
            output.write(encrypted)
        }
    }

    @Throws(InvalidPasswordException::class, InvalidAESStreamException::class, IOException::class, StrongEncryptionNotAvailableException::class)
    fun decrypt(password: CharArray, input: InputStream, output: OutputStream): Int {

        val keyLength = input.read() * 8

        if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
            throw InvalidAESStreamException()
        }

        val salt = ByteArray(SALT_LENGTH)
        input.read(salt)
        val keys = keygen(keyLength, password, salt)
        val authRead = ByteArray(AUTH_KEY_LENGTH)
        input.read(authRead)
        if (!Arrays.equals(keys!!.authentication.encoded, authRead)) {
            throw InvalidPasswordException()
        }

        // initialize AES decryption
        val iv = ByteArray(16) // 16-byte I.V. regardless of key size
        input.read(iv)
        var decrypt: Cipher? = null
        try {
            decrypt = Cipher.getInstance(CIPHER_SPEC)
            decrypt!!.init(Cipher.DECRYPT_MODE, keys.encryption, IvParameterSpec(iv))
        } catch (impossible: NoSuchAlgorithmException) {
        } catch (impossible: NoSuchPaddingException) {
        } catch (impossible: InvalidAlgorithmParameterException) {
        } catch (e: InvalidKeyLengthException) { // 192 or 256-bit AES not available
            throw StrongEncryptionNotAvailableException(keyLength)
        }

        // read data from input into buffer, decrypt and write to output
        val buffer = ByteArray(BUFFER_SIZE)
        var numRead: Int
        var decrypted: ByteArray?
        do{
            numRead = input.read(buffer)
            if(numRead > 0) {
                decrypted = decrypt!!.update(buffer, 0, numRead)
                if (decrypted != null) {
                    output.write(decrypted)
                }
            }
            
        }while (numRead > 0)
        try { // finish decryption - do final block
            decrypted = decrypt!!.doFinal()
        } catch (e: IllegalBlockSizeException) {
            throw InvalidAESStreamException(e)
        } catch (e: BadPaddingException) {
            throw InvalidAESStreamException(e)
        }

        if (decrypted != null) {
            output.write(decrypted)
        }

        return keyLength
    }
}

/**
 * A tuple of encryption and authentication keys returned by [.keygen]
 */
private class Keys(val encryption: SecretKey, val authentication: SecretKey)


//******** EXCEPTIONS thrown by encrypt and decrypt ********

/**
 * Thrown if an attempt is made to decrypt a stream with an incorrect password.
 */
class InvalidPasswordException : Exception()

/**
 * Thrown if an attempt is made to encrypt a stream with an invalid AES key length.
 */
class InvalidKeyLengthException internal constructor(length: Int) : Exception("Invalid AES key length: $length")

/**
 * Thrown if 192- or 256-bit AES encryption or decryption is attempted,
 * but not available on the particular Java platform.
 */
class StrongEncryptionNotAvailableException(keySize: Int) : Exception(keySize.toString() + "-bit AES encryption is not available on this Java platform.")

/**
 * Thrown if an attempt is made to decrypt an invalid AES stream.
 */
class InvalidAESStreamException : Exception {
    constructor() : super()
    constructor(e: Exception) : super(e)
}