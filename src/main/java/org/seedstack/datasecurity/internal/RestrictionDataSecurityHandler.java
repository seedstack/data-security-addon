/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.internal;


import org.seedstack.datasecurity.DataObfuscationHandler;
import org.seedstack.datasecurity.Restriction;
import org.seedstack.datasecurity.spi.DataSecurityHandler;

class RestrictionDataSecurityHandler implements DataSecurityHandler<Restriction> {

    @Override
    public Object securityExpression(Restriction candidate) {
        return candidate.value();
    }

    @Override
    public Class<? extends DataObfuscationHandler<?>> securityObfuscationHandler(Restriction candidate) {
        return candidate.obfuscation();
    }

}
