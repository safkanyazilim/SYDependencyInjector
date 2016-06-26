package com.safkanyazilim.introspection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MethodInvocationUtil {
	
	private static Map<String, Method> methodMap = new HashMap<String, Method>();
	
	public static Object InvokeMethodOfClass(Class<?> clazz,String methodName,Object objectInvoketFrom,Object... args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		String key = methodName + " " + clazz.getCanonicalName();
	    Method method = methodMap.get(key);
			
		if (method == null) {
		 method = null;
			if (args.length == 0) {
				method = clazz.getDeclaredMethod(methodName);
			} else if (args.length == 1) {
				method = clazz.getDeclaredMethod(methodName, args[0].getClass());
			} else if (args.length == 2) {
				method = clazz.getDeclaredMethod(methodName, args[0].getClass(),args[1].getClass());
			} else if (args.length == 3) {
				method = clazz.getDeclaredMethod(methodName, args[0].getClass(),args[1].getClass(),args[2].getClass());
			} else if (args.length == 4) {
				method = clazz.getDeclaredMethod(methodName, args[0].getClass(),args[1].getClass(),args[2].getClass(),args[3].getClass());
			}
			methodMap.put(key, method);
		} 
		
		return method.invoke(objectInvoketFrom, args);
	}
	
	 public static Object instantiateObject(Class<?> clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		    
	    	Constructor<?> constructor = clazz.getConstructor((Class[]) null);
	        constructor.setAccessible(true);
	        return constructor.newInstance();
	 }

    public static  List<Field> getDeclearedFields(Class<?> clazz) {    

    	Type superClass = clazz.getSuperclass();
    	List<Field> fieldList = new ArrayList<Field>();	    
		addAllFieldsToList(fieldList, clazz.getDeclaredFields());
		
		if(!superClass.toString().contains("java") ) {
			List<Field> superClassField = getDeclearedFields(((Class<?>)superClass));
			fieldList.addAll(superClassField);
		}
		
		return fieldList;
	}    
		   
	 private static void addAllFieldsToList(List<Field> list, Field[] array) {
	     	for(Field element : array) {
	     		list.add(element);
	     	}
	 }
}
