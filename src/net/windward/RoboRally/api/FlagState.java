// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import net.windward.RoboRally.TRAP;
import org.dom4j.Element;

import java.awt.*;
import java.util.*;

/**
 The state of the flags for a player.
*/
public class FlagState
{
	private Point position;
	private boolean touched;

	/**
	 Build from XML.

	 @param element Initialize with the values in this object.
	*/
	public FlagState(Element element)
	{
		setPosition(new Point(Integer.parseInt(element.attributeValue("x")), Integer.parseInt(element.attributeValue("y"))));
		setTouched(element.attributeValue("touched").toLowerCase().equals("true"));
	}

	public static java.util.List<FlagState> FromXML(Element element)
	{
		java.util.List<FlagState> allFlags = new ArrayList<FlagState>();
		for (Object elemFlag : element.elements("flag-state"))
			allFlags.add(new FlagState((Element)elemFlag));
		return allFlags;
	}

	/**
	 Which square the flag is on.
	*/
	public final Point getPosition()
	{
		return position;
	}
	protected final void setPosition(Point value)
	{
		position = value;
	}

	/**
	 true if the flag has been touched. Once all 3 flags are touched a player has won.
	*/
	public final boolean getTouched()
	{
		return touched;
	}
	public final void setTouched(boolean value)
	{
		touched = value;
	}
}