/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.seedstack.seed.security.data.DataSecurityService;
import org.seedstack.seed.security.data.Secured;

import javax.inject.Inject;
import java.lang.reflect.Method;

/**
 * This interceptor will apply Data Security Service on the annotated parameters and/or on the return value.
 */
class DataSecurityInterceptor implements MethodInterceptor {
    @Inject
    private DataSecurityService dataSecurityService;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        boolean secureReturnValue = SecuredReturnValueFinder.INSTANCE.test(method);
        Secured[] parametersToBeSecured = SecuredParameterFinder.INSTANCE.apply(method);

        Object[] arguments = invocation.getArguments();
        Object returnValue = invocation.proceed();

        if (secureReturnValue) {
            dataSecurityService.secure(returnValue);
        }

        for (int i = 0; parametersToBeSecured != null && i < parametersToBeSecured.length; i++) {
            if (parametersToBeSecured[i] != null) {
                dataSecurityService.secure(arguments[i]);
            }
        }

        return returnValue;
    }
}
