/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.API.plugin;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import net.mcforge.API.CommandExecutor;
import net.mcforge.API.action.Action;
import net.mcforge.iomodel.Player;
import net.mcforge.server.Server;
import net.mcforge.system.Console;

public class CommandHandler {
    public ArrayList<Command> commands = new ArrayList<Command>();
    
    private Server _server;
    
    public CommandHandler(Server server) {
        this._server = server;
    }
    
    /**
     * Execute a command. This will check permissions to see if <b>player</b>
     * can execute the command based on the group the <b>player</b> is in.
     * @param player
     *              The client that is executing the command
     * @param name
     *            The name of the command
     * @param message
     *               The append arguments as a String
     */
    public void execute(CommandExecutor player, String name, String message) {
        execute(player, name, message.split(" "));
    }
    
    /**
     * Find a command based on the name.
     * This will also look for shortcuts
     * @param name
     *           The name/shortcut of the command
     * @return
     *        The {@link Command} object
     */
    public Command find(String name) {
        for (Command c : commands) {
            if (name.equalsIgnoreCase(c.getName()))
                return c;
            else {
                for (String shortcut : c.getShortcuts()) {
                    if (name.equalsIgnoreCase(shortcut))
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Have <b>player</b> execute a command. This method will
     * check for group permissions, if the <b>player</b> can't
     * execute the command, the player will be notified. 
     * @param player
     *              The player executing the command
     * @param command
     *               The command the player will use
     * @param args
     *            Any arguments that will be passed to this command
     */
    public void execute(CommandExecutor player, String command, String[] args) {
        if (find(command) == null) {
            if (player != null)
                player.sendMessage("Command not found!");
            else
                _server.Log("Command not found!");
        }
        Command c = find(command);
        if (args.length == 1 && args[0].equals(""))
            args = new String[0];
        
        if (_server.getPrivilegesHandler().isProtectedCmd(c)) {
        	for (int i = 0; i < args.length; i++) {
        		Player p = _server.findPlayer(args[i]);
        		if (p != null) {
        			if (_server.getPrivilegesHandler().isProtected(p, c)) {
        				int playerPerm = _server.getPrivilegesHandler().getStaffRank(player.getName()).getValue();
        				int pPerm = _server.getPrivilegesHandler().getStaffRank(p).getValue();
        				if (pPerm > playerPerm) {
        					player.sendMessage("You can't use " + c.getName() + " on " + p.username);
        					return;
        				}
        			}
        		}
        	}
        }
        
        if(c != null)
        {
            if (!(player instanceof Console) && !player.getGroup().canExecute(c) && !_server.getPrivilegesHandler().canOverride(player, c))
                player.sendMessage("Sorry, you don't have permission to execute this command!");
            else {
                if (!c.getName().equals("abort")) {
                    if (player instanceof Player) {
                        Player p = (Player)player;
                        if (Action.hasPendingAction(p)) {
                            p.sendMessage("You are currently using a command, please finish that command.");
                            p.sendMessage("Or abort it using /abort");
                            return;
                        }
                    }
                }
                try {
                    if (c.runInSeperateThread()) {
                        CommandExecute ce = new CommandExecute(player, c, args);
                        ce.start();
                    }
                    else {
                        _server.Log(player.getName() + " used /" + c.getName() + arrayToString(args));
                        c.execute(player, args);
                    }
                } catch (Exception e) {
                    _server.logError(e);
                    player.sendMessage("An error occured while executing the command.");
                }
            }
        }
    }
    
    private String arrayToString(String[] args) {
        String finals = "";
        for (String s : args) {
            finals += " " + s;
        }
        return finals;
    }
    
    /**
     * Add a command to the command list. This will load permissions
     * for this command, if no permissions are found for it, it will
     * save the default permissions for it.
     * @param cmd
     *           The command to add.
     */
    public void addCommand(Command cmd) {
        if (commands.contains(cmd))
            return;
        commands.add(cmd);
        try {
            setDefaults(cmd);
            loadPermissions();
            savePermissions();
            CommandLoadEvent cle = new CommandLoadEvent(cmd, _server);
            _server.getEventSystem().callEvent(cle);
        } catch (IOException e) {
            _server.logError(e);
        }
    }
    
    /**
     * Remove a command from the list
     * @param cmd
     *           The command to remove
     */
    public void removeCommand(Command cmd) {
        if (!commands.contains(cmd))
            return;
        commands.remove(cmd);
    }
    
    /**
     * Remove a command from the list.
     * @param name
     *            The command name.
     */
    public void removeCommand(String name) {
        Command c = find(name);
        if (c == null)
            return;
        removeCommand(c);
    }
    
    public void setDefaults(Command c) {
        c.setPermissionLevel(c.getDefaultPermissionLevel());
        c.setOP(c.isOpCommandDefault());
    }
    
    private void loadPermissions() throws IOException {
        if (!new File("properties").exists())
            new File("properties").mkdir();
        if (!new File("properties/commands.config").exists())
            makeDefault();
        FileInputStream fstream = new FileInputStream("properties/commands.config");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        while ((strLine = br.readLine()) != null)   {
            if (strLine.startsWith("#"))
                continue;
            String cmdname = strLine.split("\\:")[0];
            for (Command c : commands) {
                if (c.getName().equalsIgnoreCase(cmdname)) {
                    c.setPermissionLevel(Integer.parseInt(strLine.split("\\:")[1]));
                    try {
                        String letter = strLine.split("\\:")[2];
                        if (letter.equals("OP"))
                            c.setOP(true);
                        else
                            c.setOP(false);
                    } catch (Exception e) {
                        c.setOP(c.isOpCommandDefault());
                    }
                    break;
                }
            }
        }
        in.close();
    }
    
    private void makeDefault() {
        PrintWriter out = null;
        try {
            new File("properties/commands.config").createNewFile();
            out = new PrintWriter("properties/commands.config");
        } catch (FileNotFoundException e) {
            _server.logError(e);
            return;
        } catch (IOException e) {
            _server.logError(e);
            return;
        }
        out.println("#Permission file for commands");
        out.println("#Name:Permission ClassicLevel");
        for (Command c : commands) {
            out.println(c.getName() + ":" + c.getDefaultPermissionLevel() + ":" + (c.isOPCommand() ? "OP": "A"));
        }
        out.flush();
        out.close();
    }
    
    private void savePermissions() throws IOException {
        if (new File("properties/commands.config").exists())
            new File("properties/commands.config").delete();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e1) { }
        new File("properties/commands.config").createNewFile();
        PrintWriter out = null;
        try {
            out = new PrintWriter("properties/commands.config");
        } catch (FileNotFoundException e) {
            _server.logError(e);
            return;
        }
        out.println("#Permission file for commands");
        out.println("#Name:Permission ClassicLevel:[OP:A]");
        out.println("#Example:");
        out.println("#OPCommand:100:OP");
        out.println("#NormalCommand:43:A");
        for (Command c : commands) {
            out.println(c.getName() + ":" + c.getPermissionLevel() + ":" + (c.isOPCommand() ? "OP": "A"));
        }
        out.flush();
        out.close();
    }
    
    private class CommandExecute extends Thread {
        
        CommandExecutor p;
        Command cmd;
        String[] args;
        public CommandExecute(CommandExecutor p, Command cmd, String[] args) { this.p = p; this.cmd = cmd; this.args = args; }
        
        @Override
        public void run() {
            _server.Log(p.getName() + " used /" + cmd.getName() + arrayToString(args));
            cmd.execute(p, args);
        }
    }

}

