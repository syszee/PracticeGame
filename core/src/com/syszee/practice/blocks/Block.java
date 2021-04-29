package com.syszee.practice.blocks;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {

    // PROPERTIES
    static final float SIZE = 1F;
    public float DURABILITY = 1F;
    public float ORIGINAL_DURABILITY = 1F;
    public Boolean collideable;
    Sound breakSound;

    // TIMER
    public int breakTime = 0;
    public float breakStateTime = 0;

    // PRESENCE
    Vector2 position;
    Rectangle bounds = new Rectangle();


    // TYPE
    public BlockType blockType;
    public enum BlockType {
        DEBUG, GRASS, SIGN, ROCK_1, ROCK_2;
    }

    // STATE
    public enum State {
        BREAKING, NOT_BREAKING;
    }
    State state = State.NOT_BREAKING;

    public Block(Vector2 position, Boolean isCollideable, BlockType blockType, Sound breakSound){
        this.position = position;
        this.bounds.x = position.x;
        this.bounds.y = position.y;
        this.collideable = isCollideable;
        this.blockType = blockType;
        this.breakSound = breakSound;

        // Block Boundaries
        if(blockType.equals(BlockType.SIGN)){
            this.bounds.height = 0.3F;
            this.bounds.width = 1F;
            DURABILITY = 0.10F;
            ORIGINAL_DURABILITY = DURABILITY;
        }else if(blockType.equals(BlockType.ROCK_1) || blockType.equals(BlockType.ROCK_2)){
            this.bounds.height = 0.5F;
            this.bounds.width = 1f;
            DURABILITY = 0.25F;
            ORIGINAL_DURABILITY = DURABILITY;
        }

        // Backup Bounds
        else{
            this.bounds.height = SIZE;
            this.bounds.width = SIZE;
        }
    }

    // Special Bound/Shape
    public Block(Vector2 position, Boolean isCollideable, BlockType blockType, Sound breakSound, float boundWidth, float boundHeight, float boundOffsetX, float boundOffsetY){
        this.position = position;
        this.bounds.height = boundHeight;
        this.bounds.width = boundWidth;
        this.bounds.x = position.x - boundOffsetX;
        this.bounds.y = position.y - boundOffsetY;
        this.collideable = isCollideable;
        this.blockType = blockType;
        this.breakSound = breakSound;
    }

    public void update(float delta){
        if(state.equals(State.BREAKING)) breakStateTime += delta;
    }

    // HANDLE BREAKING
    public void breakBlock(float damage){
        breakTime++;

        if(breakTime >= 15){
            DURABILITY -= damage;
            breakTime = 0;
        }
        setState(State.BREAKING);
    }

    // CHECK IF SHOULD BREAK
    public boolean shouldBreak(){
        if(DURABILITY <= 0F) return true;
        else return false;
    }

    // RESET DURABILITY IF NOT SUCCESSFUL
    public void resetDurability(){
        this.DURABILITY = ORIGINAL_DURABILITY;
        breakTime = 0;
        setState(State.NOT_BREAKING);
        breakStateTime = 0;
    }

    public Rectangle getBounds(){ return bounds; }
    public Vector2 getPosition(){ return position; }
    public Boolean isCollideable(){ return collideable;}
    public BlockType getBlockType(){ return blockType; }
    public float getSize(){return SIZE;}
    public float getDurability(){return DURABILITY;}
    public State getState(){return state;}
    public void setState(State s){this.state = s;}
    public float getBreakStateTime(){return breakStateTime;}
    public Sound getBreakSound(){return breakSound;}

}
