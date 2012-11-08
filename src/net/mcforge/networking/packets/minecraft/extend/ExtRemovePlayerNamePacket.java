package net.mcforge.networking.packets.minecraft.extend;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.mcforge.API.ClassicExtension;
import net.mcforge.iomodel.Player;
import net.mcforge.networking.IOClient;
import net.mcforge.networking.packets.PacketManager;
import net.mcforge.networking.packets.PacketType;
import net.mcforge.server.Server;

@ClassicExtension(extName = "ExtRemovePlayerName")
public class ExtRemovePlayerNamePacket extends ExtendPacket {

	public ExtRemovePlayerNamePacket(String name, byte ID,
			PacketManager parent, PacketType packetType) {
		super(name, ID, parent, packetType);
	}
	
	public ExtRemovePlayerNamePacket(PacketManager parent) {
		this("ExtRemovePlayerName", (byte)0x35, parent, PacketType.Server_to_Client);
	}

	@Override
	public void WriteData(Player client, Server servers, Object... para)
			throws ExtensionNotSupportedException {
		if (client.hasExtension(this)) {
			byte[] array = new byte[65];
			array[0] = ID;
			String name = ((Player)para[0]).getName();
			try {
				byte[] bname = name.getBytes("US-ASCII");
				System.arraycopy(bname, 0, array, 1, bname.length);
				client.WriteData(array);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		else
			throw new ExtensionNotSupportedException(this);
	}

	@Override
	public void Handle(byte[] message, Server server, IOClient player) { }

}