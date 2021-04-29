package com.syszee.practice.entities;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.syszee.practice.blocks.Block;
import com.syszee.practice.core.Global;
import com.syszee.practice.core.SoundManager;
import com.syszee.practice.core.WorldRenderer;
import com.syszee.practice.worlds.World;

/**
 *  E.G.G.S
 *  This is a crucial part of this game. Pecking is the players first attack,
 *  with this being the second, more special attack.
 */
public class Egg {

    // PRESENCE & PROPERTIES
    Vector2         position;
    float           SIZE;
    float           LIFE_TIME;
    boolean         shouldRemove = false;

    //--------- EXPLOSIVES
    float           explosionRadius;
    Rectangle       explosionBound;
    boolean         explode = false;

    // TYPE
    public enum Type { GENERIC, BOMB }
    Type            eggType;

    // VISUALS & SOUNDS
    Sound           actionSound;
    float           stateTime = 0;
    TextureRegion   eggFrame;
    Animation       eggAnim;
    Sprite          sprite = new Sprite();

    // CONTAINER
    Array<Egg> container;
    Array<Particle> particles;
    Block[][] worldBlocks;

    // INITIALIZE
    public Egg(Vector2 position, Type eggType, Array<Egg> container, Array<Particle> worldParticles, Block[][] worldBlocks){
        this.position = position;
        this.eggType = eggType;
        this.container = container;
        this.particles = worldParticles;
        this.worldBlocks = worldBlocks;

        SIZE = 1F;
        LIFE_TIME = 150F;
        initType();
        loadTextures();
        playInitialSound();
    }

    /** UPDATE **/
    public void update(float delta){
        stateTime += delta;

        // HANDLE LIFE CYCLE
        LIFE_TIME--;
        if(LIFE_TIME <= 0) {
            shouldRemove = true;
            SoundManager.playSound(actionSound);

            // HANDLE EXPLOSION PARTICLES
            int x1 = (int)explosionBound.x;
            int y1 = (int)explosionBound.y;
            int x2 = (int) ((int)explosionBound.x + explosionBound.width);
            int y2 = (int) ((int)explosionBound.y + explosionBound.height);

            // PARTICLES & BLOCK REMOVAL
            for(int col = x1; col < x2; col++){
                for(int row = y1; row < y2; row++){
                    particles.add(new Particle(
                            new Vector2(col, row),
                            Particle.ParticleType.BLOCK_BREAK,
                            1F,
                            1F
                    ));

                    Block b = worldBlocks[col][row];
                    if(b == null) continue;
                    else {
                        // NEEDS WORK
                        b.breakBlock(50F);
                        SoundManager.playSound(b.getBreakSound());
                        particles.add(new Particle(
                                new Vector2((int)b.getPosition().x, (int)b.getPosition().y),
                                Particle.ParticleType.BLOCK_BREAK,
                                1F,
                                1F));
                        worldBlocks[(int)b.getPosition().x][(int)b.getPosition().y] = null;
                    }

                }
            }

            // SEND CAMERA SHAKE
            Global.shakeCamera(0.08F, 0.02F, 15);
            remove();
        }
        else shouldRemove = false;

    }

    /** RENDER EGG **/
    public void render(SpriteBatch spriteBatch){

        switch(eggType){
            case BOMB: // SMALL BOMB
                eggFrame = (TextureRegion) eggAnim.getKeyFrame(stateTime, true);
                break;
            default: // NONE
                eggFrame = (TextureRegion) eggAnim.getKeyFrame(stateTime, true);
                break;
        }

        //spriteBatch.draw(eggFrame, position.x, position.y, SIZE, SIZE);
        sprite.setPosition(position.x, position.y);
        sprite.setSize(SIZE, SIZE);
        sprite.setRegion(eggFrame);

    }

    // INITIALIZE TYPE PROPERTIES
    public void initType(){
        switch(eggType){
            case GENERIC:
                System.out.println("GENERIC EGG");
                break;
            case BOMB:
                LIFE_TIME = 150F; // LIFE TIME
                actionSound = SoundManager.EXPLOSION_SMALL; // EXPLOSION SOUND

                explosionRadius = 1F; // EXPLOSION SIZE -------------- /
                explosionBound = new Rectangle();
                explosionBound.x = position.x - (int)explosionRadius;
                explosionBound.y = position.y - (int)explosionRadius;
                explosionBound.width = (explosionRadius*2)+explosionRadius;
                explosionBound.height = (explosionRadius*2)+explosionRadius;
                // --------------------------------------------------- /

                break;
        }
    }

    // GETTERS & SETTERS
    public Vector2 getPosition(){return position;}
    public Type getEggType(){return eggType;}
    public float getSize(){return SIZE;}
    public Sound getActionSound(){return actionSound;}
    public boolean shouldRemove(){return shouldRemove;}
    public Rectangle getExplosionBound(){return explosionBound;}
    public boolean shouldExplode(){return explode;}
    public Sprite getSprite(){ return sprite;}

    // HANDLE REMOVAL
    public void remove(){
        container.removeValue(this, true);
    }

    // LOAD TEXTURES
    public void loadTextures(){
        TextureAtlas atlas = WorldRenderer.atlas;
        switch(eggType){
            case BOMB:
                TextureRegion[] bombFrames = new TextureRegion[4];
                for(int i = 0; i < 4; i++){
                    bombFrames[i] = atlas.findRegion("small_bomb", i);
                }
                eggAnim = new Animation(0.08F, bombFrames);

                break;
            default:
                eggFrame = atlas.findRegion("bomb_temp");
                break;
        }
    }

    // PLAY INITIAL CREATION SOUND
    public void playInitialSound(){
        switch(eggType){
            case BOMB:
                SoundManager.playSound(SoundManager.BOMB_SIZZLE);
                break;
        }
    }

}
