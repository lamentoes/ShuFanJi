package net.windward.RoboRally;// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

import java.awt.*;
import java.awt.print.Book;
import java.io.*;
import java.util.*;
import sun.misc.BASE64Encoder;
import net.windward.RoboRally.api.*;
import org.dom4j.*;
import org.dom4j.io.*;

public class Framework implements IPlayerCallback
{
	private TcpClient tcpClient;
	private MyPlayerBrain brain;
	private String ipAddress = "127.0.0.1";

	public static void main(String[] args) throws IOException {
		Framework framework = new Framework(args);
		framework.Run();
	}

	private Framework(String[] args)
	{
		brain = new MyPlayerBrain(args.length >= 2 ? args[1] : null, args.length >= 3 ? args[2] : null);
		if (args.length >= 1)
			ipAddress = args[0];
		System.out.println(String.format("Connecting to server %1$s for user: %2$s, password: %3$s", ipAddress, brain.getName(), brain.getPassword()));
	}

	private void Run() throws IOException {
		System.out.println("starting...");

		tcpClient = new TcpClient(this, ipAddress);
		tcpClient.Start();
		ConnectToServer();

		// It's all messages to us now.
		System.out.println("enter \"exit\" to exit program");
		while (true)
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String line = in.readLine();
			if (line.equals("exit"))
			{
				System.out.println("Exiting program...");
				tcpClient.abort();
				return;
			}
		}
	}

	public final void StatusMessage(String message)
	{
		TRAP.trap();
		System.out.println(message);
	}

	public final void IncomingMessage(String message) throws InterruptedException, DocumentException, IOException {

		try
		{
			long startTime = System.currentTimeMillis();

			// get the xml - we assume we always get a valid message from the server.
			SAXReader reader = new SAXReader();
			Document xml = reader.read(new StringReader(message));

			String rootName = xml.getRootElement().getName();

			if (rootName.equals("start-position"))
			{
				java.util.List<Player> allPlayers = Player.FromXML(xml.getRootElement().element("players"));
				BoardLocation start = brain.Setup(new GameMap(xml.getRootElement().element("map")),
						getMyPlayer(xml.getRootElement().element("players").attributeValue("your-guid"), allPlayers),
						allPlayers,
						getPoints(xml.getRootElement().element("points")),
						xml.getRootElement().attributeValue("game-start").toLowerCase().equals("true"));

				System.out.println(String.format("Starting robot at: %1$s", start.clone()));
				Document doc = DocumentHelper.createDocument();
				Element elem = doc.addElement("start-position");
				start.addAttributes(elem);
				tcpClient.SendMessage(documentToString(doc));
			}
			else if (rootName.equals("turn"))
			{
				int turnOn = Integer.parseInt(xml.getRootElement().attributeValue("turn"));
				// not used in this code, but you may wish to.
				boolean  repairSitesOn = Boolean.parseBoolean(xml.getRootElement().attributeValue("repair-on"));

				java.util.List<Player> allPlayers = Player.FromXML(xml.getRootElement().element("players"));
				PlayerTurn turn = brain.Turn(new GameMap(xml.getRootElement().element("map")),
						getMyPlayer(xml.getRootElement().element("players").attributeValue("your-guid"), allPlayers),
						allPlayers, Card.FromXML(xml.getRootElement().element("cards")));

				System.out.println(String.format("Turn: %1$s - %2$s", turnOn, turn));
				Document doc = DocumentHelper.createDocument();
				turn.addElement(doc).addAttribute("turn", Integer.toString(turnOn));
				tcpClient.SendMessage(documentToString(doc));
			}
			else
			{
				TRAP.trap();
				System.out.println(String.format("ERROR: bad message (XML) from server - root node %1$s", rootName));
			}

			long diffTime = System.currentTimeMillis() - startTime;
			if (diffTime > 800)
				System.out.println(String.format("WARNING, turn took %1$f seconds", ((float)diffTime) / 1000f));
		}
		catch (RuntimeException ex)
		{
			ex.printStackTrace();
			TRAP.trap();
			System.out.println(String.format("Error on incoming message. Exception: %1$s", ex));
		}
	}

	private static Player getMyPlayer(String guid, java.util.List<Player> players) {

		for (Player plyrOn : players)
			if (plyrOn.getGuid().equals(guid))
				return plyrOn;
		return null;
	}

	private static java.util.List<Point> getPoints(Element element)
	{
		java.util.List<Point> allPoints = new ArrayList<Point>();
		for (Object objPoint : element.elements("position")) {
			Element elemPoint = (Element) objPoint;
			allPoints.add(new Point(Integer.parseInt(elemPoint.attributeValue("x")), Integer.parseInt(elemPoint.attributeValue("y"))));
		}
		return allPoints;
	}

	public final void ConnectionLost(Exception ex) throws IOException, InterruptedException {

		System.out.println("Lost our connection! Exception: " + ex.getMessage());

		int delay = 500;
		while (true)
		{
			try
			{
				if (tcpClient != null)
					tcpClient.Close();
				tcpClient = new TcpClient(this, ipAddress);
				tcpClient.Start();

				ConnectToServer();
				System.out.println("Re-connected");
				return;
			}
			catch (RuntimeException e)
			{
				TRAP.trap();
				System.out.println("Re-connection fails! Exception: " + e.getMessage());
				Thread.sleep(delay);
				delay += 500;
			}
		}
	}

	private void ConnectToServer() throws IOException {

		Document doc = DocumentHelper.createDocument();

		Element root = doc.addElement("join").addAttribute("password", brain.getPassword()).addAttribute("name", brain.getName());

		// avatar is optional
		byte [] data = brain.getAvatar();
		if (data != null) {
			TRAP.trap();
			BASE64Encoder encoder = new BASE64Encoder();
			root.addElement("avatar", encoder.encode(data));
		}

		tcpClient.SendMessage(documentToString(doc));
	}

	private static String documentToString(Document doc) throws IOException {

		// get the XML as a string
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setTrimText(false);
		format.setIndent(true);
		format.setNewlines(true);
		format.setEncoding("UTF-8");
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		XMLWriter writer = new XMLWriter(outStream, format);
		writer.write(doc);
		writer.flush();
		return new String(outStream.toByteArray());
	}
}