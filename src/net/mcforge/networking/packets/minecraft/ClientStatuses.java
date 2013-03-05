package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class ClientStatuses extends SMPPacket {

	public ClientStatuses(String name, byte ID, PacketManager parent) {
        super(name, ID, parent);
    }
	
	public ClientStatuses(PacketManager pm) {
        this("ClientStatuses", (byte)0xCD, pm);
    }
	

	@SuppressWarnings("unused")
	@Override
	public void handle(SMPPlayer client, Server server, DataInputStream reader) {
		try {
			//TODO: finish
			byte payload = reader.readByte(); 
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void write(SMPPlayer client, Server server, Object... obj) { }
}