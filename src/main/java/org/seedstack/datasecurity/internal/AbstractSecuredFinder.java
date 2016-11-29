/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;

import org.seedstack.shed.predicate.ExecutablePredicates;
import org.seedstack.shed.reflect.Classes;

import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class AbstractSecuredFinder implements Predicate<Method> {
    protected Stream<Method> findEquivalentMethods(Method method) {
        return Classes.from(method.getDeclaringClass())
                .traversingInterfaces()
                .traversingSuperclasses()
                .methods()
                .filter(ExecutablePredicates.executableIsEquivalentTo(method));
    }
}

