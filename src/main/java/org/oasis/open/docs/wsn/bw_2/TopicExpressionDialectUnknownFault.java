package org.oasis.open.docs.wsn.bw_2;

import org.oasis.open.docs.wsn.b_2.TopicExpressionDialectUnknownFaultType;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 */
@WebFault(name = "TopicExpressionDialectUnknownFault", targetNamespace = "http://docs.oasis-open.org/wsn/b-2")
public class TopicExpressionDialectUnknownFault
        extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private TopicExpressionDialectUnknownFaultType faultInfo;

    /**
     * @param faultInfo
     * @param message
     */
    public TopicExpressionDialectUnknownFault(String message, TopicExpressionDialectUnknownFaultType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * @param faultInfo
     * @param cause
     * @param message
     */
    public TopicExpressionDialectUnknownFault(String message, TopicExpressionDialectUnknownFaultType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * @return returns fault bean: org.oasis_open.docs.wsn.b_2.TopicExpressionDialectUnknownFaultType
     */
    public TopicExpressionDialectUnknownFaultType getFaultInfo() {
        return faultInfo;
    }

}
