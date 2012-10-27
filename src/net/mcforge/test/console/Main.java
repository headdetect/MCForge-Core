/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.test.console;

import java.util.Scanner;

import net.mcforge.API.EventHandler;
import net.mcforge.API.Listener;
import net.mcforge.API.io.ServerLogEvent;
import net.mcforge.API.server.ServerChatEvent;
import net.mcforge.chat.ChatColor;
import net.mcforge.chat.Messages;
import net.mcforge.groups.Group;
import net.mcforge.server.Server;
import net.mcforge.system.Console;

public class Main extends Console implements Listener {
	Server s;
	final Scanner scan = new Scanner(System.in);
	public static void main(String[] args) {
		new Main().start();
	}

	@Override
	public void sendMessage(String message) {
		s.Log(message);
	}
	
	public void start() {
		s = new Server("Test", 25565, "Test");
		Messages m = new Messages(s);
		s.Start((Console)this, true);
		Scanner scanner = new Scanner(System.in);
		while (s.Running) {
			String line = scanner.nextLine();
			if (line.startsWith("/")) {
				line = line.substring(1); //Get rid of the / at the beginning
				if (line.split("\\ ").length > 1)
					s.getCommandHandler().execute(this, line.split("\\ ")[0], line.substring(line.indexOf(line.split("\\ ")[1])));
				else
					s.getCommandHandler().execute(this, line, "");
			}
			else {
				ServerChatEvent event = new ServerChatEvent(this, line);
				s.getEventSystem().callEvent(event);
				if (event.isCancelled())
					continue;
				m.serverBroadcast(ChatColor.Purple + "[Server] " + ChatColor.White + line);
				s.Log("[Server] " + line);
			}
		}
		System.out.println("Server stopped..");
		scanner.close();
		System.exit(0);
	}

	@Override
	public Server getServer() {
		return s;
	}

	@Override
	public Group getGroup() {
		return Group.getGroupList().get(Group.getGroupList().size() - 1);
	}

	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public String next() {
		return scan.next();
	}
	
	@EventHandler
	public void onLog(ServerLogEvent event) {
		
	}
}
