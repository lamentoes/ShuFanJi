// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import net.windward.RoboRally.TRAP;
import org.dom4j.Element;

/**
 A conveyor belt. This is an element of a map square and represents the conveyor belt on that single square.
*/
public class Conveyor
{
	private int speed;
	private MapSquare.DIRECTION direction = MapSquare.DIRECTION.values()[0];
	private int entry = MapSquare.SIDE_NONE;

	/**
	 Build from XML serialization of this object.

	 @param element Initialize with the values in this object.
	*/
	public Conveyor(Element element)
	{
		setSpeed(Integer.parseInt(element.attributeValue("speed")));
		setDirection(MapSquare.DIRECTION.valueOf(element.attributeValue("direction")));
		setEntry(MapSquare.parseSides(element.attributeValue("entry")));
	}

	/**
	 The speed of the belt. Values are 1 or 2.
	*/
	public final int getSpeed()
	{
		return speed;
	}
	protected final void setSpeed(int value)
	{
		speed = value;
	}

	/**
	 The direction the belt exits at.
	*/
	public final MapSquare.DIRECTION getDirection()
	{
		return direction;
	}
	protected final void setDirection(MapSquare.DIRECTION value)
	{
		direction = value;
	}

	/**
	 The direction(s) the conveyor belt enters from.
	*/
	public final int getEntry()
	{
		return entry;
	}
	protected final void setEntry(int value)
	{
		entry = value;
	}
}