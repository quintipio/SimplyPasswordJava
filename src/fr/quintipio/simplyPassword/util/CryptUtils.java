package fr.quintipio.simplyPassword.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CryptUtils {

		private static char[] listeLettreMinuscule = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k'
        , 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        private static char[] listeLettreMajuscule = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K'
        , 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        private static char[] listeChiffre = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        private static char[] listeCaractereSpeciaux = { '²', '&', 'é', '"', '#', '\'', '{', '-', '|', 'è', '_'
        , '\\', 'ç', 'à', '@', ')', '(', '[', ']', '=', '+', '}', '£', '$', '¤', '%', 'ù', 'µ', '*', '?', ',', '.', ';'
        , '/', ':', '§', '!', '€', '>', '<'};
	
	/**
	 * Calcul la force approximative d'un mot de passe
	 * @param motDePasse le mot de passe à calculer
	 * @return
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
        String password = "";
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
                            password += listeLettreMinuscule[rnd.nextInt(listeLettreMinuscule.length)];
                            caracBienCree = true;
                        }
                        break;
                    case 1:
                        if (lettre)
                        {
                            password += listeLettreMajuscule[rnd.nextInt(listeLettreMajuscule.length)];
                            caracBienCree = true;
                        }
                        break;
                    case 2:
                        if (chiffre)
                        {
                            password += listeChiffre[rnd.nextInt(listeChiffre.length)];
                            caracBienCree = true;
                        }
                        break;
                    case 3:
                        if (caracSpeciaux)
                        {
                            password += listeCaractereSpeciaux[rnd.nextInt(listeCaractereSpeciaux.length)];
                            caracBienCree = true;
                        }
                        break;
                }
            } while (!caracBienCree);
        }
        return password;
	}
}
