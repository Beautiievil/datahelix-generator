package com.scottlogic.deg.generator.constraints;

import com.scottlogic.deg.generator.Field;

import java.util.Collection;
import java.util.Objects;

public class AtomicNotConstraint implements AtomicConstraint {
    public final IConstraint negatedConstraint;

    public AtomicNotConstraint(IConstraint negatedConstraint) {
        this.negatedConstraint = negatedConstraint;
    }


    public static IConstraint negate(IConstraint constraint) {
        if (constraint instanceof AtomicNotConstraint)
            return ((AtomicNotConstraint) constraint).negatedConstraint;

        return new AtomicNotConstraint(constraint);
    }

    private IConstraint getBaseConstraint(){
        if (negatedConstraint instanceof AtomicNotConstraint){
            return ((AtomicNotConstraint) negatedConstraint).getBaseConstraint();
        }
        return negatedConstraint;
    }

    private int getNegationLevel(){
        if (negatedConstraint instanceof AtomicNotConstraint){
            return ((AtomicNotConstraint) negatedConstraint).getNegationLevel() + 1;
        }
        return 1;
    }

    @Override
    public String toDotLabel() {
        /*Use the encoded character code for the NOT (¬) symbol; leaving it un-encoded causes issues with visualisers*/
        return String.format("&#x00AC;(%s)", negatedConstraint.toDotLabel());
    }

    @Override
    public Collection<Field> getFields() {
        return negatedConstraint.getFields();
    }

    public String toString(){
        return String.format(
                "NOT(%s)",
                negatedConstraint);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AtomicNotConstraint otherConstraint = (AtomicNotConstraint) o;
        return Objects.equals(getBaseConstraint(), otherConstraint.getBaseConstraint())
            && Objects.equals(getNegationLevel() % 2, otherConstraint.getNegationLevel() % 2);
    }

    @Override
    public int hashCode(){
        return Objects.hash("NOT", negatedConstraint.hashCode());
    }
}
