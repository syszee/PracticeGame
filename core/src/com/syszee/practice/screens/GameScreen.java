package com.syszee.practice.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.Array;
import com.syszee.practice.controller.WorldController;
import com.syszee.practice.core.GUIRenderer;
import com.syszee.practice.core.SoundManager;
import com.syszee.practice.core.WorldRenderer;
import com.syszee.practice.entities.Particle;
import com.syszee.practice.worlds.World;

public class GameScreen implements Screen, InputProcessor {

    private World world;
    private WorldRenderer renderer;
    private WorldController controller;
    private SoundManager soundManager;
    private GUIRenderer GUIrenderer;

    // PASS ALONGS
    public Array<Particle> particles;

    private boolean showDebug = false;

    @Override
    public void show() {
        soundManager = new SoundManager();
        world = new World();
        renderer = new WorldRenderer(world);
        GUIrenderer = new GUIRenderer(world);
        controller = new WorldController(world);
        particles = controller.getParticles();
        Gdx.input.setInputProcessor(this);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.8f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        controller.update(delta);
        renderer.render(delta, particles);
        GUIrenderer.render(delta);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if(showDebug) renderer.renderDebug();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        renderer.setSize(width, height);
        GUIrenderer.setSize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        renderer.dispose();
        Gdx.input.setInputProcessor(null);
        soundManager.dispose();
        GUIrenderer.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A) controller.leftPressed();
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) controller.rightPressed();
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W) controller.upPressed();
        if(keycode == Input.Keys.DOWN || keycode == Input.Keys.S) controller.downPressed();
        if(keycode == Input.Keys.SPACE) controller.spacePressed();
        if(keycode == Input.Keys.SHIFT_LEFT) controller.shiftPressed();

        // Debug
        if(keycode == Input.Keys.F3){
            if(showDebug) showDebug = false;
            else showDebug = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT || keycode == Input.Keys.A) controller.leftReleased();
        if(keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) controller.rightReleased();
        if(keycode == Input.Keys.UP || keycode == Input.Keys.W) controller.upReleased();
        if(keycode == Input.Keys.DOWN || keycode == Input.Keys.S) controller.downReleased();
        if(keycode == Input.Keys.SPACE) controller.spaceReleased();
        if(keycode == Input.Keys.SHIFT_LEFT) controller.shiftReleased();
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
