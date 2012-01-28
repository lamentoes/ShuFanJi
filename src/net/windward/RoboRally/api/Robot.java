// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import org.dom4j.Element;

/**
 A player's robot.
*/
public class Robot extends CombatUnit
{
	/**
	 Build from XML.

	 @param elemOn Initialize with the values in this object.
	*/
	public Robot(Element elemOn)
	{
		super(elemOn);
	}
}