package policyCompare;

import gov.nist.csd.pm.core.common.exception.PMException;
import gov.nist.csd.pm.core.common.graph.relationship.AccessRightSet;
import gov.nist.csd.pm.core.pap.PAP;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphPolicyLoader {

    private final PAP pap;
    private final Map<String, Long> nodeIds = new HashMap<>();

    public GraphPolicyLoader(PAP pap) {
        this.pap = pap;
    }

    public void loadFromFile(Path graphFile) throws IOException, PMException {
        try (BufferedReader reader = new BufferedReader(new FileReader(graphFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }
                parseLine(line);
            }
        }
    }

    private void parseLine(String line) throws PMException {
        if (line.startsWith("policy_class(")) {
            String name = extractName(line);
            long id = pap.modify().graph().createPolicyClass(name);
            nodeIds.put(name, id);
        } else if (line.startsWith("user_attribute(")) {
            String name = extractName(line);
            long pcId = getPolicyClassId();
            long id = pap.modify().graph().createUserAttribute(name, List.of(pcId));
            nodeIds.put(name, id);
        } else if (line.startsWith("object_attribute(")) {
            String name = extractName(line);
            long pcId = getPolicyClassId();
            long id = pap.modify().graph().createObjectAttribute(name, List.of(pcId));
            nodeIds.put(name, id);
        } else if (line.startsWith("operation(")) {
            String opName = extractName(line);
            AccessRightSet ops = pap.query().operations().getResourceOperations();
            ops.add(opName);
            pap.modify().operations().setResourceOperations(ops);
        } else if (line.startsWith("associate(")) {
            parseAssociation(line);
        }
    }

    private String extractName(String line) {
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Cannot parse name from: " + line);
    }

    private void parseAssociation(String line) throws PMException {
        // Format: associate(Source, {right1,right2}, Target)
        Pattern pattern = Pattern.compile("associate\\(([^,]+),\\s*\\{([^}]+)\\},\\s*([^)]+)\\)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String source = matcher.group(1).trim();
            String rightsStr = matcher.group(2).trim();
            String target = matcher.group(3).trim();

            String[] rights = rightsStr.split(",");
            AccessRightSet rightSet = new AccessRightSet();
            for (String r : rights) {
                rightSet.add(r.trim());
            }

            long sourceId = nodeIds.get(source);
            long targetId = nodeIds.get(target);
            pap.modify().graph().associate(sourceId, targetId, rightSet);
        }
    }

    private long getPolicyClassId() {
        return nodeIds.values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No policy class found"));
    }
    
    public long getNodeId(String name) {
		return nodeIds.get(name);
	}
}
