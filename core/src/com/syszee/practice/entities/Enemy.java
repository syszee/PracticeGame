package com.syszee.practice.entities;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.syszee.practice.blocks.Block;
import com.syszee.practice.core.WorldRenderer;
import com.syszee.practice.worlds.World;
import org.w3c.dom.css.Rect;

public class Enemy {

    // PRESENCE & PROPERTIES
    Vector2             position;
    Vector2             centerPoint = new Vector2();
    Vector2             velocity = new Vector2();
    Vector2             acceleration = new Vector2();
    Rectangle           bounds = new Rectangle();
    Rectangle           targetSight = new Rectangle();
    float               SIZE_WIDTH;
    float               SIZE_HEIGHT;
    float               renderWidth;
    float               renderHeight;
    World               world;
    Array<Enemy>        container;

    float               SPEED = 30F;
    float               WANDERING_SPEED = 20F;
    float               DAMP = 0.8F;
    float               MAX_VELOCITY = 4F;

    // ENEMY TYPE
    public enum Type {
        YELLOW_DINO
    }
    Type                enemyType;

    // TEXTURES & SOUNDS
    boolean             isFacingLeft = true;
    float               stateTime = 0;
    TextureRegion       enemyFrame;
    Animation           enemyIdleLeft, enemyIdleRight;
    Animation           enemyWalkLeft, enemyWalkRight;
    Sprite              sprite = new Sprite();
    float               IDLE_FRAME_DURATION = 0.1F;
    float               WALK_FRAME_DURATION = 0.1F;

    // MOVEMENT & AI
    public enum State { IDLE, WALKING }
    public enum AIState { IDLE, TRACKING, WANDERING }
    State               state;
    AIState               stateAI;
    Vector2             targetPosition;

    // INITIALIZE
    public Enemy(Vector2 position, Type enemyType, World world){
        this.position = position;
        this.enemyType = enemyType;
        this.world = world;
        this.container = world.getEnemies();

        state = State.IDLE;
        stateAI = AIState.IDLE;

        initValues();
        this.bounds.width = SIZE_WIDTH;
        this.bounds.height = SIZE_HEIGHT;
        this.bounds.x = position.x;
        this.bounds.y = position.y;

        this.targetPosition = new Vector2();

        this.targetSight.width = SIZE_WIDTH*5;
        this.targetSight.height = SIZE_HEIGHT*5;

        loadTextures();
    }

    // UPDATE
    public void update(float delta){
        stateTime += delta;

        // HANDLE THE ENTITY TARGET VIEW DISTANCE
        this.targetSight.x = bounds.x - (int)(targetSight.width/2);
        this.targetSight.y = bounds.y - (int)(targetSight.height/2);

        targetPosition.x = world.getPlayer().getBounds().x + (world.getPlayer().getBounds().width/2);
        targetPosition.y = world.getPlayer().getBounds().y + (world.getPlayer().getBounds().height/2);
        this.centerPoint.x = getBounds().x + (getBounds().width/2);
        this.centerPoint.y = getBounds().y + (getBounds().height/2);

        // DECIDE DIRECTION
        if(acceleration.x >= 0 && !state.equals(State.IDLE)) isFacingLeft = false;
        else if(acceleration.x < 0 && !state.equals(State.IDLE)) isFacingLeft = true;

        // IF NOT MOVING SET STILL
        if(stateAI.equals(AIState.IDLE)) state = State.IDLE;
        else state = State.WALKING;
        // IF MOVING, SET WALKING ^

        // IF A PLAYER IS DETECTED
        if(world.getPlayer().getBounds().overlaps(targetSight)){
            stateAI = AIState.TRACKING;
            chaseTarget(targetPosition);
        }else {
            stateAI = AIState.IDLE;
        }

        acceleration.scl(delta);
        velocity.add(acceleration.x, acceleration.y);

        checkCollisionWithBlocks(delta);

        velocity.x *= DAMP;
        velocity.y *= DAMP;

        if(velocity.x > MAX_VELOCITY) velocity.x = MAX_VELOCITY;
        if(velocity.x < -MAX_VELOCITY) velocity.x = -MAX_VELOCITY;
        if(velocity.y > MAX_VELOCITY) velocity.y = MAX_VELOCITY;
        if(velocity.y < -MAX_VELOCITY) velocity.y = -MAX_VELOCITY;

    }

    // RENDER
    public void render(SpriteBatch spriteBatch){

        // TEMPORARY (NEEDS TO BE SWITCH CASE)
        if(state.equals(State.IDLE)){
            enemyFrame = isFacingLeft ? (TextureRegion) enemyIdleLeft.getKeyFrame(stateTime, true)
                    : (TextureRegion) enemyIdleRight.getKeyFrame(stateTime, true);
        }else if(state.equals(State.WALKING)){
            enemyFrame = isFacingLeft ? (TextureRegion) enemyWalkLeft.getKeyFrame(stateTime, true)
                    : (TextureRegion) enemyWalkRight.getKeyFrame(stateTime, true);
        }

        //spriteBatch.draw(enemyFrame, position.x, position.y, renderWidth, renderHeight);
        sprite.setPosition(position.x, position.y);
        sprite.setSize(renderWidth, renderHeight);
        sprite.setRegion(enemyFrame);

    }

    // REMOVE
    public void remove(){
        container.removeValue(this, true);
    }

    // INITIALIZE TEXTURES
    public void loadTextures(){
        TextureAtlas atlas = WorldRenderer.atlas;
        switch(enemyType){
            case YELLOW_DINO:
                // IDLE LEFT
                TextureRegion[] idleLeftFrames = new TextureRegion[3];
                for(int i = 0; i < 3; i++){
                    idleLeftFrames[i] = atlas.findRegion("yellow_dino_idle", i);
                }
                enemyIdleLeft = new Animation(IDLE_FRAME_DURATION, idleLeftFrames);

                // IDLE RIGHT
                TextureRegion[] idleRightFrames = new TextureRegion[3];
                for(int i = 0; i < 3; i++){
                    idleRightFrames[i] = new TextureRegion(idleLeftFrames[i]);
                    idleRightFrames[i].flip(true, false);
                }
                enemyIdleRight = new Animation(IDLE_FRAME_DURATION, idleRightFrames);

                // WALK LEFT
                TextureRegion[] walkLeftFrames = new TextureRegion[6];
                for(int i = 0; i < 6; i++){
                    walkLeftFrames[i] = atlas.findRegion("yellow_dino_walk", i);
                }
                enemyWalkLeft = new Animation(WALK_FRAME_DURATION, walkLeftFrames);

                // WALK RIGHT
                TextureRegion[] walkRightFrames = new TextureRegion[6];
                for(int i = 0; i < 6; i++){
                    walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
                    walkRightFrames[i].flip(true, false);
                }
                enemyWalkRight = new Animation(WALK_FRAME_DURATION, walkRightFrames);

                break;
            default:

        }
    }

    // INITIALIZE VALUES
    public void initValues(){

        switch(enemyType){
            case YELLOW_DINO:
                renderHeight = 1.5F;
                renderWidth = 1F;
                SIZE_HEIGHT = 1F;
                SIZE_WIDTH = 1F;
                break;

            default:
                renderHeight = 1.5F;
                renderWidth = 1F;
                SIZE_HEIGHT = 1F;
                SIZE_WIDTH = 1F;
                break;
        }

    }

    // RETRIEVAL
    public Vector2 getPosition(){return position;}
    public Vector2 getVelocity(){return velocity;}
    public Vector2 getAcceleration(){return acceleration;}
    public Rectangle getBounds(){return bounds;}
    public Rectangle getTargetSight(){return targetSight;}
    public Type getEnemyType(){return enemyType;}
    public State getState(){return state;}
    public boolean isFacingLeft(){return isFacingLeft;}
    public float getStateTime(){return stateTime;}
    public float getRenderWidth(){return renderWidth;}
    public float getRenderHeight(){return renderHeight;}
    public Sprite getSprite(){return sprite;}

    /**
     * COLLISION HANDLING
     */

    // CHECK FOR COLLISION
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    public void checkCollisionWithBlocks(float delta){

        velocity.scl(delta);
        Rectangle enemyRect = rectPool.obtain();
        enemyRect.set(bounds.x, bounds.y, bounds.width, bounds.height);

        int startX, endX;
        int startY = (int) bounds.y;
        int endY = (int) (bounds.y + bounds.height);

        if(velocity.x < 0) startX = endX = (int) Math.floor(bounds.x + velocity.x);
        else startX = endX = (int) Math.floor(bounds.x + bounds.width + velocity.x);

        populateCollidableBlocks(startX, startY, endX, endY);

        enemyRect.x += velocity.x;
        world.getCollisionBlocks().clear();

        for(Block block : collidable){
            if(block == null) continue;
            if(enemyRect.overlaps(block.getBounds())){
                velocity.x = 0;
                world.getCollisionBlocks().add(block.getBounds());
                break;
            }
        }

        enemyRect.x = position.x;

        startX = (int) bounds.x;
        endX = (int) (bounds.x + bounds.width);

        if(velocity.y < 0) startY = endY = (int) Math.floor(bounds.y + velocity.y);
        else startY = endY = (int) Math.floor(bounds.y + bounds.height + velocity.y);
        populateCollidableBlocks(startX, startY, endX, endY);
        enemyRect.y += velocity.y;
        world.getCollisionBlocks().clear();;

        for(Block block : collidable){
            if(block == null) continue;
            if(enemyRect.overlaps(block.getBounds())){
                velocity.y = 0;
                world.getCollisionBlocks().add(block.getBounds());
                break;
            }
        }

        enemyRect.y = position.y;

        position.add(velocity);
        bounds.x = position.x;
        bounds.y = position.y;
        velocity.scl(1/delta);

    }

    // GET COLLIDABLE BLOCKS
    private Array<Block> collidable = new Array<Block>();
    private void populateCollidableBlocks(int startX, int startY, int endX, int endY){
        collidable.clear();
        for(int x = startX; x <= endX; x++){
            for(int y = startY; y <= endY; y++){
                if(x >= 0 && x < world.getLevel().getWidth() && y >= 0 && y < world.getLevel().getHeight()) {
                    if(world.getLevel().getBlock(x, y) != null){
                        if(world.getLevel().getBlock(x, y).isCollideable()){
                            collidable.add(world.getLevel().getBlock(x, y));
                        }
                    }
                }
            }
        }
    }

    /**
     * HANDLE AI & DECISION MAKING
     */

    public void chaseTarget(Vector2 target){
        if(centerPoint.x <= target.x) {
            if(!bounds.overlaps(world.getPlayer().bounds))acceleration.x += SPEED;
            else state = State.IDLE;
        }
        if(centerPoint.x >= target.x) {
            if(!bounds.overlaps(world.getPlayer().bounds))acceleration.x -= SPEED;
            else state = State.IDLE;
        }
        if(centerPoint.y <= target.y) {
            if(!bounds.overlaps(world.getPlayer().bounds))acceleration.y += SPEED;
            else state = State.IDLE;
        }
        if(centerPoint.y >= target.y) {
            if(!bounds.overlaps(world.getPlayer().bounds))acceleration.y -= SPEED;
            else state = State.IDLE;
        }
    }

}
