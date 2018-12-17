//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.5-2 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.02.19 um 02:35:56 PM CET 
//

package org.onvif.ver10.media.wsdl;

import org.onvif.ver10.schema.VideoSourceConfiguration;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java-Klasse f�r anonymous complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 *
 * <pre>
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Configurations" type="{http://www.onvif.org/ver10/schema}VideoSourceConfiguration" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"configurations"})
@XmlRootElement(name = "GetCompatibleVideoSourceConfigurationsResponse")
public class GetCompatibleVideoSourceConfigurationsResponse {

    @XmlElement(name = "Configurations")
    protected List<VideoSourceConfiguration> configurations;

    /**
     * Gets the value of the configurations property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the configurations property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getConfigurations().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link VideoSourceConfiguration }
     */
    public List<VideoSourceConfiguration> getConfigurations() {
        if (configurations == null) {
            configurations = new ArrayList<VideoSourceConfiguration>();
        }
        return this.configurations;
    }

}