package fr.quintipio.simplyPassword.com

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption

class ComFile(path: String) {

    /**
     * Le fichier utilisé
     */
    var file: File
        private set

    /**
     * Lit le fichier et le retourne en String
     * @return le contenu du fichier
     */
    fun readFileToString(): String {
        return try {
            if (file.exists()) String(Files.readAllBytes(file.toPath())) else ""
        } catch (ex: Exception) {
            ""
        }

    }

    /**
     * Lit le fichier et le retourne en byte[]
     * @return le contenu du fichier
     */
    fun readFileToByteArray(): ByteArray? {
        return try {
            if (file.exists()) Files.readAllBytes(file.toPath()) else null
        } catch (ex: Exception) {
            null
        }

    }

    /**
     * Ecrit une chaine de caractère dans un fichier
     * @param data les données à écrire
     * @param overwrite true, écrasera le fichier, false écrira à la suite
     */
    fun writeFile(data: String, overwrite: Boolean = true) {
        try {
            Files.write(file.toPath(), data.toByteArray(), if (overwrite) StandardOpenOption.CREATE else StandardOpenOption.APPEND)
        } catch (ex: Exception) {

        }

    }

    /**
     * Ecrit un byte[] de caractère dans un fichier
     * @param data les données à écrire
     * @param overwrite true, écrasera le fichier, false écrira à la suite
     */
    fun writeFile(data: ByteArray, overwrite: Boolean = true) {
        try {
            Files.write(file.toPath(), data, if (overwrite) StandardOpenOption.CREATE else StandardOpenOption.APPEND)
        } catch (ex: Exception) {

        }
    }

    init {
        this.file = File(path)
    }
}