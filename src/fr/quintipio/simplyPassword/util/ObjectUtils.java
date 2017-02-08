package fr.quintipio.simplyPassword.util;

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
}
