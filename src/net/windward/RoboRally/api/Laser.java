// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.
package net.windward.RoboRally.api;

import net.windward.RoboRally.TRAP;
import org.dom4j.*;

/**
 A laser on the board. This sits on a specific map square.
*/
public class Laser extends CombatUnit
{
	private int numSquares;

	/**
	 Copy constructor.

	 @param element Initialize with the values in this XML.
	*/
	public Laser(Element element)
	{
		super(element);
		setNumSquares(Integer.parseInt(element.attributeValue("num-squares")));
	}

	/**
	 The number of squares the laser shoots over, including the one it is on. (The Laser is ended by a wall.)
	*/
	public final int getNumSquares()
	{
		return numSquares;
	}
	private void setNumSquares(int value)
	{
		numSquares = value;
	}
}