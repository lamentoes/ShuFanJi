package net.windward.RoboRally;// Created by Windward Studios, Inc. (www.windward.net). No copyright claimed - do anything you want with this code.

import org.dom4j.DocumentException;

import java.io.IOException;

public interface IPlayerCallback
{

	/**
	 Adds a message to the status window.

	 @param message The message to add.
	*/
	void StatusMessage(String message);

	void IncomingMessage(String message) throws InterruptedException, DocumentException, IOException;

	void ConnectionLost(Exception ex) throws IOException, InterruptedException;
}