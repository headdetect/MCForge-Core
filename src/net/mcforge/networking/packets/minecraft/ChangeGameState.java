package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class ChangeGameState extends SMPPacket {

	public ChangeGameState(String name, byte ID, PacketManager parent) {
		super(name, ID, parent);
	}
	
	public ChangeGameState(PacketManager pm) {
		this("ChangeGameState", (byte)0x46, pm);
	}

	@Override
	public void handle(SMPPlayer p, Server server, DataInputStream reader) {}
	
	@Override
	public void write(SMPPlayer p, Server server, Object... obj) {
		if(obj.length >= 2 && obj[0] instanceof Byte && obj[1] instanceof Byte) {
			ByteBuffer bb = ByteBuffer.allocate(3);
			bb.put(this.ID);
			bb.put((Byte)obj[0]);
			bb.put((Byte)obj[1]);
			
			try{
				p.writeData(bb.array());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}
