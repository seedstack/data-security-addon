/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.MapBinder;
import net.jodah.typetools.TypeResolver;
import org.seedstack.datasecurity.DataSecurityService;
import org.seedstack.datasecurity.spi.DataSecurityHandler;
import org.seedstack.seed.core.internal.utils.MethodMatcherBuilder;

import java.lang.reflect.Method;
import java.util.Collection;

@DataSecurityConcern
class DataSecurityModule extends AbstractModule {
    private static final TypeLiteral<DataSecurityHandler<?>> MAP_TYPE_LITERAL = new TypeLiteral<DataSecurityHandler<?>>() {
    };
    private Collection<Class<? extends DataSecurityHandler<?>>> dataSecurityHandlers;

    DataSecurityModule(Collection<Class<? extends DataSecurityHandler<?>>> dataSecurityHandlers) {
        this.dataSecurityHandlers = dataSecurityHandlers;
    }

    @Override
    protected void configure() {
        bind(DataSecurityService.class).to(DataSecurityServiceInternal.class);

        MapBinder<Object, DataSecurityHandler<?>> mapBinder = MapBinder.newMapBinder(binder(), TypeLiteral.get(Object.class), MAP_TYPE_LITERAL);
        for (Class<? extends DataSecurityHandler<?>> dSecClass : dataSecurityHandlers) {
            Object typeParameterClass = TypeResolver.resolveRawArguments(DataSecurityHandler.class, dSecClass)[0];
            mapBinder.addBinding(typeParameterClass).to(dSecClass);

            // TODO : pour l'augmentation des features du DataSecurityHandler
            // 1 ) Créer une interface SecuredObjectProvider qui fournira la ConventionSpecification application soit (Field.class , Method.class , Constructor.class)
            //     elle doit avoir une methode get en plus de apply/specify/whatv
            //     un simple get
            // 2) Créer la Clé composite en question
        }

        // @Secured interceptor
        DataSecurityInterceptor dataSecurityInterceptor = new DataSecurityInterceptor();
        requestInjection(dataSecurityInterceptor);
        bindInterceptor(Matchers.any(), securedMethods(), dataSecurityInterceptor);
    }

    private Matcher<? super Method> securedMethods() {
        return new MethodMatcherBuilder(SecuredReturnValueResolver.INSTANCE.or(SecuredParameterResolver.INSTANCE)).build();
    }
}
