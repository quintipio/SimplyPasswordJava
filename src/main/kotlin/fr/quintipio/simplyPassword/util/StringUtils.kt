package fr.quintipio.simplyPassword.util

object StringUtils {

    fun isEmpty(chaine : String?) : Boolean {
        return chaine?.trim(' ')?.isEmpty() ?: true
    }
}