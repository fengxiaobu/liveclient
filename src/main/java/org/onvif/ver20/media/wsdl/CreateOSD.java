//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.05 um 06:25:30 PM CET 
//


package org.onvif.ver20.media.wsdl;

import org.onvif.ver10.schema.OSDConfiguration;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse f�r anonymous complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OSD" type="{http://www.onvif.org/ver10/schema}OSDConfiguration"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "osd"
})
@XmlRootElement(name = "CreateOSD")
public class CreateOSD {

    @XmlElement(name = "OSD", required = true)
    protected OSDConfiguration osd;

    /**
     * Ruft den Wert der osd-Eigenschaft ab.
     *
     * @return possible object is
     * {@link OSDConfiguration }
     */
    public OSDConfiguration getOSD() {
        return osd;
    }

    /**
     * Legt den Wert der osd-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link OSDConfiguration }
     */
    public void setOSD(OSDConfiguration value) {
        this.osd = value;
    }

}
