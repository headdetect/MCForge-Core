/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.world.generator.classicmodel;

import java.util.Random;

import net.mcforge.server.Server;
import net.mcforge.world.blocks.classicmodel.ClassicBlock;
import net.mcforge.world.classicmodel.ClassicLevel;

/**
 * Creates a rainbow level where the level is a giant box with a
 * rainbow patterned wall
 * @author MCForge Team
 *
 */
public class Rainbow implements ClassicGenerator {

    private Server _server;
    
    @Override
    public String getName() {
    	return "Rainbow";
    }
	@Override
	public String[] getShortcuts() {
		return new String[0];
	}
    
    /**
     * The constructor for the rainbow level generator
     * 
     * @param server - The server the level is in
     */
    public Rainbow(Server server) {
        this._server = server;
    }
    
    @Override
    public void generate(ClassicLevel l) {
        Random rand = new Random(System.currentTimeMillis());
        for (int x = 0; x < l.getWidth(); x++) {
            for (int y = 0; y < l.getHeight(); y++) {
                for (int z = 0; z < l.getDepth(); z++) {
                    if (y == 0 || y == l.getHeight() - 1 || x == 0 || x == l.getWidth() - 1 || z == 0 || z == l.getDepth() - 1) 
                        l.rawSetTile(x, y, z, ClassicBlock.getBlock((byte)(rand.nextInt(36 - 21) + 21)), _server, false);
                }
            }
        }
    }
    @Override
    public void generate(ClassicLevel l, int x, int y, int z) {
        generate(l);
    }
}