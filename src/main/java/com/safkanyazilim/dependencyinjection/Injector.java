package com.safkanyazilim.dependencyinjection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class Injector {
	
	public static void satisfyDependenciesWithConfigurationOfClassMap(Object object, HashMap<Field, Class<?>> configurationMap){
		Class<?> clazz = object.getClass();
		
		List<Field> fields = new LinkedList<Field>();
		fields =  getAllFields(fields, clazz);
		
		for (Field field : fields) {
			if (Injector.fieldIsDependency(field) && configurationMap.containsKey(field)) {
				Class<?> fieldClass = configurationMap.get(field);
				injectField(field, object, fieldClass);
			}
		}
	}
	
	private static List<Field> getAllFields(List<Field> fields, Class<?> clazz) {
	    for (Field field: clazz.getDeclaredFields()) {
	        fields.add(field);
	    }
	    
	    if (clazz.getSuperclass() != null) {
	        fields = getAllFields(fields, clazz.getSuperclass());
	    }

	    return fields;
	}
	
	/**
	 * This method will generate (or retrieve, if it is a singleton, 
	 * and already exists) an object for the given class, satisfy
	 * its dependencies, and return the resulting object.
	 * 
	 * @param clazz the class to be instantiated/retrieved.
	 * @return the generated/retrieved object.
	 */
	public static <T> T generateObjectForClass(Class<T> clazz) {
		T object = generateOrFindObjectForClass(clazz);
		return object;
	}
	
	public static void satisfyDependencies(Object object) {
		Class<?> clazz = object.getClass();
		
		List<Field> fields = new LinkedList<Field>();
		fields =  getAllFields(fields, clazz);
		
		for (Field field : fields) {
			if (Injector.fieldIsDependency(field)) {
				Class<?> fieldClass = field.getType();
				Injector.injectField(field, object, fieldClass);
			}
		}

		for (Method method : clazz.getMethods()) {
			if(method.getAnnotation(Initializer.class) != null) {
				try {
					method.setAccessible(true);
					method.invoke(object);
				} catch (IllegalAccessException e) {
					throw new InjectionException("Failed calling method " + method.getName() + " of " + clazz.getCanonicalName(), e);
				} catch (InvocationTargetException e) {
					throw new InjectionException("Failed calling method " + method.getName() + " of " + clazz.getCanonicalName(), e);
				}
			}
		}			
	}
	
	private static void injectField(Field field, Object object, Class<?> fieldClass) {
		
		Object fieldValue = generateOrFindObjectForClass(fieldClass);
		
		field.setAccessible(true);
		
		try {
			field.set(object, fieldValue);
		}  catch (IllegalAccessException e) {
			throw new InjectionException("Field injection failed. Trying to inject " 
										 + object.getClass().getCanonicalName() 
										 + " field " + field.getName() , e);
		}
	}
	
	private static <T> T generateOrFindObjectForClass(Class<T> clazz) {
		
		if (Injector.classIsSingleton(clazz)) {
			// Singleton!		
			if (ObjectFactory.isClassMappedToObject(clazz)) {
				// Class is mapped to object, either by us (the expected case) or by
				// someone else (also legal, just not expected to be typical) so we
				// will readily use that. -- YS
				// 
				// Also note that if mapping is done externally, its dependencies 
				// should also have been satisfied externally -- we do not satisfy
				// dependencies for anything we did not generate. -- YS
				return ObjectFactory.newObject(clazz);
			} else {
				// This is a singleton, but not mapped to an object --yet--. So, we
				// ask ObjectFactory to construct us the object, satisfy its dependencies,
				// and return the object. -- YS
				T object = ObjectFactory.newObject(clazz);
				ObjectFactory.mapClassToObject(clazz, object);
				Injector.satisfyDependencies(object);
				return object;
			}
		} else {
			// This is not a singleton. So, we generate a new object using ObjectFactory. Note 
			// that regular ObjectFactory rules apply here; even if the class is not marked as
			// as a singleton, it may indeed act as a singleton if the class is mapped to 
			// an object externally. -- YS
			T object = ObjectFactory.newObject(clazz);
			Injector.satisfyDependencies(object);
			
			return object;
		}
	}
	
	public static boolean fieldIsDependency(Field field) {
		return field.getAnnotation(Dependency.class) != null;
	}
	
	public static boolean classIsSingleton(Class<?> clazz) {
		return clazz.getAnnotation(Singleton.class) != null;
	}

	
}
