package com.syszee.practice.core;

import com.badlogic.gdx.graphics.g2d.*;
import com.syszee.practice.entities.Player;

public class PlayerRenderer {

    TextureAtlas atlas;

    private static final float IDLE_FRAME_DURATION = 0.25F;
    private static final float RUNNING_FRAME_DURATION = 0.10F;
    private static final float PECKING_FRAME_DURATION = 0.1F;

    // Texture Regions
    private TextureRegion playerIdleLeft, playerIdleRight,
            playerFrame, playerIdleSitLeft, playerIdleSitRight;

    // Animations
    private Animation walkLeftAnim, walkRightAnim,
            idleLeftAnim, idleRightAnim,
            peckLeftAnim, peckRightAnim;

    Sprite sprite = new Sprite();


    public PlayerRenderer(TextureAtlas atlas){
        this.atlas = atlas;
        loadTextures();
    }

    public void loadTextures() {
        playerIdleLeft = atlas.findRegion("chicken_idle");
        playerIdleRight = new TextureRegion(playerIdleLeft);
        playerIdleRight.flip(true, false);
        playerIdleSitLeft = atlas.findRegion("chicken_sit");
        playerIdleSitRight = new TextureRegion(playerIdleSitLeft);
        playerIdleSitRight.flip(true, false);

        // Idle Left
        TextureRegion[] idleLeftFrames = new TextureRegion[2];
        for(int i = 0; i < 2; i++){
            idleLeftFrames[i] = atlas.findRegion("chicken_idle", i);
        }
        idleLeftAnim = new Animation(IDLE_FRAME_DURATION, idleLeftFrames);

        // Idle Right
        TextureRegion[] idleRightFrames = new TextureRegion[2];
        for(int i = 0; i < 2; i++){
            idleRightFrames[i] = new TextureRegion(idleLeftFrames[i]);
            idleRightFrames[i].flip(true, false);
        }
        idleRightAnim = new Animation(IDLE_FRAME_DURATION, idleRightFrames);

        // Walk Left
        TextureRegion[] walkLeftFrames = new TextureRegion[4];
        for(int i = 0; i < 4; i++){
            walkLeftFrames[i] = atlas.findRegion("chicken_walk", i);
        }
        walkLeftAnim = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);

        // Walk Right
        TextureRegion[] walkRightFrames = new TextureRegion[4];
        for(int i = 0; i < 4; i++){
            walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
            walkRightFrames[i].flip(true, false);
        }
        walkRightAnim = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);

        // Peck Left
        TextureRegion[] peckLeftFrames = new TextureRegion[2];
        for(int i = 0; i < 2; i++){
            peckLeftFrames[i] = atlas.findRegion("chicken_peck", i);
        }
        peckLeftAnim = new Animation(PECKING_FRAME_DURATION, peckLeftFrames);

        // Peck Right
        TextureRegion[] peckRightFrames = new TextureRegion[2];
        for(int i = 0; i < 2; i++){
            peckRightFrames[i] = new TextureRegion(peckLeftFrames[i]);
            peckRightFrames[i].flip(true, false);
        }
        peckRightAnim = new Animation(PECKING_FRAME_DURATION, peckRightFrames);

    }

    public void renderPlayer(Player player, SpriteBatch batch, float ppuX, float ppuY){
        // Idle
        playerFrame = (TextureRegion) (player.isFacingLeft() ? idleLeftAnim.getKeyFrame(player.getStateTime(), true) : idleRightAnim.getKeyFrame(player.getStateTime(), true));
        if(player.getState().equals(Player.State.IDLE_SIT) || player.getState().equals(Player.State.PLACING_EGG))
            playerFrame = player.isFacingLeft() ? playerIdleSitLeft : playerIdleSitRight;

        // Walking
        if(player.getState().equals(Player.State.WALKING)){
            playerFrame = (TextureRegion) (player.isFacingLeft() ? walkLeftAnim.getKeyFrame(player.getStateTime(), true) : walkRightAnim.getKeyFrame(player.getStateTime(), true));
        }

        // Pecking
        if(player.getState().equals(Player.State.PECKING)){
            playerFrame = (TextureRegion) (player.isFacingLeft() ? peckLeftAnim.getKeyFrame(player.getPeckTime(), true) : peckRightAnim.getKeyFrame(player.getPeckTime(), true));

        }

        // Return Draw
        float sizeOffset = Math.nextUp((Player.SCALE-Player.SIZE)/2);

        //batch.draw(playerFrame, player.getPosition().x - sizeOffset, player.getPosition().y - sizeOffset, Player.SCALE, Player.SCALE);
        sprite.setRegion(playerFrame);
        sprite.setPosition(player.getPosition().x - sizeOffset, player.getPosition().y - sizeOffset);
        sprite.setSize(Player.SCALE, Player.SCALE);
    }

    public void dispose(){
        atlas.dispose();
    }

    public Sprite getSprite(){return sprite;}

}
