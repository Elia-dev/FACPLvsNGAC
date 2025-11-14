package policyCompare;

import it.unifi.facpl.lib.policy.*;
import it.unifi.facpl.lib.enums.*;
import it.unifi.facpl.lib.util.*;
import it.unifi.facpl.lib.function.comparison.*;
import it.unifi.facpl.lib.algorithm.*;

@SuppressWarnings("all")
public class PolicySet_patientConsent extends PolicySet {
    public PolicySet_patientConsent() {
        addId("patientConsent");
        // Algorithm Combining
        addCombiningAlg(new PermitOverridesGreedy());
        // Target
        addTarget(new ExpressionFunction(
            new Equal(), 
            "Alice",
            new AttributeName("resource", "patient-id")
        ));
        // PolElements
        addPolicyElement(new PolicySet_ePre());
        addPolicyElement(new PolicySet_denyAll());
        
     // Obligations
        addObligation(new Obligation("compress",Effect.PERMIT,ObligationType.O,null));
        addObligation(new Obligation("mailTo",Effect.DENY,ObligationType.M,new AttributeName("resource","patient-id.mail"),"Data requested by unauthorized subject"));
    }

    private class PolicySet_ePre extends PolicySet {
        PolicySet_ePre() {
            addId("ePre");
            // Algorithm Combining
            addCombiningAlg(new PermitOverridesGreedy());
            // Target
            addTarget(new ExpressionFunction(
                new Equal(), 
                "e-Prescription",
                new AttributeName("resource", "type")
            ));
            // PolElements
            addPolicyElement(new Rule_readDoc());
            addPolicyElement(new Rule_writeDoc());
            addPolicyElement(new Rule_readPha());
        }
    }

    private class Rule_readDoc extends Rule {
        Rule_readDoc() {
            addId("readDoc");
            // Effect
            addEffect(Effect.PERMIT);
            // Target
            addTarget(new ExpressionBooleanTree(
                ExprBooleanConnector.AND,
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new Equal(),
                        new AttributeName("subject", "role"), 
                        "doctor"
                    )
                ),
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new Equal(),
                        new AttributeName("action", "id"), 
                        "read"
                    )
                )
            ));
            // Obligations
        }
    }

    private class Rule_writeDoc extends Rule {
        Rule_writeDoc() {
            addId("writeDoc");
            // Effect
            addEffect(Effect.PERMIT);
            // Target
            addTarget(new ExpressionBooleanTree(
                ExprBooleanConnector.AND,
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new Equal(),
                        new AttributeName("subject", "role"), 
                        "doctor"
                    )
                ),
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new Equal(),
                        new AttributeName("action", "id"), 
                        "write"
                    )
                )
            ));
            // Obligations
        }
    }

    private class Rule_readPha extends Rule {
        Rule_readPha() {
            addId("readPha");
            // Effect
            addEffect(Effect.PERMIT);
            // Target
            addTarget(new ExpressionBooleanTree(
                ExprBooleanConnector.AND,
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new Equal(),
                        new AttributeName("subject", "role"), 
                        "pharmacist"
                    )
                ),
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new Equal(),
                        new AttributeName("action", "id"), 
                        "read"
                    )
                )
            ));
            // Obligations
        }
    }
    
    private class PolicySet_denyAll extends PolicySet {
        PolicySet_denyAll() {
            addId("denyAll");
            addCombiningAlg(new it.unifi.facpl.lib.algorithm.DenyOverridesGreedy());
            addPolicyElement(new Rule_deny());
        }

        private class Rule_deny extends Rule {
            Rule_deny() {
                addId("denyRule");
                addEffect(Effect.DENY);
                // no target => applies when others are NotApplicable
            }
        }
    }
}
