package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class EntityVelocity extends SMPPacket {

    public EntityVelocity(String name, byte ID, PacketManager parent) {
        super(name, ID, parent);
    }
    
    public EntityVelocity(PacketManager pm) {
        this("EntityVelocity", (byte)0x1C, pm);
    }

    @Override
    public void handle(SMPPlayer p, Server server, DataInputStream reader) {
    }

    @Override
    public void write(SMPPlayer player, Server server, Object... obj) {
        if (obj.length >= 4) {
            ByteBuffer bb;
            
            if (obj[0] instanceof Integer && obj[1] instanceof Short && obj[2] instanceof Short && obj[3] instanceof Short) {
                bb = ByteBuffer.allocate(8);
                
                bb.put(ID);
                bb.putInt((Integer)obj[0]);
                bb.putShort((Short)obj[1]);
                bb.putShort((Short)obj[2]);
				bb.putShort((Short) obj[3]);

				try {
					player.writeData(bb.array());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
    }
}
