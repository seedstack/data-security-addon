/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;

import com.google.inject.Injector;
import org.kametic.universalvisitor.api.Mapper;
import org.kametic.universalvisitor.api.Node;
import org.seedstack.datasecurity.DataObfuscationHandler;
import org.seedstack.datasecurity.spi.DataSecurityHandler;
import org.seedstack.seed.SeedException;
import org.seedstack.seed.security.internal.securityexpr.SecurityExpressionInterpreter;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

class DataSecurityMapper implements Mapper<Void> {
    private Map<Object, DataSecurityHandler<?>> securityHandlers;
    private DataSecurityHandler dataSecurityHandler;
    private Object securedObject;
    private SecurityExpressionInterpreter securityExpressionInterpreter;
    private Injector injector;
    private static Map<Class<?>, Object> nulls = new HashMap<>();

    DataSecurityMapper(
            Map<Object, DataSecurityHandler<?>> securityHandlers,
            SecurityExpressionInterpreter securityExpressionInterpreter,
            Injector injector) {
        this.securityHandlers = securityHandlers;
        this.securityExpressionInterpreter = securityExpressionInterpreter;
        this.injector = injector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean handle(AnnotatedElement candidate) {
        for (Annotation anno : candidate.getAnnotations()) {
            dataSecurityHandler = securityHandlers.get(anno.annotationType());
            // If there is no DataSecurityAnnotationHandler for this annotation we continue in the loop
            // if there is one and the interpretation of the associated security expression is false ( i.e. not secured)
            // the field is considered to be obfuscate or removed in the map method.
            if (dataSecurityHandler != null && !securityExpressionInterpreter.interpret(dataSecurityHandler.securityExpression(anno))) {
                securedObject = anno;
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Void map(Node node) {
        checkNotNull(securedObject, "Secured object cannot be null");

        Class<? extends DataObfuscationHandler<?>> securityObfuscationHandler = dataSecurityHandler.securityObfuscationHandler(securedObject);
        Object value;
        Field f = (Field) node.annotatedElement();
        if (securityObfuscationHandler != null) {
            DataObfuscationHandler dataObfuscationHandler = injector.getInstance(securityObfuscationHandler);

            value = readField(f, node.instance());

            value = dataObfuscationHandler.obfuscate(value);

            writeField(f, node.instance(), value);
        } else {
            value = nulls.get(f.getType());
            writeField(f, node.instance(), value);
        }

        return null;
    }

    private Object readField(Field f, Object instance) {
        Object o;

        try {
            f.setAccessible(true);
            o = f.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw SeedException.wrap(e, DataSecurityErrorCode.FIELD_ACCESS_ERROR);
        }

        return o;
    }

    private void writeField(Field f, Object instance, Object value) {
        try {
            f.setAccessible(true);
            f.set(instance, value);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw SeedException.wrap(e, DataSecurityErrorCode.FIELD_ACCESS_ERROR);
        }
    }

    static {
        nulls.put(Integer.class, null);
        nulls.put(Integer.TYPE, 0);
        nulls.put(Short.class, null);
        nulls.put(Short.TYPE, (short) 0);
        nulls.put(Boolean.class, null);
        nulls.put(Boolean.TYPE, false);
        nulls.put(Byte.class, null);
        nulls.put(Byte.TYPE, (byte) 0);
        nulls.put(Long.class, null);
        nulls.put(Long.TYPE, 0L);
        nulls.put(Float.class, null);
        nulls.put(Float.TYPE, 0f);
        nulls.put(Double.class, null);
        nulls.put(Double.TYPE, 0d);
        nulls.put(Character.class, null);
        nulls.put(Character.TYPE, (char) 0);
        nulls.put(String.class, null);
    }
}