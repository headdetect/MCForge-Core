package net.mcforge.networking.packets.minecraft;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import net.mcforge.iomodel.SMPPlayer;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.server.Server;

public class CloseWindow extends SMPPacket {

    public CloseWindow(String name, byte ID, PacketManager parent) {
        super(name, ID, parent);
    }
    
    public CloseWindow(PacketManager pm) {
        this("CloseWindow", (byte)0x65, pm);
    }

    @Override
    public void handle(SMPPlayer p, Server server, DataInputStream reader) {
    }

    @Override
    public void write(SMPPlayer player, Server server, Object... obj) {
        if (obj.length >= 1) {
            ByteBuffer bb;
            
            if (obj[0] instanceof Byte) {
                bb = ByteBuffer.allocate(2);
                
                bb.put(ID);
                bb.put((Byte)obj[1]);
                
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
