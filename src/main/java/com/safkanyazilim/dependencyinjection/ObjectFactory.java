package com.safkanyazilim.dependencyinjection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is a general purpose "Object Factory". 
 * @author Y. Safkan <safkan@safkanyazilim.com>
 * 
 */

@Singleton
public class ObjectFactory {
    private static Map<Class<?>, Class<?>> classToClassMap = new HashMap<Class<?>, Class<?>>();
    
    private static Map<Class<?>, Object> classToObjectMap = new HashMap<Class<?>, Object>();
    
    /**
     * Determine whether the given argument list args can be used to invoke the
     * given constructor.
     * 
     * @param <T> The type the constructor will construct.
     * @param constructor the constructor
     * @param args the arguments that would be passed to the constructor
     * @return true if the constructor can be invoked, false otherwise
     */
    private static <T> boolean isCompatible(Constructor<T> constructor, Object[] args) 
    {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        
        // If number of arguments do not match, return false.
        
        if (parameterTypes.length != args.length) {
            return false;
        }     
                
        // Make sure the arguments are of the correct types.
     
        for (int i = 0; i < parameterTypes.length; i++) {
    
        	if (!isParamaterTypeCompatibleForArgument(parameterTypes[i], args[i])) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * <p> 
     * This class checks if the given object is compatible with the given parameter type.
     *  1) If parameter class is primitive argument,
     *  	a) Argument can be convert to primitive. (Integer, Character, Boolean...)
     *  	Convert argument to primitive than check argument class is compatible for parameter class.
     *  	b) Argument is not compatible with parameter class.
     *  2) Otherwise, it will be checked that argument is instance of parameter class.  
     *  </p>
     *  
     * @param parameterClass is class which is required for parameter.
     * @param object is argument which we want to give for parameter.
     * @return
     */

    private static boolean isParamaterTypeCompatibleForArgument (Class<?> parameterClass, Object object) {
    	
        Class<?> argumentClass = object.getClass();
    	
    	    if (parameterClass.isPrimitive()) {
        	    	if (argumentClass == Integer.class) {
        	    		return parameterClass == int.class;
        	    	} else if (argumentClass == Character.class) {
        	    		return parameterClass == char.class;
        	    	} else if (argumentClass == Boolean.class) {
        	    		return parameterClass == boolean.class;
        	    	} else if (argumentClass == Byte.class) {
        	    		return parameterClass == byte.class;
        	    	} else if (argumentClass == Short.class) {
        	    		return parameterClass == short.class;
        	    	} else if (argumentClass == Long.class) {
        	    		return parameterClass == long.class;
        	    	} else if (argumentClass == Double.class) {
        	    		return parameterClass == double.class;
        	    	} else if (argumentClass == Void.class) {
        	    		return parameterClass == void.class;
        	    	} else if (argumentClass == Float.class) {
        	    		return parameterClass == float.class;
        	    	} else {
        	    		return false;
        	    	}
    	    } else {
    	        return parameterClass.isInstance(object);
    	    }
    }
    
    /**
     * <p>
     * Invokes the given constructor with the given arguments, and returns the resulting
     * object. It does not perform any checks to the given constructor and objects. If 
     * an exception is thrown, this method will throw an Error instead. The idea here is
     * that if we fail object instantiation we should better fail and fail fast.
     * </p>
     * 
     * @param <T> The type of the object being constructed.
     * @param constructor The constructor
     * @param args Arguments to be passed to the constructor.
     * @return the new object
     */
    
    private static <T> T invokeConstructor(Constructor<T> constructor, Object[] args) {
        try {
            return performInvokeConstructor(constructor, args);
        } catch (Exception e) {
            throw new Error("ObjectFactory.invokeConstructor got exception", e);
        } 
    }
    
    /**
     * Perform the actual call to the Constructor of type T with the given arguments.
     * 
     * The method Constructor.newInstance has varargs, that is, its signature is:
     * 
     * Constructor.newInstance(Object... args)
     * 
     * If you call this with an array of objects, it will not be unpacked to cover
     * all arguments, instead you would be calling the constructor with one argument
     * which happens to be an array of objects. I have not been able to find a notation
     * or "way" to unpack an array into an argument list. So this method does that properly
     * for up to nine arguments using a switch statement. 
     * 
     * If it receives more than nine arguments, it throws an error, because it is just 
     * like when object construction fails due to low memory.
     * 
     * @param <T> the type of the constructor
     * @param constructor the constructor to be invoked
     * @param args arguments to the constructor
     * @return the constructed object
     * @throws IllegalArgumentException if an incorrect number of arguments are passed, or an argument could not be converted by a widening conversion
     * @throws InstantiationException if the class can not be instantiated
     * @throws IllegalAccessException if this constructor is not accessible
     * @throws InvocationTargetException if an exception was thrown by the target constructor
     */
    
    private static <T> T performInvokeConstructor(Constructor<T> constructor, Object[] args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException
    {
        switch (args.length) {
        case 0:
            return constructor.newInstance();
        case 1:
            return constructor.newInstance(args[0]);
        case 2:
        	return constructor.newInstance(args[0], args[1]);
        case 3:
            return constructor.newInstance(args[0], args[1], args[2]);
        case 4:
            return constructor.newInstance(args[0], args[1], args[2], args[3]);
        case 5:
            return constructor.newInstance(args[0], args[1], args[2], args[3], args[4]);
        case 6:
            return constructor.newInstance(args[0], args[1], args[2], args[3], args[4], args[5]);
        case 7:
            return constructor.newInstance(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
        case 8:
            return constructor.newInstance(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7]);
        case 9:
            return constructor.newInstance(args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
        default:
            throw new Error("ObjectFactory.performInvokeConstructor can not call constructors with more than 9 arguments.");
        }
    }

    /**
     * <p>Construct a new object of the given type, using the given argument list as arguments to 
     * its constructor.</p>
     * 
     * <p>
     * This is the "main" method of the ObjectFactory class. The idea is, you need an instance of a class,
     * and instead of using the new operator, you call this method, essentially with the same arguments.
     * Why then? The point is, this method can give you an instance of a subclass instead of the original
     * class if that class has been previously mapped to another class by a call to mapClassToClass method.
     * In this way, it is possible to modify behavior -- for instance it would be easy to insert mocks
     * instead of the actual objects in this way.
     * </p>
     * 
     * <p>
     * Another use case is like a "regular" factory. You can ask for an instance of an interface rather
     * than a class using exactly the same semantics. In this case of course, it is mandatory to have the interface
     * class mapped to an actual class before this call is made. 
     * </p>
     * 
     * <p>
     * Addendum: There is a special case. If the given class is mapped directly to an object, using
     * the mapClassToObject method, that object is returned directly without considering any class
     * mappings which may be made for that class.
     * </p>
     * 
     * @param <T> the type of the object to be instantiated
     * @param clazz the class (may be a class or an interface) of the object to be instantiated
     * @param arguments the arguments to the constructor
     * @return the constructed object instance
     */
    
    @SuppressWarnings("unchecked")
    public static <T> T newObject(Class<? extends T> clazz, Object... arguments) {
        Object mappedObject = ObjectFactory.classToObjectMap.get(clazz);
        
        // Warning: Early return. If we have the class mapped to an object directly,
        // we return that object without doing anything else.
        
        if (mappedObject != null) {
            return (T)mappedObject;
        }
        
        Class<? extends T> actualClass = (Class<? extends T>)ObjectFactory.classToClassMap.get(clazz);
        
        if (actualClass == null) {
            actualClass = clazz;
        }
        
        Constructor<? extends T>[] constructors = (Constructor<? extends T>[])actualClass.getConstructors();
    	
        for (Constructor<? extends T> constructor : constructors) {
        	
        	if (isCompatible(constructor, arguments)) {
                return invokeConstructor(constructor, arguments);
            }
        }

        throw new Error("ObjectFactory.newObject() failed to create " + clazz.getCanonicalName() + " with given arguments. No matching constructor found.");
    }
   
    /**
     * This method causes clazz1 to be mapped to clazz2 so that when an instance of clazz1
     * is requested using the newObject() method, an instance of clazz2 will actually be
     * generated. clazz2 must be a subclass of clazz1, or in case clazz1 is actually an 
     * interface, clazz2 must implement clazz1.
     * 
     * Note that the mapping done here is not recursive. If you map a to b, and then map
     * b to c, a request for a will not generate c, it will just generate b.
     * 
     * @param <T> The type of clazz1
     * @param clazz1 the class mapped from
     * @param clazz2 the class mapped to
     */

    public static <T> void mapClassToClass(Class<T> clazz1, Class<? extends T> clazz2) {
        ObjectFactory.classToClassMap.put(clazz1, clazz2);
    }
    
    /**
     * <p>
     * This method causes clazz to be mapped to a specific given object. When an instance
     * of clazz is requested using the newObject() method, the given object will be returned.
     * Note that this effectively causes the object to be a singleton, unless the mapping is 
     * modified on-the-fly. This is essentially meant for two cases:
     * </p>
     * 
     * <p>
     * 1. The object is difficult to construct, or should not be constructed by the callers
     * for some reason or another.
     * </p>
     * 
     * <p>
     * 2. For testing purposes, one wishes to use mock objects with different settings for 
     * different tests. One can either modify the mapping to achieve this effect, or hold
     * on to the reference and make the modifications directly on the mapped object.
     * </p>
     *  
     * @param <T> The type of the object to be mapped
     * @param clazz the class to be mapped from
     * @param object the object to be mapped to
     */
    
    public static <T> void mapClassToObject(Class<T> clazz, T object) {
        ObjectFactory.classToObjectMap.put(clazz, object);
    }
    
    /**
     * <p>
     * Returns whether the given class is already mapped to an object.
     * </p>
     * @param clazz the class to be checked for mapping.
     * @return true if the class is mapped to an object, false otherwise.
     */
    
    public static boolean isClassMappedToObject(Class<?> clazz) {
    	return ObjectFactory.classToObjectMap.containsKey(clazz);
    }
    
    /**
     * This method clears the class to class map. 
     */
    
    public static void clearClassMap() {
        ObjectFactory.classToClassMap.clear();
    }
    
    /**
     * This method clears the class to object map.
     */
    
    public static void clearObjectMap() {
        ObjectFactory.classToObjectMap.clear();
    }
    
    
}
