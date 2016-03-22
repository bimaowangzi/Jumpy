package com.mygdx.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.HUD.Controls;
import com.mygdx.InputHandlerClass;
import com.mygdx.Jumpy;
import com.mygdx.PlatformGenerator;
import com.mygdx.PowerUps;
import com.mygdx.Sprites.Player;
import com.mygdx.appwarp.WarpController;
import com.mygdx.appwarp.WarpListener;

/**
 * Created by acer on 16/2/2016.
 */
public class PlayScreen implements Screen
{
    private Jumpy game;

    private float accelX = Gdx.input.getAccelerometerX();

    private boolean accelMode;

    private OrthographicCamera gamecam;
    private Viewport gamePort;

    private TmxMapLoader mapLoader;                 //load the map into the game
    private TiledMap map;                           //reference to the map itself
    private OrthogonalTiledMapRenderer renderer;    //renders the map to the screen

    private World world;
    private Box2DDebugRenderer b2dr;

    private SpriteBatch batch = new SpriteBatch();
    private Controls controls = new Controls(batch);

    private Player player;

    private PlatformGenerator platformgenerator;
    private PlatformGenerator platformgenerator2;

    private PowerUps powerup;
    private long lastPowerupTime;
    private Texture powerUpImg = new Texture(Gdx.files.internal("PowerUp.png"));

    private float nextInterval;
    public static float velocityScale = 0.9f;
    private long effectiveTime;
    private int powerUpType = 0;
    private boolean flyFlag = false;

    private ShapeRenderer shapeRenderer;

    public PlayScreen(Jumpy game){
        this.game = game;

        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(Jumpy.WIDTH, Jumpy.HEIGHT, gamecam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("test3.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        gamecam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);

        world = new World(new Vector2(0,-70),true);
        b2dr = new Box2DDebugRenderer();
        player = new Player(world,Jumpy.WIDTH/2,13);

        world.setContactFilter(player);
        world.setContactListener(player);

        controls.getButtonLeft().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.b2body.setLinearVelocity(-10*velocityScale, player.b2body.getLinearVelocity().y);
            }
        });

        controls.getButtonRight().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                    player.b2body.setLinearVelocity(10*velocityScale, player.b2body.getLinearVelocity().y);
            }
        });

        controls.getButtonJump().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try{
                int state = (Integer) player.b2body.getUserData();
                    if(state==0) {
                        player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 35*velocityScale);
                    }
                }
                catch(NullPointerException e){}
            }
        });

        accelMode = false;

        controls.getToggleButton().addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (controls.getToggleButton().isChecked()){
                    controls.getToggleButton().setText("ACCEL");
                    controls.getButtonLeft().setVisible(false);
                    controls.getButtonRight().setVisible(false);
                    controls.getButtonJump().setVisible(false);
                    accelMode = true;
                } else {
                    controls.getToggleButton().setText("DPAD");
                    accelMode = false;
                    controls.getButtonLeft().setVisible(true);
                    controls.getButtonRight().setVisible(true);
                    controls.getButtonJump().setVisible(true);
                }
            }
        });

        InputHandlerClass inputHandlerClass = new InputHandlerClass(this);
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(controls.stage);
        inputMultiplexer.addProcessor(inputHandlerClass.getInputProcessor());
        Gdx.input.setInputProcessor(inputMultiplexer);

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(19, 4);
        body = world.createBody(bdef);
        shape.setAsBox(19, 4);
        fdef.shape = shape;
        body.createFixture(fdef);

        platformgenerator = new PlatformGenerator(body, -20,0,4,8,4,7,0.1f);
        platformgenerator2 = new PlatformGenerator(body, 0,20,4,8,4,7,0.1f);

        lastPowerupTime = TimeUtils.millis();
        spawnPowerup();

        batch = new SpriteBatch();

        //Generate platforms on first screen
        for(int i = 1;i<16;i++) {
            float y = 4*i;
            float width = MathUtils.random(4, 7);
            float x = MathUtils.random(-20, 0 - width);
            PolygonShape shape2 = new PolygonShape();
            shape2.setAsBox(width / 2, 1 / 2, new Vector2(x + width / 2, y + 0.1f / 2), 0);
            Fixture fdef2 = body.createFixture(shape2, 0);
            int platformID = MathUtils.random(0, 3);
            fdef2.setUserData(platformID);

            width = MathUtils.random(4, 7);
            x = MathUtils.random(0, 20 - width);
            PolygonShape shape3 = new PolygonShape();
            shape3.setAsBox(width / 2, 1 / 2, new Vector2(x + width / 2, y + 0.1f / 2), 0);
            Fixture fdef3 = body.createFixture(shape3, 0);
            platformID = MathUtils.random(0, 3);
            fdef3.setUserData(platformID);

            shape2.dispose();
            shape3.dispose();

        }


    }

    private void spawnPowerup() {
        powerup = new PowerUps(Jumpy.WIDTH, gamecam.position.y-Jumpy.HEIGHT/2, world);
        lastPowerupTime = TimeUtils.millis();
        nextInterval = MathUtils.random(6f, 10f) * 1000;
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt){

        if (accelMode){
            player.b2body.setLinearVelocity(-20 * accelX * velocityScale / 6f,player.b2body.getLinearVelocity().y);
        } else {
            if(controls.getButtonJump().isPressed()){
                try{
                    int state = (Integer) player.b2body.getUserData();

                    if(state==0) {
                        player.b2body.setLinearVelocity(player.b2body.getLinearVelocity().x, 35*velocityScale);
                    }
                }
                catch(NullPointerException e){}
            }
            if(controls.getButtonRight().isPressed()){
                player.b2body.setLinearVelocity(10*velocityScale, player.b2body.getLinearVelocity().y);
            }

            if(controls.getButtonLeft().isPressed()){
                player.b2body.setLinearVelocity(-10*velocityScale, player.b2body.getLinearVelocity().y);
            }
        }
    }



    public void update(float dt){
        if (accelMode){
            accelX = Gdx.input.getAccelerometerX();
        }
        handleInput(dt); //check for any user inputs

        world.step(1 / 60f, 6, 2);
        gamecam.position.y = gamecam.position.y + 0.1f;
//        System.out.println("Player :"+player.getPosY());
//        System.out.println("Camera :"+gamecam.position.y);
        //gamecam.position.y = player.b2body.getPosition().y;

        if(player.getPosY()<gamecam.position.y-33){
            //System.out.println("Player dies");
            //world.destroyBody(player);
            try{
                player.deleteBody();
                player = new Player(world,Jumpy.WIDTH/2,(int)gamecam.position.y);
                world.setContactFilter(player);
            }
            catch(NullPointerException e){
                e.getMessage();
            }
        }

        if(player.getPosX()<gamecam.position.x-20){
            try{
                float tempPosY = player.getPosY();
                player.deleteBody();
                player = new Player(world,Jumpy.WIDTH,(int) tempPosY);
                world.setContactFilter(player);
            }
            catch(NullPointerException e){
                e.getMessage();
            }
        }

        if(player.getPosX()>gamecam.position.x+20){
            try{
                float tempPosY = player.getPosY();
                player.deleteBody();
                player = new Player(world,0,(int) tempPosY);
                world.setContactFilter(player);
            }
            catch(NullPointerException e){
                e.getMessage();
            }
        }

        if(player.getPosY()>gamecam.position.y+20){
            gamecam.position.y = player.getPosY()-20;
        }


        if (powerup!=null) {
            powerup.update(dt);
            if (powerup.powerupBody.getPosition().dst(player.b2body.getPosition()) < 3.5) {
                effectiveTime = TimeUtils.millis();
                powerUpType = powerup.getType();
            }
            if (powerup.getPosY()>gamecam.position.y+Jumpy.HEIGHT/2)
                powerup = null;
        }

        if (TimeUtils.timeSinceMillis(effectiveTime) < 3000) {
            if (powerUpType==1) velocityScale = 2f;
            else if (powerUpType==2)velocityScale = 0.7f;
            else if (powerUpType==3) {
                player.b2body.setLinearVelocity(powerup.powerupBody.getLinearVelocity());
                flyFlag = true;
            }
        } else {
            velocityScale = 1;
            if (flyFlag) {
                player.b2body.setLinearVelocity(0,0);
                flyFlag = false;
            }
        }

        if (TimeUtils.timeSinceMillis(lastPowerupTime)>nextInterval)
            spawnPowerup();



        gamecam.update();
        renderer.setView(gamecam);
    }


    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);//(R,G,B,alpha)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();
        b2dr.render(world, gamecam.combined);

        //game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
       // hud.stage.draw();
//        //to render what the camera can see
//        game.batch.setProjectionMatrix(gamecam.combined);

        controls.stage.draw();

//        batch.begin();
//        if (powerup!=null)
//            batch.draw(powerUpImg, powerup.getPosX(), powerup.getPosY(),64,64);
//
//        batch.end();

        platformgenerator.generate(gamecam.position.y + gamecam.viewportHeight / 2);

        platformgenerator2.generate(gamecam.position.y + gamecam.viewportHeight / 2);

//        shapeRenderer.begin();
//        shapeRenderer.setColor(1, 0, 0, 0);
//        shapeRenderer.rect(platformgenerator.getX(), platformgenerator.getY(), platformgenerator.getWidth(),platformgenerator.getHeight());
//        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public boolean isAccelMode() {
        return accelMode;
    }

    public Player getPlayer() {
        return player;
    }

}
