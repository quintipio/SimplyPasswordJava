package fr.quintipio.simplypassword.model


import java.io.Serializable
import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlRootElement
import javax.xml.bind.annotation.XmlTransient

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
data class Dossier (var titre: String = "",
                   var listeMotDePasse: MutableList<MotDePasse> = mutableListOf(),
                   var sousDossier: MutableList<Dossier> = mutableListOf(),
                   var idIcone: Int = 0) : Serializable {

    @XmlTransient
    var dossierParent: Dossier? = null

    override fun toString(): String {
        return titre
    }

}
