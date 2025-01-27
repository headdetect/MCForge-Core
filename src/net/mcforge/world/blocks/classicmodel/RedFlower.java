/*******************************************************************************
 * Copyright (c) 2012 MCForge.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package net.mcforge.world.blocks.classicmodel;

import net.mcforge.server.Server;

public class RedFlower extends PhysicsBlock {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public RedFlower(byte ID, String name) {
        super(ID, name);
    }
    
    public RedFlower() {
        super((byte)38, "RedFlower");
    }
    
    public RedFlower(Server s) {
        super((byte)38, "RedFlower", s);
    }

    public RedFlower(byte b, String string, Server s) {
        super(b, string, s);
    }

    @Override
    public boolean initAtStart() {
        return true;
    }

    @Override
    public PhysicsBlock clone(Server s) {
        return new RedFlower(s);
    }

    @Override
    public void tick() {
        if (getLevel().getTile(getX(), getY() - 1, getZ()).getVisibleBlock() != 2)
            super.remove();
        else
            super.stopTick();
    }
    
    @Override
    public boolean canWalkThrough() {
        return true;
    }

    @Override
    public boolean inSeperateThread() {
        return false;
    }

    @Override
    public int getTimeout() {
        // TODO Auto-generated method stub
        return 1;
    }

}

