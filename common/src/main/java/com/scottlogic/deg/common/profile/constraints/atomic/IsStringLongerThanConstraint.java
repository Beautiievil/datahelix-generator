package com.scottlogic.deg.common.profile.constraints.atomic;

import com.scottlogic.deg.common.profile.Field;

import java.util.Objects;

public class IsStringLongerThanConstraint implements AtomicConstraint {
    public final Field field;
    public final int referenceValue;

    public IsStringLongerThanConstraint(Field field, int referenceValue) {
        if (referenceValue < 0){
            throw new IllegalArgumentException("Cannot create an IsStringLongerThanConstraint for field '" +
                field.name + "' with a a negative length.");
        }

        this.referenceValue = referenceValue;
        this.field = field;
    }

    @Override
    public String toDotLabel(){
        return String.format("%s length > %s", field.name, referenceValue);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        IsStringLongerThanConstraint constraint = (IsStringLongerThanConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(referenceValue, constraint.referenceValue);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, referenceValue);
    }

    @Override
    public String toString() { return String.format("`%s` length > %d", field.name, referenceValue); }
}
