package fr.quintipio.simplypassword.model


import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty

/**
 * L'objet Mot de passe pour être observable dans la vue
 */
data class ObservableMotDePasse(var mdpOri: MotDePasse) {

     var titre: StringProperty = SimpleStringProperty(mdpOri.titre)

     var login: StringProperty = SimpleStringProperty(mdpOri.login)

     var motDePasseObjet: StringProperty = SimpleStringProperty(mdpOri.motDePasseObjet)

    var commentaire: StringProperty = SimpleStringProperty(mdpOri.commentaire)

    var siteWeb: StringProperty = SimpleStringProperty(mdpOri.siteWeb)

    var dossierPossesseur: Dossier? = mdpOri.dossierPossesseur


    fun getTitre(): String? {
        return titre.get()
    }

    fun getLogin(): String? {
        return login.get()
    }

    fun setMotDePasseObjet(motDePasseObjet: String) {
        this.motDePasseObjet.set(motDePasseObjet)
    }

}
