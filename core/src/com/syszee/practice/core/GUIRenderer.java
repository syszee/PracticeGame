package com.syszee.practice.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.syszee.practice.worlds.World;

public class GUIRenderer {

    // TEXTURES
    public static TextureAtlas atlasGUI;
    SpriteBatch                batchGUI;
    OrthographicCamera         camera;
    TextureRegion              test;
    TextureRegion              inventory_slot;

    // PASS-THROUGHS
    World                      world;

    // VALUES
    int                        screenWidth;
    int                        screenHeight;

    public static final float CAMERA_WIDTH = 32F;
    public static final float CAMERA_HEIGHT = 18F;

    public GUIRenderer(World world){
        this.world = world;
        batchGUI = new SpriteBatch();
        camera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        camera.position.set(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, 0);
        camera.update();
        loadTextures();
    }

    // LOAD TEXTURES
    public void loadTextures(){
        Global.log("Loading GUI textures..."); // LOG
        atlasGUI = new TextureAtlas("gui.txt");

        test = atlasGUI.findRegion("full_heart"); // HEART
        inventory_slot = atlasGUI.findRegion("inventory_slot"); // INVENTORY SLOT

        Global.log("GUI textures loaded!"); // LOG

    }

    // RENDER
    public void render(float delta){

        batchGUI.setProjectionMatrix(camera.combined);
        batchGUI.begin();

        // HEARTS
        batchGUI.draw(test, 0, CAMERA_HEIGHT-4, 4, 4);
        batchGUI.draw(test, 3, CAMERA_HEIGHT-4, 4, 4);
        batchGUI.draw(test, 6, CAMERA_HEIGHT-4, 4, 4);

        // INVENTORY SLOT
        batchGUI.draw(inventory_slot, CAMERA_WIDTH-5, 1, 4, 4);

        batchGUI.end();
    }

    public void setSize(int width, int height){
        //batchGUI.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
    }

    // DISPOSE
    public void dispose(){
        atlasGUI.dispose();
    }

}
