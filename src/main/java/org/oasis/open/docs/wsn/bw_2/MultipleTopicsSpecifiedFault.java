package org.oasis.open.docs.wsn.bw_2;

import org.oasis.open.docs.wsn.b_2.MultipleTopicsSpecifiedFaultType;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 */
@WebFault(name = "MultipleTopicsSpecifiedFault", targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
public class MultipleTopicsSpecifiedFault
        extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private MultipleTopicsSpecifiedFaultType faultInfo;

    /**
     * @param faultInfo
     * @param message
     */
    public MultipleTopicsSpecifiedFault(String message, MultipleTopicsSpecifiedFaultType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * @param faultInfo
     * @param cause
     * @param message
     */
    public MultipleTopicsSpecifiedFault(String message, MultipleTopicsSpecifiedFaultType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * @return returns fault bean: org.oasis_open.docs.wsn.b_2.MultipleTopicsSpecifiedFaultType
     */
    public MultipleTopicsSpecifiedFaultType getFaultInfo() {
        return faultInfo;
    }

}
