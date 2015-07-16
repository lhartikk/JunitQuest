package org.tsers.junitquest;


import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

public class BranchCoverage {

    private final List<String> targetLabels;
    private final List<String> visitedLabels;


    public BranchCoverage(MethodNode method) {
        this.targetLabels = new ArrayList<>();
        this.visitedLabels = new ArrayList<>();
        AbstractInsnNode instructions[] = method.instructions.toArray();
        for (AbstractInsnNode current : instructions) {

            if (current instanceof LabelNode) {
                targetLabels.add(((LabelNode) current).getLabel().toString());
            }
        }

        int lastOpcode = instructions[instructions.length - 1].getOpcode();
        if (lastOpcode == Opcodes.IRETURN || lastOpcode == Opcodes.RETURN
                || lastOpcode == Opcodes.ARETURN || lastOpcode == Opcodes.DRETURN || lastOpcode == Opcodes.LRETURN) {

        } else {
            targetLabels.remove(targetLabels.size() - 1);
        }
    }

    public boolean visitLabel(String label) {
        if (!targetLabels.contains(label)) {
            return false;
        }
        if (visitedLabels.contains(label)) {
            return false;
        } else {
            visitedLabels.add(label);
            return true;
        }
    }

    public boolean visitLabels(List<String> labels) {
        return labels.stream()
                .map(l -> visitLabel(l))
                .reduce(false, (a, b) -> a || b);
    }

    public List<String> getTargetLabels() {
        return targetLabels;
    }

    public boolean isAllVisited() {
        for (String label : targetLabels) {
            if (!visitedLabels.contains(label)) {
                return false;
            }
        }
        return true;
    }

}