/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;

import org.seedstack.datasecurity.Secured;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

class SecuredReturnValueResolver extends AbstractSecuredResolver implements Function<Method, Optional<Secured>>, Predicate<Method> {
    static SecuredReturnValueResolver INSTANCE = new SecuredReturnValueResolver();

    private SecuredReturnValueResolver() {
        // no external instantiation allowed
    }

    @Override
    public Optional<Secured> apply(Method method) {
        return findEquivalentMethods(method)
                .map(candidate -> candidate.getReturnType().getAnnotation(Secured.class))
                .findFirst();
    }

    @Override
    public boolean test(Method method) {
        return findEquivalentMethods(method).anyMatch(candidate ->
                candidate.isAnnotationPresent(Secured.class)
        );
    }
}
