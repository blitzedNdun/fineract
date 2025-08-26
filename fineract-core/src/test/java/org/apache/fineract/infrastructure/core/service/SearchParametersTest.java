/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Ad-hoc test for SearchParameters secured field functionality Tests the implementation of the secured field and getter
 * method according to the requirements in Summary of Changes section 0.3.1
 */
class SearchParametersTest {

    @Test
    void testSecuredFieldWithTrueValue() {
        // Test secured=true scenario for loans with collateral
        SearchParameters params = SearchParameters.builder().secured(Boolean.TRUE).build();

        assertEquals(Boolean.TRUE, params.getSecured());
        assertNotNull(params.getSecured());
        assertTrue(params.getSecured());
    }

    @Test
    void testSecuredFieldWithFalseValue() {
        // Test secured=false scenario for loans without collateral
        SearchParameters params = SearchParameters.builder().secured(Boolean.FALSE).build();

        assertEquals(Boolean.FALSE, params.getSecured());
        assertNotNull(params.getSecured());
        assertFalse(params.getSecured());
    }

    @Test
    void testSecuredFieldWithNullValue() {
        // Test secured=null scenario for no filtering (backward compatibility)
        SearchParameters params = SearchParameters.builder().secured(null).build();

        assertNull(params.getSecured());
    }

    @Test
    void testSecuredFieldNotSet() {
        // Test when secured parameter is not set at all (backward compatibility)
        SearchParameters params = SearchParameters.builder().accountNo("12345").status("ACTIVE").build();

        assertNull(params.getSecured());
    }

    @Test
    void testSecuredFieldWithOtherParameters() {
        // Test secured field works together with other parameters
        SearchParameters params = SearchParameters.builder().secured(Boolean.TRUE).accountNo("12345").status("ACTIVE").clientId(100L)
                .officeId(1L).build();

        assertEquals(Boolean.TRUE, params.getSecured());
        assertEquals("12345", params.getAccountNo());
        assertEquals("ACTIVE", params.getStatus());
        assertEquals(Long.valueOf(100), params.getClientId());
        assertEquals(Long.valueOf(1), params.getOfficeId());
    }

    @Test
    void testSecuredFieldThreeStates() {
        // Test all three states as specified in the requirements:
        // - null (unset): No filtering applied, preserves current behavior
        // - Boolean.TRUE: Filter to only loans with collateral (EXISTS clause)
        // - Boolean.FALSE: Filter to only loans without collateral (NOT EXISTS clause)

        SearchParameters unsetParams = SearchParameters.builder().build();
        SearchParameters withCollateralParams = SearchParameters.builder().secured(Boolean.TRUE).build();
        SearchParameters withoutCollateralParams = SearchParameters.builder().secured(Boolean.FALSE).build();

        // Unset state - should return null
        assertNull(unsetParams.getSecured());

        // With collateral state - should return TRUE
        assertEquals(Boolean.TRUE, withCollateralParams.getSecured());

        // Without collateral state - should return FALSE
        assertEquals(Boolean.FALSE, withoutCollateralParams.getSecured());

        // Verify they are different from each other
        assertNotEquals(unsetParams.getSecured(), withCollateralParams.getSecured());
        assertNotEquals(unsetParams.getSecured(), withoutCollateralParams.getSecured());
        assertNotEquals(withCollateralParams.getSecured(), withoutCollateralParams.getSecured());
    }
}
