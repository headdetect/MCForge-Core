package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class EntityTeleport extends SMPPacket {

    public EntityTeleport(String name, byte ID, PacketManager parent) {
        super(name, ID, parent);
    }
    
    public EntityTeleport(PacketManager pm) {
        this("EntityTeleport", (byte)0x22, pm);
    }

    @Override
    public void handle(SMPPlayer p, Server server, DataInputStream reader) {
    }

    @Override
    public void write(SMPPlayer player, Server server, Object... obj) {
        if (obj.length >= 6) {
            ByteBuffer bb;
            
            if (obj[0] instanceof Integer && obj[1] instanceof Integer && obj[2] instanceof Integer && 
            	obj[3] instanceof Integer && obj[4] instanceof Byte && obj[5] instanceof Byte) {
                bb = ByteBuffer.allocate(19);
                
                bb.put(ID);
                bb.putInt((Integer)obj[0]);
                bb.putInt((Integer)obj[1]);
                bb.putInt((Integer)obj[2]);
                bb.putInt((Integer)obj[3]);
                bb.put((Byte)obj[4]);
                bb.put((Byte)obj[5]);

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
