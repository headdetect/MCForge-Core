package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class BlockAction extends SMPPacket {
	
	public BlockAction(String name, byte ID, PacketManager parent) {
		super(name, ID, parent);
	}
	
	public BlockAction(PacketManager pm) {
		this("BlockAction", (byte)0x36, pm);
	}

	@Override
	public void handle(SMPPlayer client, Server server, DataInputStream reader) { }
	
	@Override
	public void write(SMPPlayer p, Server server, Object... obj) {
		if(obj.length >= 6 && obj[0] instanceof Integer 
				&& obj[1] instanceof Short 
				&& obj[2] instanceof Integer 
				&& obj[3] instanceof Byte 
				&& obj[4] instanceof Byte 
				&& obj[5] instanceof Short) {
			ByteBuffer bb = ByteBuffer.allocate(15);
			bb.put(this.ID);
			bb.putInt((Integer)obj[0]);
			bb.putShort((Short)obj[1]);
			bb.putInt((Integer)obj[2]);
			bb.put((Byte)obj[3]);
			bb.put((Byte)obj[4]);
			bb.putShort((Short)obj[5]);
			
			try{
				p.writeData(bb.array());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
