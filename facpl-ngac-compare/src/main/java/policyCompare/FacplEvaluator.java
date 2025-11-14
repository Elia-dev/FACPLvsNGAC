package policyCompare;

import it.unifi.facpl.lib.policy.*;
import it.unifi.facpl.system.*;
import it.unifi.facpl.lib.context.*;
import it.unifi.facpl.lib.interfaces.*;
import it.unifi.facpl.lib.enums.*;
import it.unifi.facpl.lib.util.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import it.unifi.facpl.lib.algorithm.*;

public class FacplEvaluator {
    
    private PDP pdp;
    private PEP pep;
    
    public FacplEvaluator(IEvaluablePolicy policy) {
        // Create policy list
        LinkedList<IEvaluablePolicy> policies = new LinkedList<IEvaluablePolicy>();
        policies.add(policy);
        
        // Initialize PDP with combining algorithm
        this.pdp = new PDP(new PermitUnlessDenyGreedy(), policies, false);
        
        // Initialize PEP
        this.pep = new PEP(EnforcementAlgorithm.DENY_BIASED);
    }
    
    /**
     * Evaluate access request
     * @param subjectId Subject identifier (e.g., "Doctor", "Pharmacist")
     * @param resourceId Resource identifier (e.g., "ePrescription")
     * @param actionId Action identifier (e.g., "read", "write")
     * @param additionalAttributes Additional attributes as key-value pairs
     * @return true if access is permitted, false otherwise
     */
    public ExtendedDecision isAllowed(String subjectId, String resourceId, String actionId, 
                            HashMap<String, Object> additionalAttributes) {
        // Create request
        Request req = new Request("AccessRequest");
        
        // Create attribute maps for each category
        HashMap<String, Object> subjectAttrs = new HashMap<String, Object>();
        subjectAttrs.put("id", subjectId);
        
        HashMap<String, Object> resourceAttrs = new HashMap<String, Object>();
        resourceAttrs.put("id", resourceId);
        
        HashMap<String, Object> actionAttrs = new HashMap<String, Object>();
        actionAttrs.put("id", actionId);
        
        // Add additional attributes if provided
        if (additionalAttributes != null) {
            for (String key : additionalAttributes.keySet()) {
                String[] parts = key.split("/");
                if (parts.length == 2) {
                    String category = parts[0];
                    String attrName = parts[1];
                    Object value = additionalAttributes.get(key);
                    
                    if (category.equals("subject")) {
                        subjectAttrs.put(attrName, value);
                    } else if (category.equals("resource")) {
                        resourceAttrs.put(attrName, value);
                    } else if (category.equals("action")) {
                        actionAttrs.put(attrName, value);
                    }
                }
            }
        }
        
        // Add attributes to request
        req.addAttribute("subject", subjectAttrs);
        req.addAttribute("resource", resourceAttrs);
        req.addAttribute("action", actionAttrs);
        
        // Create context request
        ContextRequest contextReq = new ContextRequest(req, ContextStub_Default.getInstance());
        
        // Evaluate
        AuthorisationPDP resPDP = pdp.doAuthorisation(contextReq);
        
        // Check decision
        //return resPDP.getDecision() == ExtendedDecision.PERMIT;
        return resPDP.getDecision();
    }
    
    public ExtendedDecision isAllowed(String subjectId, String resourceId, String actionId) {
        return isAllowed(subjectId, resourceId, actionId, null);
    }
    
    /**
     * Example main method similar to NgacEvaluator
     */
    public static void main(String[] args) throws IOException {        
        FacplEvaluator evaluator = new FacplEvaluator(new PolicySet_patientConsent());
        
        HashMap<String, Object> doctorAttrs = new HashMap<String, Object>();
        doctorAttrs.put("subject/role", "doctor");
        doctorAttrs.put("subject/permission", "ePre-Access");
        doctorAttrs.put("resource/type", "ePrescription");
        doctorAttrs.put("resource/patient-id", "Alice");
        
        HashMap<String, Object> pharmacistAttrs = new HashMap<String, Object>();
        pharmacistAttrs.put("subject/role", "pharmacist");
        pharmacistAttrs.put("subject/permission", "e-Dis-Read");
        pharmacistAttrs.put("resource/type", "e-Dispensation");
        pharmacistAttrs.put("resource/patient-id", "Bob");
        
        /*
        System.out.println("doctor READ prescription: " + 
        		(evaluator.isAllowed("Doctor", "ePrescription", "read", doctorAttrs) ? "allow" : "deny"));
        System.out.println("doctor WRITE prescription: " + 
        		(evaluator.isAllowed("Doctor", "ePrescription", "write", doctorAttrs) ? "allow" : "deny"));
        
        System.out.println("pharmacist READ prescription: " + 
        		(evaluator.isAllowed("Pharmacist", "ePrescription", "read", pharmacistAttrs) ? "allow" : "deny"));
        System.out.println("pharmacist WRITE prescription: " + 
        		(evaluator.isAllowed("Pharmacist", "ePrescription", "write", pharmacistAttrs) ? "allow" : "deny"));*/
        System.out.println("doctor READ prescription: " + 
        		(evaluator.isAllowed("Doctor", "ePrescription", "read", doctorAttrs).toString()));
        System.out.println("doctor WRITE prescription: " + 
        		(evaluator.isAllowed("Doctor", "ePrescription", "write", doctorAttrs).toString()));
        
        System.out.println("pharmacist READ prescription: " + 
        		(evaluator.isAllowed("Pharmacist", "ePrescription", "read", pharmacistAttrs).toString()));
        System.out.println("pharmacist WRITE prescription: " + 
        		(evaluator.isAllowed("Pharmacist", "ePrescription", "write", pharmacistAttrs).toString()));
    }
    
    public PDP getPdp() {
        return pdp;
    }
    
    public PEP getPep() {
        return pep;
    }
    
}
