/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.world.generator.classicmodel;

import net.mcforge.server.Server;
import net.mcforge.world.blocks.classicmodel.ClassicBlock;
import net.mcforge.world.classicmodel.ClassicLevel;

/**
 * Create a level where the walls are made of white wool, perfect
 * for pixel art!
 * @author MCForgeTeam
 *
 */
public class Pixel implements ClassicGenerator {

    private Server _server;
    
    @Override
    public String getName() {
    	return "Pixel";
    }
	@Override
	public String[] getShortcuts() {
		return new String[0];
	}
	
    /**
     * The constructor for the pixel level generator
     * 
     * @param server - The server the level is in
     */
    public Pixel(Server server) {
        this._server = server;
    }
    @Override
    public void generate(ClassicLevel l) {
        for (int x = 0; x < l.getWidth(); x++) {
            for (int y = 0; y < l.getHeight(); y++) {
                for (int z = 0; z < l.getDepth(); z++) {
                    if (y == 0)
                        l.rawSetTile(x, y, z, ClassicBlock.getBlock("Bedrock"), _server, false);
                    else if (x == 0 || x == l.getWidth() - 1 || z == 0 || z == l.getDepth() - 1)
                        l.rawSetTile(x, y, z, ClassicBlock.getBlock("White"), _server, false);
                }
            }
        }

    }
    @Override
    public void generate(ClassicLevel l, int x, int y, int z) {
        generate(l);
    }

}