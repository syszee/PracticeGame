package com.syszee.practice.core;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.syszee.practice.entities.Particle;

public class ParticleRenderer {

    TextureAtlas atlas;

    // SPEEDS
    private static final float ROCK_BREAK_FRAME_DURATION = 0.10F;

    // TEXTURE REGIONS
    private TextureRegion particleFrame;

    // ANIMATIONS
    private Animation particleAnim, rockBreakAnim;

    public ParticleRenderer(TextureAtlas atlas){
        this.atlas = atlas;
        loadTextures();
    }

    public void loadTextures(){
        //ROCK BREAK
        TextureRegion[] rockBreakFrames = new TextureRegion[4];
        for(int i = 0; i < 4; i++){
            rockBreakFrames[i] = atlas.findRegion("rock_break", i);
        }
        rockBreakAnim = new Animation(ROCK_BREAK_FRAME_DURATION, rockBreakFrames);

    }

    public void renderParticles(Array<Particle> particles, SpriteBatch spriteBatch){

       for(Particle particle : particles){
           // CHOOSE PARTICLE
           if(particle == null) continue;
           if(particle.getType().equals(Particle.ParticleType.BLOCK_BREAK)){
               particleAnim = rockBreakAnim;
           }

           particleFrame = (TextureRegion) particleAnim.getKeyFrame(particle.getStateTime(), false);
           // DRAW PARTICLE
           spriteBatch.draw(particleFrame, particle.getPosition().x, particle.getPosition().y, particle.getSize(), particle.getSize());

           // CHECK REMOVAL STATUS
           particle.shouldRemove(particleAnim.isAnimationFinished(particle.getStateTime()));
       }

    }

}
