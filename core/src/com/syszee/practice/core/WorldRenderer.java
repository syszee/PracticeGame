package com.syszee.practice.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.syszee.practice.blocks.Block;
import com.syszee.practice.entities.Egg;
import com.syszee.practice.entities.Enemy;
import com.syszee.practice.entities.Particle;
import com.syszee.practice.entities.Player;
import com.syszee.practice.worlds.World;

import java.util.Comparator;
import java.util.Random;

public class WorldRenderer {

    private World world;

    // CAMERA -------------------------------------/
    private OrthographicCamera camera;
    private float shakeX, shakeY = 0F;

    private SpriteBatch spriteBatch;
    private Array<Sprite> sprites = new Array<Sprite>();
    ShapeRenderer shapeRenderer = new ShapeRenderer();

    // World Textures
    public static TextureAtlas atlas = new TextureAtlas("chicken_walk.txt");
    TextureRegion grassBlock, signBlock, rockBlock, rockBigBlock;


    public static final float CAMERA_WIDTH = 16F;
    public static final float CAMERA_HEIGHT = 9F;
    private int width, height;
    private float ppuX, ppuY;

    private PlayerRenderer playerRenderer;
    private ParticleRenderer particleRenderer;

    public void setSize(int w, int h){
        this.width = w;
        this.height = h;
        ppuX = (float) width / CAMERA_WIDTH;
        ppuY = (float) height / CAMERA_HEIGHT;
        camera.update();
    }

    public WorldRenderer(World world){
        this.world = world;
        camera = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.camera.update();
        spriteBatch = new SpriteBatch();
        playerRenderer = new PlayerRenderer(atlas);
        particleRenderer = new ParticleRenderer(atlas);
        loadTextures(); // Blocks
    }

    public void render(float delta, Array<Particle> particles){

        handleCamera();
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        drawBlocks(0, delta);

        particleRenderer.renderParticles(particles, spriteBatch);
        // UPDATE PLAYER, NOT RENDERING
        playerRenderer.renderPlayer(world.getPlayer(), spriteBatch, ppuX, ppuY);

        /** HANDLE SPRITES **/
        handleSprites(delta);

        drawBlocks(1, delta);
        spriteBatch.end();

    }

    /** SPRITE ARRAY HANDLER **/
    public void handleSprites(float delta){
        // CLEAR SPRITES
        sprites.clear();

        // GET ENEMIES
        for(Enemy enemy : world.getEnemies()){
            if(enemy == null) continue;
            else {
                enemy.render(spriteBatch);
                enemy.update(delta);
                //if(world.getPlayer().getPosition().y < enemy.getPosition().y)
                    //playerRenderer.renderPlayer(world.getPlayer(), spriteBatch, ppuX, ppuY);
                sprites.add(enemy.getSprite());
            }
        }

        // GET EGGS
        for(Egg egg : world.getEggs()){
            if(egg == null) continue;
            else {
                egg.render(spriteBatch);
                egg.update(delta);
                sprites.add(egg.getSprite());
            }
        }

        // GET PLAYER
        sprites.add(playerRenderer.getSprite());

        // SORT SPRITES BY Y-VALUE
        sprites.sort(new Comparator<Sprite>() {
            @Override
            public int compare(Sprite s1, Sprite s2) {
                return Float.compare(s2.getY(), s1.getY());
            }
        });

        // DRAW SPRITES
        for(Sprite s : sprites){
            if(s != null){
                s.draw(spriteBatch);
            }
        }

    }

    public void renderDebug(){
        // DEBUG

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setAutoShapeType(true);

        // Blocks
        for(Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)){
            Rectangle rect = block.getBounds();
            shapeRenderer.setColor(Color.WHITE);
            //shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }

        //drawCollisionBlocks();

        // EGGS
        for(Egg egg : world.getEggs()){
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(egg.getExplosionBound().x, egg.getExplosionBound().y, egg.getExplosionBound().width, egg.getExplosionBound().height);
        }

        for(Enemy enemy : world.getEnemies()){
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(enemy.getBounds().x, enemy.getBounds().y, enemy.getBounds().width, enemy.getBounds().height);


            // ESTABLISH PLAYER CENTER POINT
            Vector2 playerPoint = new Vector2();
            playerPoint.x = world.getPlayer().getPosition().x + (world.getPlayer().getBounds().width/2);
            playerPoint.y = world.getPlayer().getPosition().y + (world.getPlayer().getBounds().height/2);

            // ESTABLISH ENEMY CENTER POINT

            Vector2 enemyPoint = new Vector2();
            enemyPoint.x = enemy.getPosition().x + (enemy.getBounds().width/2);
            enemyPoint.y = enemy.getPosition().y + (enemy.getBounds().height/2);

            shapeRenderer.rect(enemy.getTargetSight().x, enemy.getTargetSight().y,
                    enemy.getTargetSight().width, enemy.getTargetSight().height);

            shapeRenderer.line(enemyPoint, playerPoint);
        }

        // Player
        Player player = world.getPlayer();
        Rectangle rect = player.getBounds();

        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 0.5F);
        // Target
        shapeRenderer.rect(player.getTargetPosition().x, player.getTargetPosition().y, 1F, 1F);
        // Player
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        shapeRenderer.rect(Math.round(rect.x), Math.round(rect.y), 1F, 1F);
        //shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);

        shapeRenderer.end();
    }

    public void drawCollisionBlocks() {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        for(Rectangle rect : world.getCollisionBlocks()){
            shapeRenderer.rect(rect.x, rect.y, rect.width,rect.height);
        }
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
    }

    public void drawBlocks(int layer, float delta){

        // Background Layer
        if(layer==0){
            for(Block block : world.getDrawableBackgroundLayerBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)){
                Rectangle rect = block.getBounds();
                if(block.getBlockType().equals(Block.BlockType.GRASS)) spriteBatch.draw(grassBlock, rect.x, rect.y, rect.width, rect.height);
            }
        }

        Random r = new Random();
        // float random = min + r.nextFloat() * (max - min);
        double breakShakeA, breakShakeB;
        int coinFlip = 0;

        // Foreground Layer
        if(layer==1){
            for(Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)){
                Rectangle rect = block.getBounds();

                float shakeAmp = 0.02F;

                if(block.getState().equals(Block.State.BREAKING)){
                    coinFlip = (int)Math.round(Math.random());
                    if(coinFlip == 0) breakShakeA = Math.random() * shakeAmp;
                    else breakShakeA = Math.random() * -shakeAmp;

                    coinFlip = (int)Math.round(Math.random());
                    if(coinFlip == 0) breakShakeB = Math.random() * shakeAmp;
                    else breakShakeB = Math.random() * -shakeAmp;

                }else{
                    breakShakeA = 0;
                    breakShakeB = 0;
                }

                if(block.getBlockType().equals(Block.BlockType.SIGN)) spriteBatch.draw(signBlock, rect.x + (float)breakShakeA, rect.y + (float) breakShakeB, block.getSize(), block.getSize());
                if(block.getBlockType().equals(Block.BlockType.ROCK_1)) spriteBatch.draw(rockBlock, rect.x + (float) breakShakeA, rect.y + (float) breakShakeB, block.getSize(), block.getSize());
                if(block.getBlockType().equals(Block.BlockType.ROCK_2)) spriteBatch.draw(rockBigBlock, rect.x + (float) breakShakeA, rect.y + (float) breakShakeB, block.getSize(), block.getSize());


            }
        }

    }

    public void dispose(){
        shapeRenderer.dispose();
        spriteBatch.dispose();
        playerRenderer.dispose();
    }

    public void loadTextures(){
        grassBlock = atlas.findRegion("grass_block");
        signBlock = atlas.findRegion("sign_block");
        rockBlock = atlas.findRegion("rock_block");
        rockBigBlock = atlas.findRegion("big_rock_block");

    }

    // HANDLE CAMERA
    public void handleCamera(){

        camera.position.set(world.getPlayer().getPosition().x + shakeX + world.getPlayer().getBounds().width/2, world.getPlayer().getPosition().y + shakeY + world.getPlayer().getBounds().height/2, 0);

        // IF CAMERA SHOULD BE SHAKING
        if(Global.SHAKE_CAMERA){
            shakeCamera();
        }else{
            shakeX = 0; shakeY = 0;
        }

    }

    public void shakeCamera(){
        Global.SHAKE_DURATION--;
        float shake = (float)Math.random() * (Global.MAX_SHAKE_AMP - Global.MIN_SHAKE_AMP) + Global.MIN_SHAKE_AMP;

        float flip = (int)Math.round(Math.random()) + 1;
        if(flip == 1) shakeX = shake;
        else shakeX = -shake;


        flip = (int)Math.round(Math.random()) + 1;
        if(flip == 1) shakeY = shake;
        else shakeY = -shake;

        if(Global.SHAKE_DURATION == 0) {
            Global.SHAKE_CAMERA = false;
        }
    }

}
