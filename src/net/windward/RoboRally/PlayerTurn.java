package net.windward.RoboRally;// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

import net.windward.RoboRally.api.*;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 A requested move for a player's turn.
*/
public class PlayerTurn
{
	private java.util.List<Card> privateCards;
	private boolean privateIsPowerDown;

	/**
	 Return your turn. If you have locked cards will only use the first N (unlocked) cards returned. If you do not return
	 enough cards, random cards will be assigned. Locked cards are the last N cards from the previous turn.

	 @param cards Your unlocked cards.
	 @param powerDown true if power down at the end of this turn.
	*/
	public PlayerTurn(java.util.List<Card> cards, boolean powerDown)
	{
		setCards((cards != null) ? cards : new java.util.ArrayList<Card>());
		setIsPowerDown(powerDown);
	}

	/**
	 The cards requested for the upcoming turn. If your Damage is greater than 4 then not all 5 of these will be used, but you
	 may return 5. The extras will be ignored.
	*/
	public final java.util.List<Card> getCards()
	{
		return privateCards;
	}
	private void setCards(java.util.List<Card> value)
	{
		privateCards = value;
	}

	/**
	 Return true to power down at the end of the upcoming turn. You also need to return Cards because the power down occurs
	 AFTER this turn!
	*/
	public final boolean getIsPowerDown()
	{
		return privateIsPowerDown;
	}
	private void setIsPowerDown(boolean value)
	{
		privateIsPowerDown = value;
	}

	/**
	 Create the XML of the turn.
	*/
	public final Element addElement(Document parent)
	{
		Element xmlTurn = parent.addElement("turn").addAttribute("power-down", Boolean.toString(getIsPowerDown()));
		Element xmlCards = xmlTurn.addElement("cards");
		for (Card cardOn : getCards())
			cardOn.addElement(xmlCards);
		return xmlTurn;
	}

	/**
	 User friendly display.

	 @return User friendly display.
	*/
	@Override
	public String toString()
	{
		StringBuilder buf = new StringBuilder("Turn: Cards[");
		for (Card cardOn : getCards())
			buf.append(cardOn + ", ");
		if (getCards().size() > 0)
			buf.delete(buf.length() - 2, buf.length());
		buf.append(']');
		if (getIsPowerDown())
			buf.append(" PowerDown");
		return buf.toString();
	}
}