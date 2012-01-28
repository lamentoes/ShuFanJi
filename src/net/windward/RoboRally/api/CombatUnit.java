// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import org.dom4j.Element;

/**
 Any unit that fires (Robot and Laser) inherits from this class.
*/
public abstract class CombatUnit
{
	private BoardLocation location;

	/**
	 * Create from passed in XML..
	 * @param element Initialize with the values in this object.
	 */
	protected CombatUnit(Element element)
	{
		setLocation(new BoardLocation(element));
	}

	/**
	 * The location on the map for this unit. Ignored if the unit is dead.
	 * @return The location on the map for this unit. Ignored if the unit is dead.
	 */
	public final BoardLocation getLocation()
	{
		return location;
	}

	/**
	 * The location on the map for this unit. Ignored if the unit is dead.
	 * @param value The location on the map for this unit. Ignored if the unit is dead.
	 */
	public final void setLocation(BoardLocation value)
	{
		location = value.clone();
	}
}