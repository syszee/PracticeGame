package com.syszee.practice.entities;

import com.badlogic.gdx.math.Vector2;

public class Particle {

    // TYPE
    public enum ParticleType {
        DEFAULT, BLOCK_BREAK
    }
    ParticleType particleType = ParticleType.DEFAULT;

    // PROPERTIES
    public static float SIZE = 1F;
    public static float LIFE_TIME = 1F;
    public Vector2 position;
    float stateTime = 0;
    boolean shouldRemove = false;

    public Particle(Vector2 position, ParticleType particleType, float size, float lifeTime){
        this.position = position;
        this.particleType = particleType;

        SIZE = size;
        LIFE_TIME = lifeTime;
    }

    // UPDATE
    public void update(float delta){
        stateTime += delta;

    }

    // GETTERS
    public ParticleType getType(){return particleType;}
    public float getStateTime(){return stateTime;}
    public Vector2 getPosition(){return position;}
    public float getSize(){return SIZE;}

    public void shouldRemove(Boolean b){this.shouldRemove = b;}
    public boolean checkRemoval(){return shouldRemove;}

}
