package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.DataBagValue;
import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import com.scottlogic.deg.generator.generation.databags.IDataBagSource;
import com.scottlogic.deg.generator.generation.field_value_sources.*;
import com.scottlogic.deg.generator.restrictions.FieldSpec;
import com.scottlogic.deg.generator.restrictions.NullRestrictions;
import com.scottlogic.deg.generator.restrictions.StringRestrictions;
import com.scottlogic.deg.generator.utils.*;

import java.util.*;
import java.util.stream.Collectors;

public class FieldSpecFulfiller implements IDataBagSource {
    private final Field field;
    private final FieldSpec spec;

    public FieldSpecFulfiller(Field field, FieldSpec spec) {
        this.field = field;
        this.spec = spec;
    }

    @Override
    public Iterable<DataBag> generate(GenerationConfig generationConfig) {
        List<IFieldValueSource> fieldValueSources = getAllApplicableValueSources();

        if (generationConfig.shouldChooseFiniteSampling()) {
            fieldValueSources = fieldValueSources.stream()
                .map(fvs -> new LimitingFieldValueSource(fvs, 3, 8))
                .collect(Collectors.toList());
        }

        IFieldValueSource combinedFieldValueSource = new CombiningFieldValueSource(fieldValueSources);

        return new ProjectingIterable<>(
            combinedFieldValueSource.generateAllValues(),
            value ->
            {
                DataBagValue dataBagValue = new DataBagValue(
                    value,
                    this.spec.getFormatRestrictions() != null
                        ? this.spec.getFormatRestrictions().formatString
                        : null);

                return DataBag.startBuilding()
                    .set(
                        this.field,
                        dataBagValue)
                    .build();
            });
    }

    private List<IFieldValueSource> getAllApplicableValueSources() {
        List<IFieldValueSource> validSources = new ArrayList<>();

        // check nullability...
        if (determineNullabilityAndDecideWhetherToHalt(validSources))
            return validSources;

        // if there's a whitelist, we can just output that
        if (spec.getSetRestrictions() != null) {
            Set<?> whitelist = spec.getSetRestrictions().getWhitelist();
            if (whitelist != null) {
                return Collections.singletonList(
                    new CannedValuesFieldValueSource(new ArrayList<>(whitelist)));
            }
        }

        // if there're reasonably populated numeric restrictions, output within range
        if (spec.getNumericRestrictions() != null) {
            validSources.add(
                new IntegerFieldValueSource(
                    spec.getNumericRestrictions().min,
                    spec.getNumericRestrictions().max,
                    getBlacklist()));

            return validSources;
        }

        // if there're reasonably populated string restrictions, output from stringGenerator
        StringRestrictions stringRestrictions = spec.getStringRestrictions();
        if (stringRestrictions != null && (stringRestrictions.stringGenerator != null)) {
            Set<Object> blacklist = getBlacklist();

            final IStringGenerator generator;
            if (blacklist.size() > 0) {
                RegexStringGenerator blacklistGenerator = RegexStringGenerator.createFromBlacklist(blacklist);

                generator = stringRestrictions.stringGenerator.intersect(blacklistGenerator);
            } else {
                generator = stringRestrictions.stringGenerator;
            }

            validSources.add(generator.asFieldValueSource());

            return validSources;
        }

        // there were no typed restrictions - just output some random bits of data
        validSources.add(
            CannedValuesFieldValueSource.of(
                "string", 123, true));

        return validSources;
    }

    private boolean determineNullabilityAndDecideWhetherToHalt(List<IFieldValueSource> fieldValueSources) {
        IFieldValueSource nullOnlySource = new CannedValuesFieldValueSource(Collections.singletonList(null));

        if (spec.getNullRestrictions() != null) {
            if (spec.getNullRestrictions().nullness == NullRestrictions.Nullness.MustBeNull) {
                // if *always* null, add a null-only source and signal that no other sources are needed
                fieldValueSources.add(nullOnlySource);
                return true;
            } else if (spec.getNullRestrictions().nullness == NullRestrictions.Nullness.MustNotBeNull) {
                // if *never* null, add nothing and signal that source generation should continue
                return false;
            }
        }

        // if none of the above, the field is nullable
        fieldValueSources.add(nullOnlySource);
        return false;
    }

    private Set<Object> getBlacklist() {
        if (spec.getSetRestrictions() == null)
            return Collections.emptySet();

        return new HashSet<>(spec.getSetRestrictions().getBlacklist());
    }
}