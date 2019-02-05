package fr.quintipio.simplypassword.model

import java.io.Serializable
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlTransient

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
class MotDePasse(var titre: String = "",
                 var login: String = "",
                 var motDePasseObjet: String = "",
                 var commentaire: String = "",
                 var siteWeb: String = "",
                 var idIcone: Int = 0
                 ) : Serializable  {
    @XmlTransient
    var dossierPossesseur: Dossier? = null

}
