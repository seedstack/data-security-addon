/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class NullifyObfuscationHandlerTest {
    private static class Foo {
        int myint = 123;
        Integer myInteger = 123;
        short myshort = 456;
        Short myShort = 4554;
        boolean myboolean = true;
        Boolean myBoolean = Boolean.TRUE;
        byte mybyte = 12;
        Byte myByte = 123;
        long mylong = 121L;
        Long myLong = 45L;
        float myfloat = 565f;
        Float myFloat = 45456f;
        double mydouble = 4545d;
        Double myDouble = 545d;
        char mycharacter = 'c';
        Character myCharacter = ' ';
        String myString = "zerzerrzet";
        Object dummy = new Object();
    }

    private NullifyObfuscationHandler underTest;

    @Before
    public void init() {
        underTest = new NullifyObfuscationHandler();
    }

    @Test
    public void testObfuscate() {
        Foo foo = new Foo();

        assertThat(underTest.obfuscate(foo.myint)).isEqualTo(0);
        assertThat(underTest.obfuscate(foo.myInteger)).isEqualTo(0);

        assertThat(underTest.obfuscate(foo.myshort)).isEqualTo((short) 0);
        assertThat(underTest.obfuscate(foo.myShort)).isEqualTo((short) 0);

        assertThat(underTest.obfuscate(foo.myboolean)).isEqualTo(false);
        assertThat(underTest.obfuscate(foo.myBoolean)).isEqualTo(Boolean.FALSE);

        assertThat(underTest.obfuscate(foo.mybyte)).isEqualTo((byte) 0);
        assertThat(underTest.obfuscate(foo.myByte)).isEqualTo((byte) 0);

        assertThat(underTest.obfuscate(foo.mylong)).isEqualTo(0L);
        assertThat(underTest.obfuscate(foo.myLong)).isEqualTo(0L);

        assertThat(underTest.obfuscate(foo.myfloat)).isEqualTo(0f);
        assertThat(underTest.obfuscate(foo.myFloat)).isEqualTo(0f);

        assertThat(underTest.obfuscate(foo.mydouble)).isEqualTo(0d);
        assertThat(underTest.obfuscate(foo.myDouble)).isEqualTo(0d);

        assertThat(underTest.obfuscate(foo.mycharacter)).isEqualTo((char) 0);
        assertThat(underTest.obfuscate(foo.myCharacter)).isEqualTo((char) 0);

        assertThat(underTest.obfuscate(foo.myString)).isEqualTo("");

        assertThat(underTest.obfuscate(foo.dummy)).isNull();
    }
}
