package com.syszee.practice.controller;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.sun.glass.ui.EventLoop;
import com.syszee.practice.blocks.Block;
import com.syszee.practice.core.SoundManager;
import com.syszee.practice.entities.Egg;
import com.syszee.practice.entities.Particle;
import com.syszee.practice.entities.Player;
import com.syszee.practice.worlds.World;

import java.util.HashMap;
import java.util.Map;

public class WorldController {

    enum Keys {
        LEFT, RIGHT, UP, DOWN, SPACE, SHIFT
    }

    private World world;
    private Player player;
    private boolean hasPlacedEgg = false;

    static Map<Keys, Boolean> keys = new HashMap<Keys, Boolean>();
    static {
      keys.put(Keys.LEFT, false);
      keys.put(Keys.RIGHT, false);
      keys.put(Keys.DOWN, false);
      keys.put(Keys.UP, false);
      keys.put(Keys.SPACE, false);
      keys.put(Keys.SHIFT, false);
    };

    // This rectangle pool is used in collision detection
    private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
        @Override
        protected Rectangle newObject() {
            return new Rectangle();
        }
    };
    private Array<Block> collidable = new Array<Block>();

    // PARTICLES
    public Array<Particle> particles = new Array<Particle>();

    public WorldController(World world){
        this.world = world;
        this.player = world.getPlayer();
    }

    /** KEY PRESSES **/
    public void leftPressed(){keys.put(Keys.LEFT, true);}
    public void rightPressed(){keys.put(Keys.RIGHT, true);}
    public void upPressed(){keys.put(Keys.UP, true);}
    public void downPressed(){keys.put(Keys.DOWN, true);}
    public void spacePressed(){keys.put(Keys.SPACE, true);}
    public void shiftPressed(){keys.put(Keys.SHIFT, true);}
    public void leftReleased(){keys.put(Keys.LEFT, false);}
    public void rightReleased(){keys.put(Keys.RIGHT, false);}
    public void upReleased(){keys.put(Keys.UP, false);}
    public void downReleased(){keys.put(Keys.DOWN, false);}
    public void spaceReleased(){keys.put(Keys.SPACE, false);}
    public void shiftReleased(){keys.put(Keys.SHIFT, false);}

    public void update(float delta){
        processInput();

        player.getAcceleration().scl(delta);
        player.getVelocity().add(player.getAcceleration().x, player.getAcceleration().y);

        checkCollisionWithBlocks(delta);

        player.getVelocity().x *= Player.DAMP;
        player.getVelocity().y *= Player.DAMP;

        if(player.getVelocity().x > Player.MAX_VELOCITY) player.getVelocity().x = Player.MAX_VELOCITY;
        if(player.getVelocity().x < -Player.MAX_VELOCITY) player.getVelocity().x = -Player.MAX_VELOCITY;
        if(player.getVelocity().y > Player.MAX_VELOCITY) player.getVelocity().y = Player.MAX_VELOCITY;
        if(player.getVelocity().y < -Player.MAX_VELOCITY) player.getVelocity().y = -Player.MAX_VELOCITY;

        player.update(delta);
        for(Particle particle : particles){
            particle.update(delta);
            if(particle.checkRemoval()) particles.removeValue(particle, true);
        }

    }

    /** COLLISION **/
    private void checkCollisionWithBlocks(float delta){

        player.getVelocity().scl(delta);

        Rectangle playerRect = rectPool.obtain();
        playerRect.set(player.getBounds().x, player.getBounds().y, player.getBounds().width, player.getBounds().height);

        /* First up, we check the movement
           of the player along the horizontal X axis. */

        int startX, endX;
        int startY = (int) player.getBounds().y;
        int endY = (int) (player.getBounds().y + player.getBounds().height);

        // If left, check collidable blocks left. Otherwise, check right.
        if(player.getVelocity().x < 0)
            startX = endX = (int) Math.floor(player.getBounds().x + player.getVelocity().x);
        else
            startX = endX = (int) Math.floor(player.getBounds().x + player.getBounds().width + player.getVelocity().x);

        // Get collidable blocks
        populateCollidableBlocks(startX, startY, endX, endY);

        // predictive rectangle keep up with player velocity
        playerRect.x += player.getVelocity().x;
        world.getCollisionBlocks().clear();

        // Handle horizontal collision
        for(Block block : collidable){
            if(block == null) { continue; }
            if(playerRect.overlaps(block.getBounds())){
                player.getVelocity().x = 0;
                world.getCollisionBlocks().add(block.getBounds());
                break;
            }
        }

        playerRect.x = player.getPosition().x;

        // Now let's do the same thing, but for the vertical axis
        startX = (int) player.getBounds().x;
        endX = (int) (player.getBounds().x + player.getBounds().width);

        if(player.getVelocity().y < 0) startY = endY = (int) Math.floor(player.getBounds().y + player.getVelocity().y);
        else startY = endY = (int) Math.floor(player.getBounds().y + player.getBounds().height + player.getVelocity().y);

        populateCollidableBlocks(startX, startY, endX, endY);

        playerRect.y += player.getVelocity().y;

        for(Block block : collidable){
            if(block == null) continue;
            if(playerRect.overlaps(block.getBounds())){
                player.getVelocity().y = 0;
                world.getCollisionBlocks().add(block.getBounds());
                break;
            }
            block.update(delta);
        }

        playerRect.y = player.getPosition().y;


        // Update player position
        player.getPosition().add(player.getVelocity());
        player.getBounds().x = player.getPosition().x;
        player.getBounds().y = player.getPosition().y;

        player.getVelocity().scl(1/delta);

    }

    private void populateCollidableBlocks(int startX, int startY, int endX, int endY){
        collidable.clear();
        for(int x = startX; x <= endX; x++){
            for(int y = startY; y <= endY; y++){
                if(x >= 0 && x < world.getLevel().getWidth() && y >= 0 && y < world.getLevel().getHeight()) {
                    if(world.getLevel().getBlock(x, y) != null){
                        if(world.getLevel().getBlock(x, y).isCollideable()){
                            collidable.add(world.getLevel().getBlock(x, y));
                        }
                    }
                }
            }
        }
    }

    /** END COLLISION **/

    private void processInput(){

        if(keys.get(Keys.LEFT)){
            if(!keys.get(Keys.SPACE) && !keys.get(Keys.SHIFT)){
                player.setState(Player.State.WALKING);
                player.getAcceleration().x = -Player.SPEED;
                player.setFacingLeft(true);
            }
        }

        if(keys.get(Keys.RIGHT)) {
            if(!keys.get(Keys.SPACE) && !keys.get(Keys.SHIFT)){
                player.setState(Player.State.WALKING);
                player.getAcceleration().x = Player.SPEED;
                player.setFacingLeft(false);
            }
        }

        if(keys.get(Keys.UP)){
            if(!keys.get(Keys.SPACE) && !keys.get(Keys.SHIFT)){
                player.setState(Player.State.WALKING);
                player.getAcceleration().y = Player.SPEED;
            }
        }

        if(keys.get(Keys.DOWN)){
            if(!keys.get(Keys.SPACE) && !keys.get(Keys.SHIFT)){
                player.setState(Player.State.WALKING);
                player.getAcceleration().y = -Player.SPEED;
            }
        }

        // NOT MOVING or PLACING EGG
        if(!keys.get(Keys.DOWN) && !keys.get(Keys.UP) && !keys.get(Keys.LEFT) && !keys.get(Keys.RIGHT) && !keys.get(Keys.SHIFT)){
            player.setState(Player.State.IDLE);
        }

        // PECKING
        if(keys.get(Keys.SPACE)){
            player.setState(Player.State.PECKING);
            handleAttack();
        }else if(!keys.get(Keys.SPACE)){
            if(getPlayerTargetBlock() != null) getPlayerTargetBlock().resetDurability();
        }

        // PLACING EGG
        if(keys.get(Keys.SHIFT)){
            player.setState(Player.State.PLACING_EGG);
            if(!hasPlacedEgg) {
                world.getEggs().add(new Egg(
                        new Vector2(player.getPosition().x, player.getPosition().y),
                        Egg.Type.BOMB,
                        world.getEggs(),
                        particles,
                        world.getLevel().getBlocks()
                ));

                hasPlacedEgg = true;
            }
        }else {
            hasPlacedEgg = false;
        }

    }

    public void handleAttack(){
        Block targetBlock = getPlayerTargetBlock();
        if(targetBlock != null){
            int x = (int)targetBlock.getPosition().x;
            int y = (int)targetBlock.getPosition().y;

            targetBlock.breakBlock(player.getPeckPower());
            if(targetBlock.shouldBreak()){

                SoundManager.playSound(targetBlock.getBreakSound());
                particles.add(new Particle(new Vector2(x, y), Particle.ParticleType.BLOCK_BREAK, 1F, 1F));
                world.getLevel().getBlocks()[x][y] = null;
            }
        }
    }

    public Block getPlayerTargetBlock(){
        return world.getLevel().getBlock((int)player.getTargetPosition().x, (int)player.getTargetPosition().y);
    }

    // RETURN PARTICLES
    public Array<Particle> getParticles(){return particles;}


}
