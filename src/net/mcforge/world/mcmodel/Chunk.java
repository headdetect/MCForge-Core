package net.mcforge.world.mcmodel;

import net.mcforge.server.Server;
import net.mcforge.world.Level;
import net.mcforge.world.blocks.Block;
import net.mcforge.world.blocks.mcmodel.SMPBlock;

public class Chunk {
    private SMPBlock[] blocks = new SMPBlock[16 * 16 * 256];
    private final int xlength = 16;
    private final int zlength = 16;
    private final int ylength = 256;
    private ChunkPoint point;
    private boolean updated;
    private ChunkLevel owner;
    
    public Chunk(ChunkPoint point, ChunkLevel owner) {
        this.point = point;
        this.owner = owner;
    }
    
    public Chunk(int x, int z, ChunkLevel owner) {
        this(new ChunkPoint(x, z), owner);
    }
    
    public Chunk(double x, double z, ChunkLevel owner) {
        this((int)x >> 4, (int)z >> 4, owner);
    }
    
    public ChunkPoint getPoint() {
        return point;
    }
    
    /**
     * Place a block in this chunk.
     * @param block
     *             The block to place
     * @param x
     *         The X cord. <b>in this chunk. This value can't be greater than 16</b> 
     * @param y
     *         The Y cord. <b>in this chunk. This value can't be greater than 256</b>
     * @param z
     *         The Z cord. <b>in this chunk. This value can't be greator than 16</b>
     */
    public void setTile(SMPBlock block, int x, int y, int z) {
        setTile(block, posToInt(x, y, z));
    }
    
    /**
     * Get a block inside this chunk
     * @param x
     *         The X cord. <b>in this chunk. This value can't be greater than 16</b> 
     * @param y
     *         The Y cord. <b>in this chunk. This value can't be greater than 256</b>
     * @param z
     *         The Z cord. <b>in this chunk. This value can't be greator than 16</b>
     * @return
     */
    public Block getTile(int x, int y, int z) {
        return getTile(posToInt(x, y, z));
    }
    
    private Block getTile(int index) {
        if (index < 0) index = 0;
        if (index >= blocks.length) index = blocks.length - 1;
        return blocks[index];
    }
    
    private void setTile(SMPBlock block, int index) {
        if (index < 0) index = 0;
        if (index >= blocks.length) index = blocks.length - 1;
        int[] pos = intToPos(index);
        SMPBlock wasthere = blocks[index];
        blocks[index] = block;
        if (wasthere != null) {
            if (wasthere.onDelete(owner, pos[0], pos[1], pos[2], getServer())) {
                blocks[index] = wasthere;
                return;
            }
        }
        if (block.onPlace(owner, pos[0], pos[1], pos[2], getServer())) {
            blocks[index] = wasthere;
            return;
        }
        updated = true;
    }
    
    public Server getServer() {
        return owner.getServer();
    }
    
    public Level getOwner() {
        return owner;
    }
    
    public void save() {
        if (!updated)
            return;
    }
    
    public boolean unload() {
        save();
        blocks = null;
        owner = null;
        return true;
    }
    
    private int posToInt(int x, int y, int z) {
        if (x < 0) { return -1; }
        if (x >= xlength) { return -1; }
        if (y < 0) { return -1; }
        if (y >= ylength) { return -1; }
        if (z < 0) { return -1; }
        if (z >= zlength) { return -1; }
        return x + z * xlength + y * ylength * zlength;
    }

    private int[] intToPos(int index) {
        int[] toreturn = new int[3];
        toreturn[1] = (index / xlength / ylength);
        index -= toreturn[1]*xlength*ylength;
        toreturn[2] = (index/xlength);
        index -= toreturn[2]*xlength;
        toreturn[0] = index;
        return toreturn;
    }
}
