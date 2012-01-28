// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import org.dom4j.*;
import java.awt.*;

/**
 A location and direction on the board.
*/
public final class BoardLocation
{
	private Point mapPosition;
	private MapSquare.DIRECTION direction = MapSquare.DIRECTION.values()[0];

	/**
	 Create the object

	 @param mapPosition The board square located on.
	 @param direction The direction pointed to on the board.
	*/
	public BoardLocation(Point mapPosition, MapSquare.DIRECTION direction)
	{
		this.mapPosition = mapPosition;
		this.direction = direction;
	}

	/**
	 Copy constructor.

	 @param src Initialize with the values in this struct.
	*/
	public BoardLocation(BoardLocation src)
	{
		mapPosition = src.getMapPosition();
		direction = src.getDirection();
	}

	/**
	 Create from passed in XML.

	 @param element Initialize with the values in this object.
	*/
	public BoardLocation(Element element)
	{
		mapPosition = new Point(Integer.parseInt(element.attributeValue("x")), Integer.parseInt(element.attributeValue("y")));
		direction = MapSquare.DIRECTION.valueOf(element.attributeValue("direction"));
	}

	public Element addAttributes(Element element)
	{
		element.addAttribute("direction", direction.toString());
		element.addAttribute("x", Integer.toString(mapPosition.x));
		element.addAttribute("y", Integer.toString(mapPosition.y));
		return element;
	}

	/**
	 * The location on the map for this unit. Generally (-1, -1) if the unit is dead.
	 * @return The location on the map for this unit.
	 */
	public Point getMapPosition()
	{
		return mapPosition;
	}

	/**
	 * The direction this unit is facing. Ignored if the unit is dead.
	 * @return The direction this unit is facing.
	 */
	public MapSquare.DIRECTION getDirection()
	{
		return direction;
	}

	/**
	 * Return a new struct moved the requested number of squares in the location's direction. This can move off the
	 * board and is not blocked by units or walls.
	 * @param num The number of map squares to move.
	 * @return A new struct at the location moved to.
	*/
	public BoardLocation move(int num)
	{
		switch (getDirection())
		{
			case NORTH:
				return new BoardLocation(new Point(mapPosition.x, mapPosition.y - num), getDirection());
			case SOUTH:
				return new BoardLocation(new Point(mapPosition.x, mapPosition.y + num), getDirection());
			case EAST:
				return new BoardLocation(new Point(mapPosition.x + num, mapPosition.y), getDirection());
			case WEST:
				return new BoardLocation(new Point(mapPosition.x - num, mapPosition.y), getDirection());
		}
		return new BoardLocation(getMapPosition(), getDirection());
	}

	/**
	 * Rotate the direction in a clockwise direction (NESW) the number of turns requested. A value of 4 results in a full circle.
	 * @param num The number of quarter turns to make. Can be negative or positive. Can be outside the range of -3 .. 3.
	 * @return A new struct at the location turned to.
	 */
	public BoardLocation Rotate(int num)
	{
		int dir = (getDirection().getValue() + num);
		while (dir < 0)
			dir += 4;
		while (dir >= 4)
			dir -= 4;
		return new BoardLocation(getMapPosition(), MapSquare.DIRECTION.forValue(dir));
	}

	/**
	 * Equality operator.
	 * @param obj The BoardLocation to compare to.
	 * @return true if both objects are at the same position in the same direction.
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof BoardLocation))
			return false;
		BoardLocation loc = (BoardLocation) obj;
		return getDirection() == loc.getDirection() && getMapPosition() == loc.getMapPosition();
	}

	/**
	 * The hash code.
	 * @return The hash code.
	 */
	@Override
	public int hashCode()
	{
		return getDirection().hashCode() ^ getMapPosition().hashCode();
	}

	/**
	 * Displays the value of the object.
	 * @return The value of the object.
	 */
	@Override
	public String toString()
	{
		return String.format("%1$s - %2$s", getMapPosition(), getDirection());
	}

	/**
	 * Clone it.
	 * @return A clone of this object.
	 */
	public BoardLocation clone()
	{
		return new BoardLocation(this);
	}
}