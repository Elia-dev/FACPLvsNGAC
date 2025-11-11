package policyCompare;

import it.unifi.facpl.lib.policy.*;
import it.unifi.facpl.lib.enums.*;
import it.unifi.facpl.lib.util.*;

@SuppressWarnings("all")
public class PolicySet_patientConsent extends PolicySet {
    public PolicySet_patientConsent() {
        addId("patientConsent");
        // Algorithm Combining
        addCombiningAlg(new it.unifi.facpl.lib.algorithm.PermitOverridesGreedy());
        // Target
        addTarget(new ExpressionFunction(
            new it.unifi.facpl.lib.function.comparison.Equal(), 
            "Alice",
            new AttributeName("resource", "patient-id")
        ));
        // PolElements
        addPolicyElement(new PolicySet_ePre());
        // Obligation - none specified in your .fpl
    }

    private class PolicySet_ePre extends PolicySet {
        PolicySet_ePre() {
            addId("ePre");
            // Algorithm Combining
            addCombiningAlg(new it.unifi.facpl.lib.algorithm.PermitOverridesGreedy());
            // Target
            addTarget(new ExpressionFunction(
                new it.unifi.facpl.lib.function.comparison.Equal(), 
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
                        new it.unifi.facpl.lib.function.comparison.Equal(),
                        new AttributeName("subject", "role"), 
                        "doctor"
                    )
                ),
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new it.unifi.facpl.lib.function.comparison.Equal(),
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
                        new it.unifi.facpl.lib.function.comparison.Equal(),
                        new AttributeName("subject", "role"), 
                        "doctor"
                    )
                ),
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new it.unifi.facpl.lib.function.comparison.Equal(),
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
                        new it.unifi.facpl.lib.function.comparison.Equal(),
                        new AttributeName("subject", "role"), 
                        "pharmacist"
                    )
                ),
                new ExpressionBooleanTree(
                    new ExpressionFunction(
                        new it.unifi.facpl.lib.function.comparison.Equal(),
                        new AttributeName("action", "id"), 
                        "read"
                    )
                )
            ));
            // Obligations
        }
    }
}