/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity;

/**
 * DataSecurityService is the SEED interface that offers data security related services .
 * <p>
 * It offers methods that helps dealing with data access restriction according to permissions and roles.
 * <p>
 * DataSecurityService is used in conjunction with {@link org.seedstack.datasecurity.spi.DataSecurityHandler}.
 *
 * @see org.seedstack.datasecurity.spi.DataSecurityHandler
 */
public interface DataSecurityService {

    /**
     * This method will modify the state of c according the role and permissions of the current user.
     * <p>
     * It means data will be modified accordingly.
     *
     * @param <C>       the type of the candidate.
     * @param candidate the object to be modified.
     */
    <C> void secure(C candidate);

}
