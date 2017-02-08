package fr.quintipio.simplyPassword.com;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

/**
 * Classe pour gérer un fichier
 */
public class ComFile {

    /**
     * Le fichier utilisé
     */
    private File file;

    /**
     * Getter du fichier
     * @return le fichier
     */
    public File getFile() {
        return file;
    }

    /**
     * Constructeur du fichier
     * @param path le chemin du fichier
     */
    public ComFile(String path) {
        this.file = new File(path);
    }

    /**
     * Lit le fichier et le retourne en String
     * @return le contenu du fichier
     */
    public String readFileToString() {
        try {
            return (file.exists())?new String(Files.readAllBytes(file.toPath())):null;
        }
        catch(Exception ex) {
            return null;
        }
    }

    /**
     * Lit le fichier et le retourne en byte[]
     * @return le contenu du fichier
     */
    public byte[] readFileToByteArray() {
        try {
            return (file.exists())?Files.readAllBytes(file.toPath()):null;
        }catch(Exception ex) {
            return null;
        }
    }

    /**
     * Ecrit une chaine de caractère dans un fichier
     * @param data les données à écrire
     * @param overwrite true, écrasera le fichier, false écrira à la suite
     */
    public void writeFile(String data,boolean overwrite) {
        try {
            Files.write(file.toPath(),data.getBytes(), (overwrite)?StandardOpenOption.CREATE:StandardOpenOption.APPEND);
        }catch(Exception ex) {

        }
    }

    /**
     * Ecrit un byte[] de caractère dans un fichier
     * @param data les données à écrire
     * @param overwrite true, écrasera le fichier, false écrira à la suite
     */
    public void writeFile(byte[] data,boolean overwrite) {
        try {
            Files.write(file.toPath(),data, (overwrite)?StandardOpenOption.CREATE:StandardOpenOption.APPEND);
        }catch(Exception ex) {

        }
    }
}
