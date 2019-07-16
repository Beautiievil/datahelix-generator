/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.common.profile.constraints.atomic.IsOfTypeConstraint;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TypeRestrictionsTests {
    @Test
    public void except_withAlreadyExcludedType_shouldReturnSameCollectionOfPermittedTypes(){
        TypeRestrictions exceptStrings = TypeRestrictions.createFromWhiteList(
            IsOfTypeConstraint.Types.NUMERIC,
            IsOfTypeConstraint.Types.DATETIME);

        TypeRestrictions result = exceptStrings.except(IsOfTypeConstraint.Types.STRING);

        Assert.assertThat(result.getAllowedTypes(), containsInAnyOrder(
            IsOfTypeConstraint.Types.NUMERIC,
            IsOfTypeConstraint.Types.DATETIME));
    }

    @Test
    public void except_withNoTypes_shouldReturnSameCollectionOfPermittedTypes(){
        TypeRestrictions exceptStrings = TypeRestrictions.createFromWhiteList(
            IsOfTypeConstraint.Types.NUMERIC,
            IsOfTypeConstraint.Types.DATETIME);

        TypeRestrictions result = exceptStrings.except();

        Assert.assertThat(result, sameInstance(exceptStrings));
    }

    @Test
    public void except_withPermittedType_shouldReturnSameCollectionExcludingGivenType(){
        TypeRestrictions exceptStrings = TypeRestrictions.createFromWhiteList(
            IsOfTypeConstraint.Types.NUMERIC,
            IsOfTypeConstraint.Types.DATETIME);

        TypeRestrictions result = exceptStrings.except(IsOfTypeConstraint.Types.NUMERIC);

        Assert.assertThat(result.getAllowedTypes(), containsInAnyOrder(
            IsOfTypeConstraint.Types.DATETIME));
    }

    @Test
    public void except_withLastPermittedType_shouldReturnNoTypesPermitted(){
        TypeRestrictions exceptStrings = TypeRestrictions.createFromWhiteList(
            IsOfTypeConstraint.Types.DATETIME);

        TypeRestrictions result = exceptStrings.except(IsOfTypeConstraint.Types.DATETIME);

        Assert.assertThat(result.getAllowedTypes(), empty());
    }

}