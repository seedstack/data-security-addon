/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;

import io.nuun.kernel.api.plugin.InitState;
import io.nuun.kernel.api.plugin.context.InitContext;
import io.nuun.kernel.api.plugin.request.BindingRequest;
import io.nuun.kernel.api.plugin.request.ClasspathScanRequest;
import org.seedstack.datasecurity.DataObfuscationHandler;
import org.seedstack.datasecurity.spi.DataSecurityHandler;
import org.seedstack.seed.core.internal.AbstractSeedPlugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DataSecurityPlugin extends AbstractSeedPlugin {
    private final Set<Class<? extends DataSecurityHandler<?>>> dataSecurityHandlers = new HashSet<>();

    @Override
    public String name() {
        return "data-security";
    }

    @Override
    public Collection<ClasspathScanRequest> classpathScanRequests() {
        return classpathScanRequestBuilder().descendentTypeOf(DataSecurityHandler.class).build();
    }

    @Override
    public Collection<BindingRequest> bindingRequests() {
        return bindingRequestsBuilder().descendentTypeOf(DataObfuscationHandler.class).build();
    }

    @Override
    protected InitState initialize(InitContext initContext) {
        Map<Class<?>, Collection<Class<?>>> scannedClasses = initContext.scannedSubTypesByAncestorClass();
        configureDataSecurityHandlers(scannedClasses.get(DataSecurityHandler.class));
        return InitState.INITIALIZED;
    }

    @SuppressWarnings("unchecked")
    private void configureDataSecurityHandlers(Collection<Class<?>> securityHandlerClasses) {
        if (securityHandlerClasses != null) {
            for (Class<?> candidate : securityHandlerClasses) {
                if (DataSecurityHandler.class.isAssignableFrom(candidate)) {
                    dataSecurityHandlers.add((Class<? extends DataSecurityHandler<?>>) candidate);
                }
            }
        }
    }

    @Override
    public Object nativeUnitModule() {
        return new DataSecurityModule(dataSecurityHandlers);
    }
}
