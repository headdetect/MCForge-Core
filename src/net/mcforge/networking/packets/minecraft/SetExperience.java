package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class SetExperience extends SMPPacket {

    public SetExperience(String name, byte ID, PacketManager parent) {
        super(name, ID, parent);
    }
    
    public SetExperience(PacketManager pm) {
        this("SetExperience", (byte)0x2B, pm);
    }

    @Override
    public void handle(SMPPlayer p, Server server, DataInputStream reader) {
    }

    @Override
    public void write(SMPPlayer player, Server server, Object... obj) {
        if (obj.length >= 3) {
            ByteBuffer bb;
            
            if (obj[0] instanceof Float && obj[1] instanceof Short && obj[2] instanceof Short) {
                bb = ByteBuffer.allocate(9);
                
                bb.put(ID);
                bb.putFloat((Float)obj[0]);
                bb.putShort((Short)obj[1]);
                bb.putShort((Short)obj[2]);

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
