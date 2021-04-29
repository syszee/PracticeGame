package com.syszee.practice.core;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Global {

    // CAMERA SHAKING --------------------------- /
    public static boolean SHAKE_CAMERA = false;
    public static int SHAKE_DURATION = 0;
    public static float MIN_SHAKE_AMP = 0;
    public static float MAX_SHAKE_AMP = 0;
    // ------------------------------------------- /

    // SHAKE CAMERA
    public static void shakeCamera(float maxAmp, float minAmp, int duration){
        SHAKE_DURATION = duration;
        MIN_SHAKE_AMP = minAmp;
        MAX_SHAKE_AMP = maxAmp;
        SHAKE_CAMERA = true;
    }

    // LOG
    public static void log(String s){
        System.out.println(s);
    }


}
