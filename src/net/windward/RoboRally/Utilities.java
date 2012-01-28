package net.windward.RoboRally;// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

import net.windward.RoboRally.api.*;

import java.awt.*;

/**
 net.windward.RoboRally.Utilities that can be of use to an AI.
*/
public final class Utilities
{

	/**
	 Destination for a movement. Ignores all robots on the map but does take into account walls, conveyor belts and gears. Returns
	 the final location of the move.

	 @param map The game map.
	 @param startLocation Where the unit starts.
	 @param cards The cards to apply.
	 @return The final location of the move.
	*/
	public static MovePoint CardDestination(GameMap map, BoardLocation startLocation, Card[] cards)
	{
		MovePoint[] points = CardPath(map, startLocation.clone(), cards);
		if ((points == null) || (points.length == 0))
		{
			TRAP.trap();
			return null;
		}
		return points[points.length - 1];
	}

	/**
	 Destination for a movement. Ignores all robots on the map but does take into account walls, conveyor belts and gears. Returns
	 every step of the move.

	 @param map The game map.
	 @param startLocation Where the unit starts.
	 @param cards The cards to apply.
	 @return Every step of the move.
	*/
	public static MovePoint[] CardPath(GameMap map, BoardLocation startLocation, Card[] cards)
	{
		// if we can't move, we end up where we started
		java.util.ArrayList<MovePoint> points = new java.util.ArrayList<MovePoint>(java.util.Arrays.asList(new MovePoint[] {new MovePoint(startLocation.clone())}));

		for (Card cardOn : cards)
		{
			// move robot
			MovePoint endLocation = Move(map, startLocation.clone(), cardOn.getMove());
			if (endLocation.getDead())
			{
				points.add(endLocation);
				return points.toArray(new MovePoint[]{});
			}
			if (!endLocation.getLocation().equals(startLocation.clone()))
			{
				startLocation = endLocation.getLocation().clone();
				points.add(new MovePoint(startLocation.clone()));
			}

			// conveyor belt - may cause a 1/4 turn.
			MapSquare sq = map.GetSquare(startLocation.getMapPosition());
			for (int speed = 1; (sq.getConveyor() != null) && (speed <= sq.getConveyor().getSpeed()); speed++)
			{
				endLocation = Move(map, startLocation.getMapPosition(), sq.getConveyor().getDirection());
				BoardLocation locMove = new BoardLocation(endLocation.getLocation().getMapPosition(), startLocation.getDirection());
				sq = map.GetSquare(endLocation.getLocation().getMapPosition());
				if (sq.getConveyor() != null)
				{
					MapSquare.DIRECTION dirEnter = MoveDirection(startLocation.getMapPosition(), endLocation.getLocation().getMapPosition());
					locMove = locMove.Rotate(sq.getConveyor().getDirection().getValue() - dirEnter.getValue()).clone();
				}
				startLocation = locMove.clone();
				points.add(new MovePoint(startLocation.clone()));
			}

			// gears
			if (sq.getType() == MapSquare.TYPE.ROTATE_CLOCKWISE)
			{
				startLocation = startLocation.Rotate(1).clone();
				points.add(new MovePoint(startLocation.clone()));
			}
			if (sq.getType() == MapSquare.TYPE.ROTATE_COUNTERCLOCKWISE)
			{
				startLocation = startLocation.Rotate(-1).clone();
				points.add(new MovePoint(startLocation.clone()));
			}

			// damage
			int damage = CalcLaserDamage(map, startLocation.clone());
			if (damage != 0)
			{
				points.get(points.size() - 1).setDamage(damage);
			}
		}

		return points.toArray(new MovePoint[]{});
	}

	/**
	 Calculates what damage a unit will receive from lasers at a given location.

	 @param map The game map.
	 @param location Where the unit is located.
	 @return The amount of damage. Will be 0 or 1.
	*/
	public static int CalcLaserDamage(GameMap map, BoardLocation location)
	{
		int damage = 0;
		damage += _CalcLaserDamage(map, location.getMapPosition(), 0, -1, MapSquare.DIRECTION.SOUTH, MapSquare.SIDE_NORTH, MapSquare.SIDE_SOUTH);
		damage += _CalcLaserDamage(map, location.getMapPosition(), 0, 1, MapSquare.DIRECTION.NORTH, MapSquare.SIDE_SOUTH, MapSquare.SIDE_NORTH);
		damage += _CalcLaserDamage(map, location.getMapPosition(), -1, 0, MapSquare.DIRECTION.EAST, MapSquare.SIDE_WEST, MapSquare.SIDE_EAST);
		damage += _CalcLaserDamage(map, location.getMapPosition(), 1, 0, MapSquare.DIRECTION.WEST, MapSquare.SIDE_EAST, MapSquare.SIDE_WEST);
		return damage;
	}

	private static int _CalcLaserDamage(GameMap map, Point position, int xAdd, int yAdd, MapSquare.DIRECTION laserDirection, int wallExit, int wallEnter)
	{
		int damage = 0;
		int x = position.x;
		int y = position.y;
		boolean startSquare = true;

		while ((0 <= x) && (x < map.getWidth()) && (0 <= y) && (y < map.getHeight()))
		{
			MapSquare sq = map.getSquares()[x][y];
			// can we move into this square?
			if ((!startSquare) && ((sq.getWalls() & wallEnter) != 0))
			{
				break;
			}
			startSquare = false;

			if ((sq.getLaser() != null) && (sq.getLaser().getLocation().getDirection() == laserDirection))
			{
				damage++;
				break;
			}

			// can we move out of this square?
			if ((sq.getWalls() & wallExit) != 0)
			{
				break;
			}
			x += xAdd;
			y += yAdd;
		}
		return damage;
	}

	// NESW
	private static final int [] sideMoveOut = {MapSquare.SIDE_NORTH, MapSquare.SIDE_EAST, MapSquare.SIDE_SOUTH, MapSquare.SIDE_WEST};
	private static final int [] sideMoveIn = {MapSquare.SIDE_SOUTH, MapSquare.SIDE_WEST, MapSquare.SIDE_NORTH, MapSquare.SIDE_EAST};

	/**
	 Move a unit one card move. Ignores all robots on the map but does take into account walls, conveyor belts and gears.

	 @param map The game map.
	 @param startLocation Where the unit starts.
	 @param move The move to apply.
	 @return The final location of the move.
	*/
	public static MovePoint Move(GameMap map, BoardLocation startLocation, Card.ROBOT_MOVE move)
	{
		int steps = 0;
		switch (move)
		{
			case BACKWARD_ONE:
				steps = -1;
				break;
			case FORWARD_ONE:
				steps = 1;
				break;
			case FORWARD_TWO:
				steps = 2;
				break;
			case FORWARD_THREE:
				steps = 3;
				break;
			case ROTATE_LEFT:
				return new MovePoint(startLocation.Rotate(-1).clone());
			case ROTATE_RIGHT:
				return new MovePoint(startLocation.Rotate(1).clone());
			case ROTATE_UTURN:
				return new MovePoint(startLocation.Rotate(2).clone());
		}

		MapSquare.DIRECTION dir = steps >= 0 ? startLocation.getDirection() : startLocation.Rotate(2).getDirection();
		Point position = startLocation.getMapPosition();
		while (steps != 0)
		{
			MovePoint mp = Move(map, position, dir);
			if (mp.getDead())
			{
				return new MovePoint(new BoardLocation(mp.getLocation().getMapPosition(), startLocation.getDirection()), true);
			}
			position = mp.getLocation().getMapPosition();
			int singleStep = Math.max(-1, Math.min(1, steps));
			steps -= singleStep;
		}
		return new MovePoint(new BoardLocation(position, startLocation.getDirection()));
	}

	/**
	 Move a unit one square in the requested direction. Ignores all robots on the map but does take into account walls, conveyor belts and gears.

	 @param map The game map.
	 @param position The map square to start the move from.
	 @param direction The direction to move.
	 @return The final location of the move.
	*/
	public static MovePoint Move(GameMap map, Point position, MapSquare.DIRECTION direction)
	{

		// watch for wall in this direction
		int sideExit = sideMoveOut[direction.getValue()];
		int sideEnter = sideMoveIn[direction.getValue()];
		BoardLocation location = new BoardLocation(position, direction);

		// can we exit this square?
		MapSquare sq = map.GetSquare(position);
		if ((sq.getWalls() & sideExit) != 0)
			return new MovePoint(location.clone());
		BoardLocation moveTo = location.move(1).clone();

		// did we go off the board?
		if ((moveTo.getMapPosition().x < 0) || (map.getWidth() <= moveTo.getMapPosition().x) || (moveTo.getMapPosition().y < 0) || (map.getHeight() <= moveTo.getMapPosition().y))
			return new MovePoint(location.clone(), true);

		if (map.GetSquare(moveTo.getMapPosition()).getType() == MapSquare.TYPE.PIT)
			return new MovePoint(moveTo.clone(), true);

		// can we enter the new square?
		sq = map.GetSquare(moveTo.getMapPosition());
		if ((sq.getWalls() & sideEnter) != 0)
		{
			return new MovePoint(location.clone());
		}

		return new MovePoint(moveTo.clone());
	}

	public static MapSquare.DIRECTION MoveDirection(Point start, Point end)
	{
		if (start.y > end.y)
		{
			return MapSquare.DIRECTION.NORTH;
		}
		if (start.y < end.y)
		{
			return MapSquare.DIRECTION.SOUTH;
		}
		if (start.x > end.x)
		{
			return MapSquare.DIRECTION.WEST;
		}
		if (start.x < end.x)
		{
			return MapSquare.DIRECTION.EAST;
		}
		throw new IllegalArgumentException("start = end point " + start);
	}

	/**
	 The result of one of the net.windward.RoboRally.Utilities Move methods.
	*/
	public static class MovePoint
	{
		private BoardLocation privateLocation;
		private int privateDamage;
		private boolean privateDead;

		/**
		 Create the object. No damage and not dead.

		 @param location The location of the move result.
		*/
		public MovePoint(BoardLocation location)
		{
			setLocation(location.clone());
			setDamage(0);
			setDead(false);
		}

		/**
		 Create the object. Not dead.

		 @param location The location of the move result.
		 @param damage The damage level from this move.
		*/
		public MovePoint(BoardLocation location, int damage)
		{
			setLocation(location.clone());
			setDamage(damage);
		}

		/**
		 Create the object. No damage.

		 @param location The location of the move result.
		 @param dead true if the move caused death.
		*/
		public MovePoint(BoardLocation location, boolean dead)
		{
			setLocation(location.clone());
			setDead(dead);
		}

		/**
		 The location of the move result.
		*/
		public final BoardLocation getLocation()
		{
			return privateLocation;
		}
		private void setLocation(BoardLocation value)
		{
			privateLocation = value.clone();
		}

		/**
		 The damage level received from this move.
		*/
		public final int getDamage()
		{
			return privateDamage;
		}
		public final void setDamage(int value)
		{
			privateDamage = value;
		}

		/**
		 true if the move caused the unit to die (moved off the board).
		*/
		public final boolean getDead()
		{
			return privateDead;
		}
		private void setDead(boolean value)
		{
			privateDead = value;
		}
	}
}