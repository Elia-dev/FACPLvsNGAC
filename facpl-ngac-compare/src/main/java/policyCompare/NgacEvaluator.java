package policyCompare;

import gov.nist.ngac.model.*;
import gov.nist.ngac.pdp.*;

public class NgacEvaluator {

    public static boolean evaluate(String role, String action, String type) {
        // Mapping: role -> UA, action -> OP, type -> OA
        boolean doctor = role.equals("doctor");
        boolean pharmacist = role.equals("pharmacist");
        boolean ePrescription = type.equals("e-Prescription");

        if (ePrescription) {
            if (doctor && (action.equals("read") || action.equals("write"))) return true;
            if (pharmacist && action.equals("read")) return true;
        }

        return false;
    }
}