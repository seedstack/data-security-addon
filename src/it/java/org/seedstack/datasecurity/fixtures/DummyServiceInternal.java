/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.fixtures;

import org.seedstack.datasecurity.Secured;

public class DummyServiceInternal implements DummyService {

    @Override
    public Dummy service1(Dummy d1, @Secured Dummy d2, Dummy d3) {
        return DummyFactory.create(1);
    }

    @Override
    public Dummy service2(Dummy d4) {
        return DummyFactory.create(2);
    }

}
