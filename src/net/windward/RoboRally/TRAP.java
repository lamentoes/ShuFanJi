package net.windward.RoboRally;// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

/**
 Used to set code coverage breakpoints in the code in DEBUG mode only.
*/
public class TRAP extends RuntimeException {

	public static boolean debugMode = true;

	/**
	 * Throws us into the debugger.
	 */
	static public void trap() {

		if (! debugMode)
			return;

		// hit the debugger
		try { throw new TRAP(); } catch (TRAP tr) { /*empty*/ } }

	static public void trap(boolean doBreak) {

		if (! debugMode)
			return;

		// hit the debugger
		try { if (doBreak) throw new TRAP(); } catch (TRAP tr) { /*empty*/ } }
}