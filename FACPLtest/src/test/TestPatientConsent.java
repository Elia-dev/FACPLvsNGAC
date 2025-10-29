package test;

import it.unifi.facpl.lib.context.Request;
import it.unifi.facpl.lib.policy.PolicySet;
import it.unifi.facpl.system.PDP;
import it.unifi.facpl.lib.enums.StandardDecision;
import it.unifi.facpl.lib.context.AuthorisationPDP;

public class TestPatientConsent {

    public static void main(String[] args) {
        try {
            // Creiamo una PolicySet vuota (oppure la caricheremo dal parser se disponibile)
            PolicySet policySet = new PolicySet();

            // Inizializziamo il PDP
            PDP pdp = new PDP(policySet);

            // Costruiamo una Request
            Request request = new Request();
            request.addSubjectAttribute("role", "doctor");
            request.addActionAttribute("id", "read");
            request.addResourceAttribute("patient-id", "Alice");
            request.addResourceAttribute("type", "e-Prescription");

            // Eseguiamo l’autorizzazione
            AuthorisationPDP response = pdp.evaluate(request);

            System.out.println("Decisione: " + response.getDecision());
            System.out.println("Obblighi: " + response.getObligations());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
