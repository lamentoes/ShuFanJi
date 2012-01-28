// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import net.windward.RoboRally.TRAP;
import org.dom4j.Element;

import java.awt.*;
import java.util.*;

/**
 A player in the game.
*/
public class Player
{
	private String guid;
	private java.util.List<Card> cards;
	private int lives;
	private int damage;
	private int score;
	private int numLocked;
	private Point archive;
	private Robot robot;
	private POWER_MODE powerMode = POWER_MODE.values()[0];
	private java.util.List<FlagState> flagStates;

	/**
	 What mode the player/robot is in.
	*/
	public enum MODE
	{
		/**
		 Standard mode - it is moving around the board.
		*/
		ACTIVE,

		/**
		 It is powered down - static on the board.
		*/
		POWER_DOWN,

		/**
		 It is destroyed - will return next turn.
		*/
		DESTROYED,

		/**
		 It is dead - lost all 3 life tokens.
		*/
		DEAD;

		public int getValue()
		{
			return this.ordinal();
		}

		public static MODE forValue(int value)
		{
			return values()[value];
		}
	}

	/**
	 The power down mode for this player.
	*/
	public enum POWER_MODE
	{
		/**
		 Running (not down, not announced).
		*/
		UP,

		/**
		 Announced, will power down at the end of the turn.
		*/
		ANNOUNCED,

		/**
		 Powered down.
		*/
		DOWN;

		public int getValue()
		{
			return this.ordinal();
		}

		public static POWER_MODE forValue(int value)
		{
			return values()[value];
		}
	}

	/**
	 Create from the XML.

	 @param element Initialize with the values in this object.
	*/
	public Player(Element element)
	{
		setGuid(element.attributeValue("guid"));
		setLives(Integer.parseInt(element.attributeValue("lives")));
		setDamage(Integer.parseInt(element.attributeValue("damage")));
		setScore(Integer.parseInt(element.attributeValue("score")));
		setNumLockedCards(Integer.parseInt(element.element("cards").attributeValue("num-locked")));
		setPowerMode(POWER_MODE.valueOf(element.attributeValue("power-mode")));
		setArchive(new Point(Integer.parseInt(element.attributeValue("archive-x")), Integer.parseInt(element.attributeValue("archive-y"))));

		setRobot(new Robot(element.element("robot")));
		setFlagStates(FlagState.FromXML(element.element("flags")));
		setCards(Card.FromXML(element.element("cards")));
	}

	public static java.util.List<Player> FromXML(Element element)
	{
		java.util.List<Player> allPlayers = new ArrayList<Player>();
		for (Object elemPlayer : element.elements("player"))
			allPlayers.add(new Player((Element)elemPlayer));
		return allPlayers;
	}

	/**
	 The unique identifier for this player. This will remain constant for the length of the game (while the Player objects passed will
	 change on every call).
	*/
	public final String getGuid()
	{
		return guid;
	}
	private void setGuid(String value)
	{
		guid = value;
	}

	/**
	 The Cards for this player.
	*/
	public final java.util.List<Card> getCards()
	{
		return cards;
	}
	public final void setCards(java.util.List<Card> value)
	{
		cards = value;
	}

	/**
	 The number of lives this player has. Starts at 3 and when it is 0 the player is dead and removed from the board.
	*/
	public final int getLives()
	{
		return lives;
	}
	protected final void setLives(int value)
	{
		lives = value;
	}

	/**
	 The level of damage this player has. Starts at 0, cards start locking at 5, and the player dies (Lives decreases by
	 one) when it hits 10.
	*/
	public final int getDamage()
	{
		return damage;
	}
	protected final void setDamage(int value)
	{
		damage = value;
	}

	/**
	 The location the players robot will re-enter on if it dies.
	*/
	public final Point getArchive()
	{
		return archive;
	}
	protected final void setArchive(Point value)
	{
		archive = value;
	}

	/**
	 The player's robot.
	*/
	public final Robot getRobot()
	{
		return robot;
	}
	private void setRobot(Robot value)
	{
		robot = value;
	}

	/**
	 The power down mode for the player.
	*/
	public final POWER_MODE getPowerMode()
	{
		return powerMode;
	}
	protected final void setPowerMode(POWER_MODE value)
	{
		powerMode = value;
	}

	/**
	 The score for this player.
	*/
	public final int getScore()
	{
		return score;
	}

	/**
	 The score for this player.
	*/
	private void setScore(int score) {
		this.score = score;
	}

	/**
	 true if the player's robot is visible (on the map). False if it is dead or destroyed.
	*/
	public final boolean getIsVisible()
	{
		return getMode() != MODE.DEAD && getMode() != MODE.DESTROYED;
	}

	/**
	 The mode this player is in.
	*/
	public final MODE getMode()
	{
		if (getLives() <= 0)
		{
			return MODE.DEAD;
		}
		if (getDamage() >= 10)
		{
			return MODE.DESTROYED;
		}
		if (getPowerMode() == POWER_MODE.DOWN)
		{
			return MODE.POWER_DOWN;
		}
		return MODE.ACTIVE;
	}

	/**
	 The number of the player's cards that are locked. Locked cards are the last N cards from the previous turn.
	*/
	public final int getNumLockedCards()
	{
		return numLocked;
	}
	private void setNumLockedCards(int numLocked) {
		this.numLocked = numLocked;
	}

	/**
	 Where the flags are and if they've been touched.
	*/
	public final java.util.List<FlagState> getFlagStates()
	{
		return flagStates;
	}
	protected final void setFlagStates(java.util.List<FlagState> value)
	{
		flagStates = value;
	}
}