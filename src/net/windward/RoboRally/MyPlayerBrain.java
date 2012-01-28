package net.windward.RoboRally;// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

import net.windward.RoboRally.api.*;
import sun.misc.BASE64Encoder;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

/**
 The sample C# AI. Start with this project but write your own code as this is a very simplistic implementation of the AI.
*/
public class MyPlayerBrain
{
	private static final int NUM_CARDS = 1;

	private String name = "James Gosling";
	private String password;
	private byte[] avatar;

	private final java.util.Random rand = new java.util.Random();

	/**
	 * Create the AI.
	 * @param name he name of the player.
	 * @param password The password of the player.
	 */
	public MyPlayerBrain(String name, String password) {
		this.name = name;
		this.password = password;
	}

	/**
	 * The name of the player.
	 * @return The name of the player.
	 */
	public String getName() {
		return name;
	}

	/**
	 * The password of the player.
	 * @return The password of the player.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * The avatar of the player. Must be 32 x 32.
	 * @return The avatar of the player. Must be 32 x 32.
	 */
	public byte[] getAvatar() throws IOException {

		if (avatar == null) {
			File file = new File("MyAvatar.png");
			if (file.exists()) {
				FileInputStream fisAvatar = new FileInputStream(file);
				avatar = new byte[fisAvatar.available()];
				fisAvatar.read(avatar, 0, avatar.length);
				System.out.println("Avatar loaded: " + file.getAbsolutePath());
}
			else
				System.out.println("Could not find avatar file: " + file.getAbsolutePath());
		}

		return avatar;
	}

	/**
	 Called when your robot must be placed on the board. This is called at the start of the game and each time your robot dies.

	 @param map The game map. There will be no units on this map.
	 @param you Your player object.
	 @param players All players (including you).
	 @param robotStart The position(s) on the map where you can place your robot. This will be a single point unless another robot is on your archive point.
	 @param gameStart true if start of a game. false if re-entering the game.
	 @return Where to place your unit (location and direction.
	*/
	public final BoardLocation Setup(GameMap map, Player you, java.util.List<Player> players, java.util.List<Point> robotStart, boolean gameStart)
	{
		return new BoardLocation(robotStart.get(0), MapSquare.DIRECTION.NORTH);
	}

	/**
	 Called each time the system needs another turn. If you do not return a valid turn, the game will randomly move one of your units.
	 This call must return in under 1 second. If it has not returned in 1 second the call will be aborted and a random move will be assigned.

	 @param map The game map with all units on it.
	 @param you Your player object. This is created for each call.
	 @param allPlayers All players including you. This is created for each call.
	 @param cards The cards you get to pick from. This does not include locked cards.
	 @return Your requested turn.
	*/
	public final PlayerTurn Turn(GameMap map, Player you, java.util.List<Player> allPlayers, java.util.List<Card> cards)
	{

		// if hurt bad, consider power down
		boolean powerDown = false;
		if ((you.getDamage() > 5) && (rand.nextInt(3) == 0))
			powerDown = true;

		// get 40 sets, pick the one that's closest to the flag
		Card[] best = null;
		int bestDiff = Integer.MAX_VALUE;
		int okDiff = rand.nextInt(3);
		FlagState fs = null;
		for (FlagState fsOn : you.getFlagStates())
			if (! fsOn.getTouched()) {
				fs = fsOn;
				break;
			}

		// get the flags, middle of the board if have them all.
		Point ptFlag = fs == null ? new Point(map.getWidth() / 2, map.getHeight() / 2) : fs.getPosition();
		for (int turnOn = 0; turnOn < 40; turnOn++)
		{
			// pick NUM_CARDS (or fewer if locked) random cards
			Card[] moveCards = new Card[NUM_CARDS];
			boolean[] cardUsed = new boolean[cards.size()];
			for (int ind = 0; ind < NUM_CARDS - you.getNumLockedCards(); ind++)
				for (int iter = 0; iter < 100; iter++) // in case can't work it with these cards
				{
					int index = rand.nextInt(cards.size());
					if (cardUsed[index])
						continue;
					moveCards[ind] = cards.get(index).clone();
					cardUsed[index] = true;
					break;
				}

			// add in the locked cards
			for (int ind = NUM_CARDS - you.getNumLockedCards(); ind < NUM_CARDS; ind++)
				moveCards[ind] = you.getCards().get(ind).clone();

			// run it
			Utilities.MovePoint mp = Utilities.CardDestination(map, you.getRobot().getLocation().clone(), moveCards);
			if (mp.getDead())
				continue;

			// if better than before, use it
			int diff = Math.abs(ptFlag.x - mp.getLocation().getMapPosition().x) + Math.abs(ptFlag.y - mp.getLocation().getMapPosition().y);
			if (diff <= okDiff)
			{
				java.util.ArrayList<Card> arrCards = new java.util.ArrayList<Card>();
				Collections.addAll(arrCards, moveCards);
				return new PlayerTurn(arrCards, powerDown);
			}
			if (diff < bestDiff)
			{
				bestDiff = diff;
				best = moveCards;
			}
		}

		if (best == null)
			return new PlayerTurn(new java.util.ArrayList<Card>(), powerDown);
		java.util.ArrayList<Card> arrCards = new java.util.ArrayList<Card>();
		Collections.addAll(arrCards, best);
		return new PlayerTurn(arrCards, powerDown);
	}
}