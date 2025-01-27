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
import net.mcforge.world.generator.TreeGenerator;
import net.mcforge.world.Level;

/**
 * A generator that creates a mountainous level.
 * @author MCForgeTeam
 *
 */
public class Mountains implements ClassicGenerator {
    
    final Random rand = new Random();
    float divide;
    float[] terrain;
    float[] overlay;
    float[] overlay2;
    private Server _server;
    
    @Override
    public String getName() {
    	return "Mountains";
    }
	@Override
	public String[] getShortcuts() {
		return new String[] { "Mountain" };
	}
    
    /**
     * The constructor for the mountains level generator
     * @param server
     *              The server the level is in
     */
    public Mountains(Server _server) {
        this._server = _server;
    }
    
    @Override
    public void generate(ClassicLevel l) {
        generate(l, 64, 64, 64);
    }

    @Override
    public void generate(ClassicLevel l, int sizex, int sizey, int sizez) {
        terrain = new float[l.getWidth() * l.getHeight()];
        overlay = new float[l.getWidth() * l.getHeight()];
        overlay2 = new float[l.getWidth() * l.getHeight()]; 
        short WaterLevel = (short)(l.getDepth() / 2 + 2);

        GenerateFault(terrain, l);
        FilterAverage(l);
        GeneratePerlinNoise(overlay, l, rand);

        float RangeLow = 0.3f;
        float RangeHigh = 0.9f;
        float TreeDens = 1.4f;
        short TreeDist = 4;
        try {
            for (int bb = 0; bb < terrain.length; bb++)
            {
                short x = (short)(bb % l.getWidth());
                short y = (short)(bb / l.getWidth());
                short z = Evaluate(l, Range(terrain[bb], RangeLow - NegateEdge(x, y, l), RangeHigh - NegateEdge(x, y, l)));
                if (z > WaterLevel)
                {
                    for (short zz = 0; z - zz >= 0; zz++)
                    {
                        if (overlay[bb] < 0.72f)    //If not zoned for rocks or gravel
                        {
                            if (z > WaterLevel + 2)
                            {
                                if (zz == 0) { l.rawSetTile(x, z - zz, y, ClassicBlock.getBlock("Grass"), _server, false); }      //top layer
                                else if (zz < 3) { l.rawSetTile(x, (short)(z - zz), y, ClassicBlock.getBlock("Dirt"), _server, false); }   //next few
                                else { l.rawSetTile(x, (short)(z - zz), y, ClassicBlock.getBlock("Stone"), _server, false); }               //ten rock it
                            }
                            else
                            {
                                l.rawSetTile(x, (short)(z - zz), y, ClassicBlock.getBlock("Sand"), _server, false);                        //SAAAND extra for islands
                            }
                        }
                        else
                        {
                            l.rawSetTile(x, (short)(z - zz), y, ClassicBlock.getBlock("Stone"), _server, false);    //zoned for above sea level rock floor
                        }
                    }
                    int temprand = rand.nextInt(12);

                    switch (temprand)
                    {
                    case 10:
                        l.rawSetTile(x, (short)(z + 1), y, ClassicBlock.getBlock("RedFlower"), _server, false);
                        break;
                    case 11:
                        l.rawSetTile(x, (short)(z + 1), y, ClassicBlock.getBlock("YellowFlower"), _server, false);
                        break;
                    default:
                        break;
                    }
                    if (overlay[bb] < 0.65f && overlay2[bb] < TreeDens)
                    {
                        if (l.getTile(x, z + 1, y).getVisibleBlock() == (byte)0)
                        {
                            if (l.getTile(x, z, y).getName().equals("Grass"))
                            {
                                if (rand.nextInt(13) == 0)
                                {
                                    if (!TreeGenerator.checkForTree(l, x, z, y, TreeDist))
                                    {
                                        TreeGenerator.generateTree(_server, l, x, (short)(z + 1), y, rand);
                                    }
                                }
                            }
                        }
                    }

                }
                else    //Must be on/under the water line then
                {
                    for (short zz = 0; WaterLevel - zz >= 0; zz++)
                    {
                        if (WaterLevel - zz > z)
                        { l.rawSetTile(x, (short)(WaterLevel - zz), y, ClassicBlock.getBlock("Water"), _server, false); }    //better fill the water aboce me
                        else if (WaterLevel - zz > z - 3)
                        {
                            if (overlay[bb] < 0.75f)
                            {
                                l.rawSetTile(x, (short)(WaterLevel - zz), y, ClassicBlock.getBlock("Sand"), _server, false);   //sand top
                            }
                            else
                            {
                                l.rawSetTile(x, (short)(WaterLevel - zz), y, ClassicBlock.getBlock("Gravel"), _server, false);  //zoned for gravel
                            }
                        }
                        else
                        { 
                            l.rawSetTile(x, (short)(WaterLevel - zz), y, ClassicBlock.getBlock("Stone"), _server, false); 
                        }
                    }
                }
            }
            l.setNewSpawn(WaterLevel);
        }
        catch (Exception e)
        {
            _server.logError(e);
        }

        terrain = new float[0]; //Derp
        overlay = new float[0]; //Derp
        overlay2 = new float[0]; //Derp
    }


    private void GenerateFault(float[] array, Level l) {
        float startheight = 0.5f;
        float dispAux;
        short i, j, k, halfX, halfZ;
        float a, b, c, w, d;

        float DispMax, DispMin, DispChange;
        DispChange = -0.0025f;
        
        DispMax = 0.02f;
        startheight = 0.6f;

        for (int x = 0; x < array.length; x++)
        {
            array[x] = startheight;
        }
        DispMin = -DispMax;
        float disp = DispMax;


        halfX = (short)(l.getWidth() / 2);
        halfZ = (short)(l.getHeight() / 2);
        int numIterations = (int)((l.getWidth() + l.getHeight()));
        for (k = 0; k < numIterations; k++)
        {
            //s.Log("itteration " + k.ToString());
            d = (float)Math.sqrt(halfX * halfX + halfZ * halfZ);
            w = (float)(rand.nextDouble() * 360);
            //w = (float)(rand.NextDouble()*90);
            a = (float)Math.cos(w);
            b = (float)Math.sin(w);

            c = ((float)rand.nextDouble()) * 2 * d - d;
            for (i = 0; i < l.getHeight(); i++)
            {
                for (j = 0; j < l.getWidth(); j++)
                {
                    if ((i - halfZ) * a + (j - halfX) * b + c > 0)
                        dispAux = disp;
                    else
                        dispAux = -disp;
                    AddTerrainHeight(array, j, i, (short)l.getWidth(), dispAux);
                }
            }

            disp += DispChange;
            if (disp < DispMin) { disp = DispMax; }
        }
    }
    void AddTerrainHeight(float[] array, short x, short y, short width, float height)
    {
        int temp = x + y * width;
        if (temp < 0) return;
        if (temp > array.length) return;

        array[temp] += height;

        if (array[temp] > 1.0f) array[temp] = 1.0f;
        if (array[temp] < 0.0f) array[temp] = 0.0f;
    }

    void FilterAverage(Level l)
    {

        float[] filtered = new float[terrain.length];

        for (int bb = 0; bb < terrain.length; bb++)
        {
            short x = (short)(bb % l.getWidth());
            short y = (short)(bb / l.getWidth());
            filtered[bb] = GetAverage9(x, y, l);
        }

        for (int bb = 0; bb < terrain.length; bb++)
        {
            terrain[bb] = filtered[bb];
        }
    }
    //Averages over 5 points
    float GetAverage5(short x, short y, Level l)
    {
        divide = 0.0f;
        float temp = GetPixel(x, y, l);
        temp += GetPixel((short)(x + 1), y, l);
        temp += GetPixel((short)(x - 1), y, l);
        temp += GetPixel(x, (short)(y + 1), l);
        temp += GetPixel(x, (short)(y - 1), l);

        return temp / divide;
    }
    //Averages over 9 points
    float GetAverage9(short x, short y, Level l)
    {
        divide = 0.0f;
        float temp = GetPixel(x, y, l);
        temp += GetPixel((short)(x + 1), y, l);
        temp += GetPixel((short)(x - 1), y, l);
        temp += GetPixel(x, (short)(y + 1), l);
        temp += GetPixel(x, (short)(y - 1), l);

        temp += GetPixel((short)(x + 1), (short)(y + 1), l);
        temp += GetPixel((short)(x - 1), (short)(y + 1), l);
        temp += GetPixel((short)(x + 1), (short)(y - 1), l);
        temp += GetPixel((short)(x - 1), (short)(y - 1), l);

        return temp / divide;
    }

    float GetPixel(short x, short y, Level l)
    {
        if (x < 0) { return 0.0f; }
        if (x >= l.getWidth()) { return 0.0f; }
        if (y < 0) { return 0.0f; }
        if (y >= l.getHeight()) { return 0.0f; }
        divide += 1.0f;
        return terrain[x + y * l.getWidth()];
    }

    float Range(float input, float low, float high)
    {
        if (high <= low) { return low; }
        return low + (input * (high - low));
    }

    //Forces the edge of a map to slope lower for island map types
    float NegateEdge(short x, short y, Level l)
    {
        float tempx = 0.0f, tempy = 0.0f;
        float temp;
        if (x != 0) { tempx = ((float)x / (float)l.getWidth()) * 0.5f; }
        if (y != 0) { tempy = ((float)y / (float)l.getHeight()) * 0.5f; }
        tempx = Math.abs(tempx - 0.25f);
        tempy = Math.abs(tempy - 0.25f);
        if (tempx > tempy)
        {
            temp = tempx - 0.15f;
        }
        else
        {
            temp = tempy - 0.15f;
        }

        //s.Log("temp = " + temp.ToString());
        if (temp > 0.0f) { return temp; }
        return 0.0f;
    }

    void GeneratePerlinNoise(float[] array, Level l, Random rand)
    {
        GenerateNormalized(array, 0.7f, 8, l.getWidth(), l.getHeight(), rand.nextInt(), 64);
    }

    void GenerateNormalized(float[] array, float persistence, int octaves, int width, int height, int seed, float zoom)
    {
        float min = 0;
        float max = 0;
        for (int y = 0; y < height; ++y)
        {
            for (int x = 0; x < width; ++x)
            {
                float total = 0;
                float frequency = 1;
                float amplitude = 1;

                for (int i = 0; i < octaves; ++i)
                {
                    total = total + InterpolatedNoise(x * frequency / zoom, y * frequency / zoom, seed) * amplitude;
                    frequency *= 2;
                    amplitude *= persistence;
                }

                array[y * width + x] = total;

                min = total < min ? total : min;
                max = total > max ? total : max;
            }
        }

        //Normalize
        for (int i = 0; i < width * height; ++i)
        {
            array[i] = (array[i] - min) / (max - min);
        }
    }

    float Noise(int x, int y, int seed)
    {
        int n = x + y * 57 + seed;
        n = (n << 13) ^ n;
        return (float)(1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
    }

    float SmoothNoise(int x, int y, int seed)
    {
        float corners = (Noise(x - 1, y - 1, seed) + Noise(x + 1, y - 1, seed) + Noise(x - 1, y + 1, seed) + Noise(x + 1, y + 1, seed)) / 16;
        float sides = (Noise(x - 1, y, seed) + Noise(x + 1, y, seed) + Noise(x, y - 1, seed) + Noise(x, y + 1, seed) / 8);
        float center = Noise(x, y, seed) / 4;
        return corners + sides + center;
    }

    float Interpolate(float a, float b, float x)
    {
        float ft = x * 3.1415927f;
        float f = (float)(1 - Math.cos(ft)) * .5f;

        return a * (1 - f) + b * f;
    }

    float InterpolatedNoise(float x, float y, int seed)
    {
        int wholePartX = (int)x;
        float fractionPartX = x - wholePartX;

        int wholePartY = (int)y;
        float fractionPartY = y - wholePartY;

        float v1 = SmoothNoise(wholePartX, wholePartY, seed);
        float v2 = SmoothNoise(wholePartX + 1, wholePartY, seed);
        float v3 = SmoothNoise(wholePartX, wholePartY + 1, seed);
        float v4 = SmoothNoise(wholePartX + 1, wholePartY + 1, seed);

        float i1 = Interpolate(v1, v2, fractionPartX);
        float i2 = Interpolate(v3, v4, fractionPartX);

        return Interpolate(i1, i2, fractionPartY);
    }

    short Evaluate(Level lvl, float height)
    {
        short temp = (short)(height * lvl.getDepth());
        if (temp < 0) return 0;
        if (temp > lvl.getDepth() - 1) return (short)(lvl.getDepth() - 1);
        return temp;
    }
}