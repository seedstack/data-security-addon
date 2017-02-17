---
title: "Data security"
repo: "https://github.com/seedstack/data-security-addon"
author: Epo JEMBA
description: "Data obfuscation according to application security policy." 
tags:
    - "data"
    - "security"
    - "obfuscation"
zones:
    - Addons
menu:
    AddonDataSecurity:
        weight: 10
---

SeedStack data security add-on provides data obfuscation for any POJO according to the application security policy. The 
goal is to protect the data exposed by an application. You define security constraints on class attributes and the 
obfuscation that is used when those constraints are violated. For instance, an account number like `79927391338710` can be 
transformed into `799273******10`.

# Dependency

{{< dependency g="org.seedstack.addons.datasecurity" a="data-security" >}}

# Usage

## @Restriction annotation

The {{< java "org.seedstack.datasecurity.Restriction" "@" >}} annotation can be applied on any class attribute, marking
it as a candidate for obfuscation if the security expression evaluates to false:

```java
public class MySecuredPojo {
    @Restriction(value = "${ hasRole('manager') }", obfuscation = AccountObfuscationHandler.class)
    private String accountNumber;
}
```    

It takes the following parameters:

* `value` is a [security expression](#security-expressions),
* `obfuscation` is the class of the [obfuscation handler](#obfuscation) to use if the expression is evaluated to false. It
defaults to {{< java "org.seedstack.datasecurity.NullifyObfuscationHandler" >}} which will set the field to `null`.

## Data security service

The security on data can be applied by using the `DataSecurityService` as follows:

```java
public class SomeClass {
    @Inject
    private DataSecurityService dataSecurityService;

    public MyDto someMethod(MyBusinessObject businessObject) {
        MyDto myDto = new MyDto(businessObject);
        dataSecurityService.secure(myDto);
        return myDto;
    }
}
```

This service will go recursively through the object fields and look for restrictions. Each restriction that evaluates to f
alse against the current Subject will trigger the obfuscation of its associated field.

## @Secured annotation

You can add a `@Secured` annotation on any method parameter to automatically apply data security on it. You can also 
apply the `@Secured` annotation directly on the method to apply data security on the return value:

```java
public class SomeClass {
    // The method is intercepted and its return value is secured 
    @Secured
    public MyDto someMethod(MyBusinessObject businessObject) {
        // ...
    }
    
    // The method is intercepted and its first parameter 
    @Secured
    public void otherMethod(@Secured MyDto myDto, String otherParameter) {
        // ...
    }
}
```

Every method annotated with `@Secured` or with the annotation applied to at least one of its parameters will be intercepted 
and the relevant objects will be secured. Note that the [usual interception limitations]({{< ref "docs/seed/dependency-injection.md#method-interception" >}}) apply.

{{% callout warning %}}
Please note that the data security interceptor will inspect the whole object graph starting from the secured object, so 
you may encounter some performance penalty depending on its size. This should not be a problem for typical use.
{{% /callout %}}

# Security expressions

Security expressions are strings that respect the [Unified Expression Language (UEL)](https://uel.java.net/) syntax. The 
following methods are available:

* `hasRole(String role)`. Returns true if the current subject has the specified role, false otherwise.
* `hasRoleOn(String role, String scope)`. Returns true if the current subject has the specified role for all the specified scope, false otherwise.
* `hasPermission(String permission)`. Returns true if the current subject has the specified permission, false otherwise.
* `hasPermissionOn(String permission, String scope)`. Returns true if the current subject has the specified permission on the specified scope, false otherwise.

Examples:

```plain
${ !hasRole('manager') && hasPermission('salary:view') }
${ hasPermission('salary:view') && hasPermission('salary:update') }
${ hasPermissionOn('users:manage', 'FR') }
```

More resources on EL:

* [Oracle tutorial](http://docs.oracle.com/javaee/6/tutorial/doc/gjddd.html)
* [Unified Expression Language](https://uel.java.net/)

# Obfuscation

You can define obfuscations by implementing the {{< java "org.seedstack.datasecurity.DataObfuscationHandler" >}} interface:

```java
// This DataObfuscationHandler takes a String and turns it into its initial (eg. "Doe" -> "D.")
public class InitialObfuscationHandler implements DataObfuscationHandler<String> {
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
```
This handler is then used by referencing it from the {{< java "org.seedstack.datasecurity.Restriction" "@" >}} annotation:

```java
public class MySecuredPojo {
    @Restriction(value = "${ hasRole('admin') }", obfuscation = InitialObfuscationHandler.class)
    private String name;
}
```    

# Custom restriction annotations

Custom restriction annotations can be defined and registered with data security by defining a `DataSecurityHandler`. 
Start with defining a custom annotation:
    
```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD})
    public @interface MyRestriction {
    	String expression();
    	Todo todo() default Todo.Nullify;
    	
    	enum Todo {
    		Hide, Round, Nullify
    	}
    }
```

Then, define a `DataSecurityHandler` which handles the `@MyRestriction` annotation:

```java
    public class MyDataSecurityHandler implements DataSecurityHandler<MyRestriction> {
    	@Override
    	public Object securityExpression(MyRestriction annotation) {
    		return annotation.expression();
    	}
    
    	@Override
    	public Class<? extends DataObfuscationHandler<?>> securityObfuscationHandler(
    																MyRestriction annotation) {    
    		if (annotation.todo() .equals( Todo.Round  )) {
    			// Uses the rounding obfuscation handler defined below
    			return RoundingObfuscationHandler.class;
    		}
    		
    		if (annotation.todo() .equals( Todo.Hide  )) {
    			// Uses the name obfuscation handler defined in the previous section
    			return NameObfuscationHandler.class;
    		}
    		
    		return null;
    	}
    	
    	public static class RoundingObfuscationHandler 
    						implements DataObfuscationHandler<Integer> {
    		@Override
    		public Integer obfuscate(Integer data) {
                Integer result = 0;
    			if (data != null) {
                	result = (int) (Math.ceil(data / 1000) * 1000);
                }
    			return result;
    		}    		
    	}
    }
```

Then, you can apply the annotation on a POJO:
    
```java
    public class MyPojo {    	
    	private String firstName;
    	@MyRestriction(expression="${1 == 2}" , todo = Todo.Hide)
    	private String name;
    	@MyRestriction( expression="${ hasRole('manager') }", todo=Todo.Round )
    	private Integer salary;
    	@MyRestriction(expression="${false}")
    	private String password;
    
    	public MyPojo(String name, String firstName, String password, Integer salary) {
    		this.name = name;
    		this.firstName = firstName;
    		this.password = password;
    		this.salary = salary;
    	}
    }
```
