package com.syszee.practice.worlds;

import com.badlogic.gdx.math.Vector2;
import com.syszee.practice.blocks.Block;
import com.syszee.practice.core.SoundManager;

import java.util.Random;

public class Level {

    private int width, height;
    private Block[][] blocks;
    private Block[][] backgroundLayer;

    public Level(){
        loadDemo();
    }

    /** GETTERS AND SETTERS **/
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Block[][] getBlocks(){return blocks;}
    public Block[][] getBackgroundLayer(){return backgroundLayer;}
    public Block getBlock(int x, int y){
        return blocks[x][y];
    }
    public Block getBackgroundLayerBlock(int x, int y){ return backgroundLayer[x][y];}

    public void loadDemo(){
        width = 50;
        height = 50;

        // Random
        Random r = new Random();
        int blockChance;

        blocks = new Block[width][height];
        backgroundLayer = new Block[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                blockChance = r.nextInt(10) + 1;
                backgroundLayer[col][row] = null;
                blocks[col][row] = null;
                backgroundLayer[col][row] = new Block(new Vector2(col, row), false, Block.BlockType.GRASS, SoundManager.BLOCK_BREAK);

                if(blockChance == 5) {

                    if((int)Math.round(Math.random()) == 1)
                        blocks[col][row] = new Block(new Vector2(col, row), true, Block.BlockType.ROCK_1, SoundManager.BLOCK_BREAK);
                    else blocks[col][row] = new Block(new Vector2(col, row), true, Block.BlockType.ROCK_2, SoundManager.BLOCK_BREAK);



                }

            }
        }

        for(int col = 0; col < width; col++){
            blocks[col][0] = new Block(new Vector2(col, 0), true, Block.BlockType.DEBUG, null);
            blocks[col][49] = new Block(new Vector2(col, 49), true, Block.BlockType.DEBUG, null);
        }
        for(int row = 0; row < height; row++){
            blocks[0][row] = new Block(new Vector2(0, row), true, Block.BlockType.DEBUG, null);
            blocks[49][row] = new Block(new Vector2(49, row), true, Block.BlockType.DEBUG, null);
        }

        // Sign Block
        blocks[5][5] = new Block(new Vector2(5, 5), true, Block.BlockType.SIGN, SoundManager.BLOCK_BREAK_WOOD);

        System.out.println("Generated DEMO Level...");

    }

}
