/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;

import com.google.inject.Injector;
import org.kametic.universalvisitor.UniversalVisitor;
import org.kametic.universalvisitor.api.Filter;
import org.seedstack.datasecurity.DataSecurityService;
import org.seedstack.datasecurity.spi.DataSecurityHandler;
import org.seedstack.seed.security.internal.securityexpr.SecurityExpressionInterpreter;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * Implementation of the DataSecurityService with the UniversalVisitor library.
 */
class DataSecurityServiceInternal implements DataSecurityService {
    @Inject
    private SecurityExpressionInterpreter securityExpressionInterpreter;
    @Inject
    private Map<Object, DataSecurityHandler<?>> securityHandlers;
    @Inject
    private Injector injector;

    @Override
    public <Candidate> void secure(Candidate candidate) {
        DataSecurityMapper dataSecurityMapper = new DataSecurityMapper(securityHandlers, securityExpressionInterpreter, injector);
        UniversalVisitor universalVisitor = new UniversalVisitor();
        universalVisitor.visit(candidate, new SyntheticPredicate(), dataSecurityMapper);
    }

    private static class SyntheticPredicate implements Filter {
        @Override
        public boolean retains(Field input) {
            return !input.isSynthetic();
        }
    }
}
