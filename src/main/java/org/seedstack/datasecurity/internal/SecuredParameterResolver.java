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
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.function.Function;

class SecuredParameterResolver extends AbstractSecuredResolver implements Function<Method, Secured[]> {
    static SecuredParameterResolver INSTANCE = new SecuredParameterResolver();

    private SecuredParameterResolver() {
        // no external instantiation allowed
    }

    @Override
    public Secured[] apply(Method method) {
        Secured[] result = new Secured[method.getParameterCount()];
        findEquivalentMethods(method).forEach(elected -> {
            Parameter[] parameters = elected.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Secured secured = parameters[i].getAnnotation(Secured.class);
                if (secured != null) {
                    result[i] = secured;
                }
            }
        });
        return result;
    }

    @Override
    public boolean test(Method method) {
        return findEquivalentMethods(method).anyMatch(candidate ->
                Arrays.stream(candidate.getParameterTypes()).anyMatch(parameter -> parameter.isAnnotationPresent(Secured.class))
        );
    }
}
