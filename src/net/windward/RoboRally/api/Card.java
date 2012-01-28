// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import org.dom4j.Element;
import java.util.ArrayList;
import java.util.List;

/**
 A card (move) for a robot.
*/
public final class Card
{
	/**
	 * The move for this card.
	 */
	public enum ROBOT_MOVE
	{
		/**
		 * Backup one square.
		 */
		BACKWARD_ONE(0),
		/**
		 * Forward one square.
		 */
		FORWARD_ONE(1),
		/**
		 * Forward two squares.
		 */
		FORWARD_TWO(2),
		/**
		 * Forward three squares.
		 */
		FORWARD_THREE(3),
		/**
		 * Rotate left (counterclockwise) 1/4 of a circle. Stay on the same square.
		 */
		ROTATE_LEFT(4),
		/**
		 * Rotate right (clockwise) 1/4 of a circle. Stay on the same square.
		 */
		ROTATE_RIGHT(5),
		/**
		 * Rotate to the flip direction. Stay on the same square.
		 */
		ROTATE_UTURN(6);

		private int intValue;
		private ROBOT_MOVE(int value)
		{
			intValue = value;
		}

		public int getValue()
		{
			return intValue;
		}

		public static ROBOT_MOVE forValue(int value)
		{
			return ROBOT_MOVE.values()[value];
		}
	}

	private ROBOT_MOVE move = ROBOT_MOVE.values()[0];
	private int priority;

	/**
	 * Copy constructor.
	 * @param src Copy from this object.
	 */
	public Card(Card src) {
		move = src.move;
		priority = src.priority;
	}

	/**
	 * Build from XML.
	 * @param element Initialize with the values in this object.
	 */
	public Card(Element element)
	{
		move =  ROBOT_MOVE.valueOf(element.attributeValue("move"));
		priority = Integer.parseInt(element.attributeValue("priority"));
	}

	/**
	 * Build a list of cards from the XML.
	 * @param element root XML element holding the cards.
	 * @return The extracted list of cards.
	 */
	public static List<Card> FromXML(Element element)
	{
		List<Card> allCards = new ArrayList<Card>();
		for (Object elemCard : element.elements("card"))
			allCards.add(new Card((Element)elemCard));
		return allCards;
	}

	/**
	 * The move for the card.
	 * @return The move for the card.
	 */
	public ROBOT_MOVE getMove()
	{
		return move;
	}

	/**
	 * The priority for the move. Higher priorities go first.
	 * @return The priority for the move. Higher priorities go first.
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * Create the XML of the turn.
	 * @param parent Create the XML of the turn.
	 * @return The XML for this card.
	 */
	public Element addElement(Element parent)
	{
		return parent.addElement("card").addAttribute("priority", Integer.toString(getPriority())).addAttribute("move", getMove().toString());
	}

	/**
	 * Displays the value of the object.
	 * @return The value of the object.
	*/
	@Override
	public String toString()
	{
		return String.format("%1$s : %2$s", getMove(), getPriority());
	}

	/**
	 * Clone it.
	 * @return A clone of this object.
	 */
	public Card clone()
	{
		return new Card(this);
	}
}