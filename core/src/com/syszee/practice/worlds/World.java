package com.syszee.practice.worlds;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.syszee.practice.blocks.Block;
import com.syszee.practice.entities.Egg;
import com.syszee.practice.entities.Enemy;
import com.syszee.practice.entities.Player;

import java.util.ArrayList;
import java.util.List;

public class World {

    // ENTITIES & OBJECTS
    Player          player;
    Level           level;
    Array<Rectangle> collisionBlocks = new Array<Rectangle>();
    Array<Egg> eggs = new Array<Egg>();
    Array<Enemy> enemies = new Array<Enemy>();


    public World(){
        createDemoWorld();
    }

    /** GETTERS **/
    public Player getPlayer(){ return player; }
    public Array<Rectangle> getCollisionBlocks(){ return collisionBlocks; }
    public Array<Egg> getEggs(){return eggs;}
    public Array<Enemy> getEnemies(){return enemies;}
    public Level getLevel(){return level;}

    public void createDemoWorld() {
        player = new Player(new Vector2(10, 10));
        enemies.add(new Enemy(new Vector2(8, 8),
                Enemy.Type.YELLOW_DINO,
                this));
        level = new Level();
    }

    public List<Block> getDrawableBlocks(int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height;
        if(x < 0) x = 0;
        if(y < 0) y = 0;

        int x2 = x+2*width;
        int y2 = y+2*width;
        if(x2 >= level.getWidth()) x2 = level.getWidth() - 1;
        if(y2 >= level.getHeight()) y2 = level.getHeight() - 1;

        List<Block> blocks = new ArrayList<Block>();
        Block block;
        for(int col = x; col <= x2; col++){
            for(int row = y; row <= y2; row++){
                block = level.getBlocks()[col][row];
                if(block != null){
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public List<Block> getDrawableBackgroundLayerBlocks(int width, int height) {
        int x = (int)player.getPosition().x - width;
        int y = (int)player.getPosition().y - height;
        if(x < 0) x = 0;
        if(y < 0) y = 0;

        int x2 = x+2*width;
        int y2 = y+2*width;
        if(x2 >= level.getWidth()) x2 = level.getWidth() - 1;
        if(y2 >= level.getHeight()) y2 = level.getHeight() - 1;

        List<Block> blocks = new ArrayList<Block>();
        Block block;
        for(int col = x; col <= x2; col++){
            for(int row = y; row <= y2; row++){
                block = level.getBackgroundLayer()[col][row];
                if(block != null){
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
}
