package policyCompare;

import it.unifi.facpl.*;
import it.unifi.facpl.core.*;
import it.unifi.facpl.model.*;

public class FacplEvaluator {

    public static Decision evaluate(String role, String action, String type, String patientId) throws Exception {
        PDP pdp = FACPL.createPDP("src/main/resources/facpl/patientConsent.facpl");

        Request req = new Request();
        req.addAttribute("subject/role", role);
        req.addAttribute("action/id", action);
        req.addAttribute("resource/type", type);
        req.addAttribute("resource/patient-id", patientId);
        
        Response r = pdp.evaluate(req);
        return r.getDecision();
    }
}