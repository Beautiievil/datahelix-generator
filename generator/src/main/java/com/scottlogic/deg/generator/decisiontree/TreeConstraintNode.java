package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.restrictions.RowSpec;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class TreeConstraintNode implements ConstraintNode {
    public static ConstraintNode merge(Iterator<ConstraintNode> constraintNodeIterator) {
        Collection<IConstraint> atomicConstraints = new ArrayList<>();
        Collection<DecisionNode> decisions = new ArrayList<>();

        while (constraintNodeIterator.hasNext()) {
            ConstraintNode constraintNode = constraintNodeIterator.next();

            atomicConstraints.addAll(constraintNode.getAtomicConstraints());
            decisions.addAll(constraintNode.getDecisions());
        }

        return new TreeConstraintNode(atomicConstraints, decisions);
    }

    private final Collection<IConstraint> atomicConstraints;
    private final Collection<DecisionNode> decisions;

    public TreeConstraintNode(Collection<IConstraint> atomicConstraints, Collection<DecisionNode> decisions) {
        this.atomicConstraints =  new ArrayList<>(atomicConstraints);
        this.decisions = new ArrayList<>(decisions);
    }

    public TreeConstraintNode(IConstraint... atomicConstraints) {
        this(
            Arrays.asList(atomicConstraints),
            new ArrayList<>());
    }

    public TreeConstraintNode(IConstraint singleAtomicConstraint) {
        decisions = new ArrayList<>();
        atomicConstraints = new ArrayList<>();
        atomicConstraints.add(singleAtomicConstraint);
    }

    TreeConstraintNode(DecisionNode... decisionNodes) {
        this(
            Collections.emptyList(),
            Arrays.asList(decisionNodes));
    }

    public Collection<IConstraint> getAtomicConstraints() {
        return new ArrayList<>(atomicConstraints);
    }

    public Collection<DecisionNode> getDecisions() {
        return new ArrayList<>(decisions);
    }

    public Optional<RowSpec> getOrCreateRowSpec(Supplier<Optional<RowSpec>> createRowSpecFunc) {
        if (adaptedRowSpec != null)
            return adaptedRowSpec;

        adaptedRowSpec = createRowSpecFunc.get();
        return adaptedRowSpec;
    }
    private Optional<RowSpec> adaptedRowSpec = null;

    public String toString(){
        if (decisions.isEmpty())
            return atomicConstraints.size() > 5
                ? String.format("%d constraints", atomicConstraints.size())
                : Objects.toString(atomicConstraints);

        if (atomicConstraints.isEmpty())
            return decisions.size() > 5
                ? String.format("%d decisions", decisions.size())
                : Objects.toString(decisions);

        return String.format(
            "Decision: %s, Constraints: %s",
            decisions.size() > 5
                ? String.format("%d decisions", decisions.size())
                : Objects.toString(decisions),
            atomicConstraints.size() > 5
                ? String.format("%d constraints", atomicConstraints.size())
                : Objects.toString(atomicConstraints));
    }

    public void addDecision(DecisionNode decision) {
        decisions.add(decision);
    }

    public void removeDecision(DecisionNode decision) {
        decisions.remove(decision);
    }

    public ConstraintNode cloneWithoutAtomicConstraint(IConstraint excludeAtomicConstraint) {
        return new TreeConstraintNode(
            this.atomicConstraints
                .stream()
                .filter(c -> !c.equals(excludeAtomicConstraint))
                .collect(Collectors.toList()),
            decisions);
    }

    public boolean atomicConstraintExists(IConstraint constraint) {
        return atomicConstraints
            .stream()
            .anyMatch(c -> c.equals(constraint));
    }

    public void addAtomicConstraints(Collection<IConstraint> constraints) {
        this.atomicConstraints.addAll(constraints);
    }

    public void appendDecisionNode(DecisionNode decisionNode) {
        decisions.add(decisionNode);
    }
}