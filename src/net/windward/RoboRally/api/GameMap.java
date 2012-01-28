// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

package net.windward.RoboRally.api;

import net.windward.RoboRally.TRAP;
import org.dom4j.Element;

import java.awt.*;

/**
 The map of the game.
*/
public class GameMap
{

	private MapSquare [][] squares;
	private Point[] flags;

	/**
	 Create the map from the passed across XML

	 @param element The parent map node.
	*/
	public GameMap(Element element)
	{
		int width = Integer.parseInt(element.attributeValue("width"));
		int height = Integer.parseInt(element.attributeValue("height"));
		squares = new MapSquare[width][];
		for (int x = 0; x < width; x++)
			getSquares()[x] = new MapSquare[height];

		for (Object objSq : element.selectNodes("square")) {
			Element elemSq = (Element) objSq;
			int x = Integer.parseInt(elemSq.attributeValue("x"));
			int y = Integer.parseInt(elemSq.attributeValue("y"));
			getSquares()[x][y] = new MapSquare(elemSq);
		}
	}

	/**
	 The width of the map. Units are squares.
	*/
	public final int getWidth()
	{
		return getSquares().length;
	}

	/**
	 The height of the map. Units are squares.
	*/
	public final int getHeight()
	{
		return getSquares()[0].length;
	}

	/**
	 The map squares. This is in the format [x][y].
	*/
	public final MapSquare[][] getSquares()
	{
		return squares;
	}

	/**
	 Location (map squares) of the flags in order.
	*/
	public final Point[] getFlags()
	{
		return flags;
	}
	protected final void setFlags(Point[] value)
	{
		flags = value;
	}

	/**
	 Get a specific map square.

	 @param location The x, y point for the square desired.
	 @return The requested square.
	*/
	public final MapSquare GetSquare(Point location)
	{
		return getSquares()[location.x][location.y];
	}

}