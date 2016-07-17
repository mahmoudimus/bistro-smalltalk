//====================================================================
// Smalltalk.java
//====================================================================

package smalltalk;
import java.util.Hashtable;

/**
* Provides runtime information to the Bistro Smalltalk system.
* @author Copyright 1999,2000 Nikolas S. Boyd. All rights reserved.
*/
public class Smalltalk
{
	/**
	* Indicates whether the Bistro runtime environment is active.
	*/
	public static boolean running = true;

	/** Maps each class name to the class. */
	public static Hashtable classRegistry = new Hashtable();

	/** Registers (aClass) as a Bistro class. */
	public static void registerClass( Class primitiveClass, Object aClass ) {
		classRegistry.put( primitiveClass, aClass );
	}
}