// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import net.windward.RoboRally.TRAP;
import org.dom4j.Element;

import java.util.StringTokenizer;

/**
 An individual square in the map.
*/
public class MapSquare
{
	private TYPE sqType = TYPE.values()[0];
	private int walls = SIDE_NONE;
	private Conveyor conveyor;
	private Laser laser;
	private int flag;

	/**
	 The direction a robot or laser is facing. Also used for direction a conveyor is running (exit for a turn).
	*/
	public enum DIRECTION
	{
		/**
		 Facing North and can fire on units above.
		*/
		NORTH(0),
		/**
		 Facing East and can fire on units to the right.
		*/
		EAST(1),
		/**
		 Facing South and can fire on units below.
		*/
		SOUTH(2),
		/**
		 Facing West and can fire on units to the left.
		*/
		WEST(3);

		private int intValue;
		private DIRECTION(int value)
		{
			intValue = value;
		}
		public int getValue()
		{
			return intValue;
		}
		public static DIRECTION forValue(int value)
		{
			return DIRECTION.values()[value];
		}
	}

	/**
	 Which side(s) of the square have a wall. A square can have multiple walls. Note, couldn't figure out how to handle
	 to/from string for bitmask enums.
	*/
	public static final int SIDE_NONE = 0;
	public static final int SIDE_NORTH = 0x01;
	public static final int SIDE_EAST = 0x02;
	public static final int SIDE_SOUTH = 0x04;
	public static final int SIDE_WEST = 0x08;
	private static final String [] sideNames = {"NORTH", "EAST", "SOUTH", "WEST"};

	public static int parseSides(String sides) {

		int rtn = 0;
		StringTokenizer tok = new StringTokenizer(sides, ",");
		while (tok.hasMoreTokens()) {
			String item = tok.nextToken().trim();
			int bitMask = 0x01;
			for (int ind=0; ind<sideNames.length; ind++, bitMask <<= 1)
				if (sideNames[ind].equals(item)) {
					rtn |= bitMask;
					break;
				}
		}
		return rtn;
	}

	/**
	 What type of square it is.
	*/
	public enum TYPE
	{
		/**
		 Normal - nothing on it.
		*/
		NORMAL,
		/**
		 Has a conveyor belt on it.
		*/
		CONVEYOR_BELT,
		/**
		 Is a gear that rotates clockwise.
		*/
		ROTATE_CLOCKWISE,
		/**
		 Is a gear that rotates counter-clockwise.
		*/
		ROTATE_COUNTERCLOCKWISE,
		/**
		 Is a repair square.
		*/
		REPAIR,
		/**
		 Is a flag.
		*/
		FLAG,
		/**
		 * Is a pit.
		 */
		PIT;

		public int getValue()
		{
			return this.ordinal();
		}

		public static TYPE forValue(int value)
		{
			return values()[value];
		}
	}

	/**
	 Copy constructor.

	 @param element Serialized XML of this object..
	*/
	public MapSquare(Element element)
	{
		setType(TYPE.valueOf(element.attributeValue("type")));
		setWalls(parseSides(element.attributeValue("walls")));
		setFlag(Integer.parseInt(element.attributeValue("flag")));

		Element elemConveyor = element.element("conveyor");
		setConveyor(elemConveyor == null ? null : new Conveyor(elemConveyor));

		Element elemLaser = element.element("laser");
		setLaser(elemLaser == null ? null : new Laser(elemLaser));
	}

	/**
	 The type of square.
	*/
	public final TYPE getType()
	{
		return sqType;
	}
	protected final void setType(TYPE value)
	{
		sqType = value;
	}

	/**
	 What sides (if any) have walls.
	*/
	public final int getWalls()
	{
		return walls;
	}
	protected final void setWalls(int value)
	{
		walls = value;
	}

	/**
	 The conveyor belt on this square. null if no conveyor.
	*/
	public final Conveyor getConveyor()
	{
		return conveyor;
	}
	protected final void setConveyor(Conveyor value)
	{
		conveyor = value;
	}

	/**
	 The laser on this square. null if no laser.
	*/
	public final Laser getLaser()
	{
		return laser;
	}
	protected final void setLaser(Laser value)
	{
		laser = value;
	}

	/**
	 The flag number (1..3) on this square. 0 if no flag.
	*/
	public final int getFlag()
	{
		return flag;
	}
	protected final void setFlag(int value)
	{
		flag = value;
	}
}