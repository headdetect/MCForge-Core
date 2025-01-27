package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class EntityStatus extends SMPPacket {

	public EntityStatus(String name, byte ID, PacketManager parent) {
		super(name, ID, parent);
	}
	
	public EntityStatus(PacketManager pm) {
		this("EntityStatus", (byte)0x26, pm);
	}

	@Override
	public void handle(SMPPlayer p, Server server, DataInputStream reader) {}

	
	@Override
	public void write(SMPPlayer p, Server server, Object... obj) {
		if(obj.length >= 2 && obj[0] instanceof Integer && obj[1] instanceof Byte) {
			ByteBuffer bb = ByteBuffer.allocate(6);
			bb.put(this.ID);
			bb.putInt((Integer)obj[0]);
			bb.put((Byte)obj[1]);
			
			try{
				p.writeData(bb.array());
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
	}	
}
