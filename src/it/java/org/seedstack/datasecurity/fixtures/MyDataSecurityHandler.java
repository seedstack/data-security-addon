/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.datasecurity.fixtures;

import org.seedstack.seed.security.spi.data.DataObfuscationHandler;
import org.seedstack.seed.security.spi.data.DataSecurityHandler;


public class MyDataSecurityHandler implements DataSecurityHandler<MyRestriction> {

	@Override
	public Object securityExpression(MyRestriction annotation) {
		return annotation.expression();
	}

	@Override
	public Class<? extends DataObfuscationHandler<?>> securityObfuscationHandler(MyRestriction annotation) {

		if (annotation.todo() .equals( MyRestriction.Todo.Hide  )) {
			return Obfus.class;
		}
		if (annotation.todo() .equals( MyRestriction.Todo.Initial  )) {
			return InitialHandler.class;
		}
		return null;
	}
	
	
	public static class Obfus implements DataObfuscationHandler<Integer> {

		@Override
		public Integer obfuscate(Integer data) {
            Integer result = 0;
			if (data != null && data > 1000) {
            	result = (int) (Math.ceil(data / 1000) * 1000);
            }
			return result;
		}
		
		
	}
	
	public static class InitialHandler implements DataObfuscationHandler<String> {

		@Override
		public String obfuscate(String data) {
			String result = "";
			if (data != null && data.length() > 0) {
				result = data.charAt(0) + ".";
                result = result.toUpperCase();
			}
			return result;
		}
		
	}
	

}
