package com.syszee.practice.entities;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.syszee.practice.core.SoundManager;

public class Player {

    public enum State {
        IDLE, IDLE_SIT, WALKING, PECKING, PLACING_EGG
    }

    // Properties
    public static float SPEED = 35F;
    public static final float DAMP = 0.8F;
    public static final float MAX_VELOCITY = 20f;
    public static final float SIZE = 0.8F;
    public static final float SCALE = 1F;
    public static float PECK_POWER = 0.1F;

    Vector2 position;
    Vector2 acceleration = new Vector2();
    Vector2 velocity = new Vector2();
    Vector2 targetBlock = new Vector2();
    float stateTime = 0;
    boolean isFacingLeft = true;
    boolean isPecking = false;
    float peckTime = 0;
    Rectangle bounds = new Rectangle();

    State state = State.IDLE;

    // Timers
    private float timeIdle;
    private float soundTimer;
    private int shiftTimer = 0;

    // GAME VALUES
    int health = 3;

    public Player(Vector2 position){
        this.position = position;
        this.bounds.height = SIZE;
        this.bounds.width = SIZE;
        this.bounds.x = position.x;
        this.bounds.y = position.y;

    }

    public void update(float delta){
        stateTime += delta;

        // Idle Sit Timer
        if(state.equals(State.IDLE) || state.equals(State.IDLE_SIT)){
            timeIdle++;
            //System.out.println("IDLE: " + timeIdle);

            //Sit
            if(timeIdle > 1000) setState(State.IDLE_SIT);

        }else timeIdle = 0;

        // Peck Time
        if(state.equals(State.PECKING)) peckTime+=delta;
        else peckTime = 0;

        // SHIFT TIME
        if(state.equals(State.PLACING_EGG)){
            shiftTimer++;
            if(shiftTimer == 1){
                // HANDLE ANY EGG-PLACING FX OR SOUNDS
                SoundManager.playSound(SoundManager.PLACE_EGG);
            }
        }else shiftTimer = 0;

        // Find Target
        findTarget();
        handleSounds();

    }

    // GRAB TARGET
    public void findTarget(){
        if(isFacingLeft){
            targetBlock.x = Math.round(position.x -1);
            targetBlock.y = (int)position.y;
        }else{
            targetBlock.x = (int)position.x + 1;
            targetBlock.y = (int)position.y;
        }

    }

    // HANDLE SOUNDS
    public void handleSounds(){

        float pitchShift = (float) Math.random() * (1.2F - 0.8F) + 0.8F;

        // WALKING
        if(state.equals(State.WALKING)) {
            soundTimer++;
            if (soundTimer == 5) SoundManager.WALKING_GRASS.play(0.15F, pitchShift - 0.2F, 1F);
            if (soundTimer >= 20)
                soundTimer = 0;
        }

        // PECKING
        else if(state.equals(State.PECKING)) {
            soundTimer++;
            if(soundTimer == 5) SoundManager.PECK.play(0.5F, pitchShift, 1F);
            if(soundTimer >= 30)
                soundTimer = 0;
        }else{
            soundTimer = 0;
        }


    }

    public Rectangle getBounds(){ return bounds; }
    public Vector2 getPosition(){ return position; }
    public Vector2 getVelocity(){ return velocity; }
    public Vector2 getTargetPosition(){ return targetBlock;}
    public Vector2 getAcceleration(){ return acceleration; }
    public float getStateTime(){ return stateTime; }
    public float getPeckTime(){ return peckTime; }
    public void setSpeed(float s){this.SPEED = s;}
    public boolean isFacingLeft(){ return isFacingLeft;}
    public void setFacingLeft(Boolean b){this.isFacingLeft = b;}
    public State getState() { return state;}
    public float getPeckPower() {return PECK_POWER;}
    public void setPeckPower(float power) {PECK_POWER = power;}

    public void setState(State state){this.state = state;}
    public void setPosition(Vector2 position){
        this.position = position;
        this.bounds.setX(position.x);
        this.bounds.setY(position.y);
    }

    // GET VALUES
    public int getHealth(){return health;}


}
