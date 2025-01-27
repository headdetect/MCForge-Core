/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.networking.packets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mcforge.networking.IOClient;
import net.mcforge.networking.packets.clients.Client;
import net.mcforge.server.Server;

public abstract class Packet {
    
    /**
     * The OpCode for this packet.
     */
    public byte ID;
    
    /**
     * How many bytes this packet is.
     */
    public int length;
    
    /**
     * The packet manager that received this packet.
     */
    public PacketManager parent;
    
    /**
     * The name of the packet
     */
    public String name;
    
    private ArrayList<Client> supportedclients = new ArrayList<Client>();
    
    /**
     * This method will read the array of bytes and
     * execute code depending on whats in the array.
     * @param message
     *               The byte array read from the PacketManager
     * @param server
     *              The server that the PacketManager reads for
     * @param player
     *              The socket or {@link IOClient} that send this packet
     */
    public abstract void Handle(byte[] message, Server server, IOClient player);
    
    /**
     * This method will write data to the <code>client</code>
     * @param client
     *               The client to send this packet to.
     * @param servers
     *               The server that will send the packet
     */
    public abstract void Write(IOClient client, Server servers);
    
    /**
     * This method will write data to the <code>client</code>
     * @param client
     *              The client to send this packet to
     * @param servers
     *               The server that will send the packet
     * @param parrams
     *               Extra data if this packet should require
     *               extra data
     */
    public void Write(IOClient client, Server servers, Object... params) {
        Write(client, servers);
    }
    
    /**
     * Whether this packet's byte size is dynamic, or has no defined size.
     * This is usually the case if this packet includes a string.
     * @return
     *       True if this packet's size is dynamic.
     */
    public boolean dynamicSize() {
        return false;
    }
    
    /**
     * The constructor for this packet.
     * @param name
     *            The name of the packet
     * @param ID
     *          The OpCode of the packet
     * @param parent
     *              The PacketManager that will handle the packets
     * @param packetType
     *                  What type of packet this is. This isn't used in the PacketManager,
     *                  but its nice to be organized right :3 ?
     */
    public Packet(String name, byte ID, PacketManager parent) {
        this.name = name;
        this.ID = ID;
        this.parent = parent;
    }
    
    /**
     * Add a client this packet supports.
     * @param client
     */
    public void addClientSupport(Client client) {
        supportedclients.add(client);
    }
    
    /**
     * Get a list of Clients this packet supports.
     * @return
     */
    public List<Client> getSupportedClients() {
        return Collections.unmodifiableList(supportedclients);
    }

}

