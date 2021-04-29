package com.syszee.practice.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    public static final Sound WALKING_GRASS = createSound("walking_grass");
    public static final Sound PECK = createSound("peck");
    public static final Sound PLACE_EGG = createSound("place_egg");

    // BLOCKS
    public static final Sound BLOCK_BREAK = createSound("block_break");
    public static final Sound BLOCK_BREAK_WOOD = createSound("block_break_wood");

    // EGGS
    public static final Sound EXPLOSION_SMALL = createSound("explosion_small");
    public static final Sound BOMB_SIZZLE = createSound("bomb_sizzle");

    public static Sound createSound(String soundID){
        return Gdx.audio.newSound(Gdx.files.internal("sounds/" + soundID + ".wav"));
    };

    public static void playSound(Sound sound){
        float pitchShift = (float) Math.random() * (1.2F - 0.8F) + 0.8F;
        sound.play(0.5F, pitchShift, 1F);
    }

    public void dispose(){
        WALKING_GRASS.dispose();
        PECK.dispose();
        BLOCK_BREAK.dispose();
    }

}
