package com.android.flappydragon;

import com.android.flappydragon.scenes.MainMenu;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;


public class FlappyDragon extends Game {

    public boolean screnPerspective = true;
    public boolean gamePerspective = false;

    //TODO Atributos para configurar diferentes resoluções
    private OrthographicCamera camera;
    private Viewport viewPort;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

	//TODO Componentes do jogo
	public SpriteBatch batch;
	private ShapeRenderer shape;
	private Texture[] dragonAnimation;
    private Texture[] energyAnimation;
	public Texture backGround;
	private Texture topMountain;
	private Texture bottomMountain;
	public BitmapFont font;
	private BitmapFont stateMessage;
    private BitmapFont temportalModMessage;

	//Todo figuras para colisões
	private Rectangle dragonCollisionArea;
	private Rectangle topMountainCollisionArea;
	private Rectangle bottomMountainCollisionArea;
    private Circle timeEnergyArea;


	//TODO Configurações do jogo
	private float deltaTime;
    public float displayHeight;
    public float displayWidth;
    private Random randomNumber = new Random();
    private int score = 0;
    private  boolean scoreFlag = false;
    private int normalTime = 200;



    //TODO Configurações do Dragão
	private float dragonDropSpeed = 0;
    private float dragonStartVerticalPosition;
    private float dragonStartHorizontalPosition;
    private float dragonFrameVariation = 0;
    private int dragonWidth;
    private int dragonHeight;
    private final int agilityOfWings= 8;
    private final int flyAgility = -8;

    //TODO Configurações da energia do tempo
    private float energyVariableHorizontalPosition = 0;
    private int temporalMod = 100;
    private float timeSeconds = 0f;
    private float period = 6f;
    private boolean temporalTimeFlag = false;
    private float energyFrameVariation = 0;

    //TODO Configurações das árvores
    private float mountainVariableHorizontalPosition;
	private int spaceMountain = 200;
	private float randomMountainHeight;

	//TODO gameState = PAUSED -> Jogo não iniciado // gameState = STARTED -> Jogo Iniciado // gameState = TEMPORALFORM-> Dragão na forma temporal
	private GameState gameState = GameState.PAUSED;


    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
    }

    @Override
	public void create () {

        setScreen(new MainMenu(this));

	    //TODO Configurações da camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2,0);
        viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		batch = new SpriteBatch();
		shape = new ShapeRenderer();

		//TODO Inicializando os frames do Dragão
		dragonAnimation = new Texture[6];
		dragonAnimation[0] = new Texture("dragon-frame-1.png");
		dragonAnimation[1] = new Texture("dragon-frame-2.png");
		dragonAnimation[2] = new Texture("dragon-frame-3.png");
		dragonAnimation[3] = new Texture("dragon-frame-4.png");
        dragonAnimation[4] = new Texture("dragon-frame-5.png");
        dragonAnimation[5] = new Texture("dragon-frame-6.png");

        //TODO Inicilizando os frames da energia do tempo
        energyAnimation = new Texture[7];
        energyAnimation[0] = new Texture("energy_1.png");
        energyAnimation[1] = new Texture("energy_2.png");
        energyAnimation[2] = new Texture("energy_3.png");
        energyAnimation[3] = new Texture("energy_4.png");
        energyAnimation[4] = new Texture("energy_5.png");
        energyAnimation[5] = new Texture("energy_6.png");
        energyAnimation[6] = new Texture("energy_7.png");

        //TODO Inicializando os obstáculos
		bottomMountain = new Texture("bottom-mountain.png");
		topMountain = new Texture("top-mountain.png");


		//TODO inicializando texto do score e gameover
        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.getData().setScale(2);

        stateMessage = new BitmapFont(Gdx.files.internal("font.fnt"));
        stateMessage.getData().setScale(2);

        temportalModMessage = new BitmapFont(Gdx.files.internal("font.fnt"));
        temportalModMessage.getData().setScale(1);




		//TODO Inicializando a imagem de fundo
		backGround = new Texture("1.png");

		//TODO Dimensões iniciais dos componentes
        displayHeight = VIRTUAL_HEIGHT;
        displayWidth = VIRTUAL_WIDTH;

        dragonStartHorizontalPosition = displayWidth / 2;
        dragonStartVerticalPosition =  displayHeight / 2;
        dragonWidth = dragonAnimation[0].getWidth();
        dragonHeight = dragonAnimation[0].getHeight();


        mountainVariableHorizontalPosition = displayWidth;
        energyVariableHorizontalPosition = displayWidth;

        //TODO Inicializando área de colisão dos componentes
        dragonCollisionArea = new Rectangle();
        topMountainCollisionArea = new Rectangle();
        bottomMountainCollisionArea = new Rectangle();
        timeEnergyArea = new Circle();

	}

	@Override
	public void render () {

        deltaTime = Gdx.graphics.getDeltaTime();
        energyVariableHorizontalPosition -= deltaTime * 300;
        gamePerspective = true;

        if(screnPerspective){
            super.render();
        }else if (gamePerspective) {
            if (gameState.equals(GameState.PAUSED)) {
                drawFramePaused();
                dragonStartHorizontalPosition = displayWidth / 2;
                dragonStartVerticalPosition = displayHeight / 2;
                mountainVariableHorizontalPosition = displayWidth;
                energyVariableHorizontalPosition = displayWidth + (int) timeEnergyArea.area();
                score = 0;

                if (Gdx.input.justTouched()) {
                    gameState = GameState.STARTED;
                }
            } else if (gameState.equals(GameState.STARTED)) {

                mountainVariableHorizontalPosition -= deltaTime * normalTime;

                drawFrameStarted();
                chkTreePosition();
                chkEnergyPosition();
                chkTemporalMod();
                isCollision();
                energyCollision();
                gameScore();
                dragonFall();
                if (Gdx.input.isTouched()) {
                    dragonFly();
                }

            } else if (gameState.equals(GameState.GAMEOVER)) {
                dragonFall();
                drawFrameStarted();
                if (dragonStartVerticalPosition <= 0) {
                    gameState = GameState.RESTART;

                }
            } else if (gameState.equals(GameState.RESTART)) {
                if (Gdx.input.isTouched()) {
                    gameState = GameState.PAUSED;
                }
            } else if (gameState.equals(GameState.TEMPORALFORM)) {

                mountainVariableHorizontalPosition -= deltaTime * temporalMod;

                drawFrameStarted();
                chkTreePosition();
                chkEnergyPosition();
                chkTemporalMod();
                isCollision();
                energyCollision();
                gameScore();
                dragonFall();

                if (!temporalTimeFlag) {
                    timeSeconds += Gdx.graphics.getRawDeltaTime();
                    if (timeSeconds > period) {
                        timeSeconds = 0;
                        gameState = GameState.STARTED;
                        temporalTimeFlag = true;
                    }
                }

                if (Gdx.input.isTouched()) {
                    dragonFly();
                }

            }

            //TODO Configurar dados de projeção da câmera
            batch.setProjectionMatrix(camera.combined);

            //TODO Renderiza os componentes do jogo
            batch.begin();

            batch.draw(backGround, 0, 0, displayWidth, displayHeight);
            batch.draw(topMountain, mountainVariableHorizontalPosition, (displayHeight / 2) + (spaceMountain / 2) + (randomMountainHeight));
            batch.draw(bottomMountain, mountainVariableHorizontalPosition, ((displayHeight / 2) - bottomMountain.getHeight()) - (spaceMountain / 2) + (randomMountainHeight));
            batch.draw(dragonAnimation[(int) dragonFrameVariation], dragonStartHorizontalPosition - (dragonWidth / 2), dragonStartVerticalPosition);
            batch.draw(energyAnimation[(int) drawFrameEnergy()], energyVariableHorizontalPosition - timeEnergyArea.radius, (((displayHeight - (topMountainCollisionArea.getHeight() - bottomMountainCollisionArea.getHeight()))) / 2) - timeEnergyArea.radius);
            font.draw(batch, "Score: " + String.valueOf(score), displayWidth - displayWidth + 10, displayHeight - 20);

            if (gameState.equals(GameState.GAMEOVER)) {
                stateMessage.draw(batch, "GAME OVER", 0, displayHeight / 2, displayWidth, Align.center, true);
            } else if (gameState.equals(GameState.TEMPORALFORM)) {
                temportalModMessage.draw(batch, "Slow Form: " + String.valueOf((int) timeSeconds), displayWidth - displayWidth + 10, displayHeight - 75);
            } else if (gameState.equals(GameState.RESTART)) {
                stateMessage.draw(batch, "Click\nto\nRestart!", 0, displayHeight / 2, displayWidth, Align.center, true);
            }

            batch.end();

            //TODO Inicializando as formas para as colisões
            dragonCollisionArea.set(dragonStartHorizontalPosition - (dragonWidth / 2), dragonStartVerticalPosition, dragonWidth, dragonHeight);
            topMountainCollisionArea.set(mountainVariableHorizontalPosition, (displayHeight / 2) + (spaceMountain / 2) + (randomMountainHeight), topMountain.getWidth(), topMountain.getHeight());
            bottomMountainCollisionArea.set(mountainVariableHorizontalPosition, (displayHeight / 2) - (bottomMountain.getHeight()) - (spaceMountain / 2) + (randomMountainHeight), bottomMountain.getWidth(), bottomMountain.getHeight());
            timeEnergyArea.set(energyVariableHorizontalPosition, ((displayHeight - (topMountainCollisionArea.getHeight() - bottomMountainCollisionArea.getHeight()))) / 2, 30);


            //TODO Desenha as formas de colisão
            //shape.begin( ShapeRenderer.ShapeType.Filled );
            //shape.rect(dragonCollisionArea.x, dragonCollisionArea.y, dragonCollisionArea.width, dragonCollisionArea.height);
            //shape.rect(topMountainCollisionArea.x, topMountainCollisionArea.y, topMountainCollisionArea.width, topMountainCollisionArea.height);
            //shape.rect(bottomMountainCollisionArea.x, bottomMountainCollisionArea.y, bottomMountainCollisionArea.width, bottomMountainCollisionArea.height);
            //shape.circle(energyVariableHorizontalPosition, ((displayHeight - (topMountainCollisionArea.getHeight() - bottomMountainCollisionArea.getHeight()))) / 2, 30);
            //shape.end();

        }
	}

	//TODO Renderiza o bater das asas do Dragão antes do jogo começar
	private float drawFramePaused(){
	    // TODO Delta time calcula o tempo da diferença entre as execuções do método render() com a finalidade de suavezar o bater das asas
        dragonFrameVariation += deltaTime * agilityOfWings;

        if(dragonFrameVariation > 3) {
            dragonFrameVariation = 0;
        }
       return dragonFrameVariation;
    }

    //TODO Renderiza o bater das asas do Dragão ao iniciar o jogo
    private float drawFrameStarted(){
        if(Gdx.input.isTouched()){
            dragonFrameVariation = 4;

        }
        if(dragonDropSpeed > 0){
            dragonFrameVariation = 5;
        }
        return dragonFrameVariation;
    }
    //TODO Renderiza a animação da energia do tempo
    private float drawFrameEnergy(){
        energyFrameVariation += deltaTime * 8;
        if(energyFrameVariation > 6){
            energyFrameVariation = 0;
        }

        return energyFrameVariation;
    }

	//TODO Decrementa a posição do dragão
    private void dragonFall(){
	    if(dragonStartVerticalPosition > 0 || dragonDropSpeed < 0 )
	        dragonStartVerticalPosition = dragonStartVerticalPosition - (++dragonDropSpeed);
	    if(dragonStartVerticalPosition <= 0){
	        gameState = GameState.GAMEOVER;
        }
    }

	//TODO Incrementa a posição do dragão quando a tela é tocada
    private void dragonFly(){
        dragonDropSpeed = flyAgility;
    }

	//TODO Verifica se a montanha saiu inteiramente da tela e Verifica a velocidade com que elas se locomovem
    private void chkTreePosition(){
        if(mountainVariableHorizontalPosition < -topMountain.getWidth()){
            mountainVariableHorizontalPosition = displayWidth;
            randomMountainHeight = randomNumber.nextInt((200) - 100);
            scoreFlag = false;
        }

	}
    //TODO Verifica a posição da energia do tempo
	private void chkEnergyPosition(){
        if(energyVariableHorizontalPosition < - timeEnergyArea.area()){
            energyVariableHorizontalPosition = displayWidth + (int) timeEnergyArea.area();
        }

    }
    //TODO Modifica a velocidade do ambiente caso esteja na forma temporal
    private void chkTemporalMod(){
      if (gameState.equals(GameState.STARTED)){
            normalTime += score / 10;
        }
    }

    //TODO Cálcula o score do jogo
    private void gameScore(){
        if( mountainVariableHorizontalPosition < 100 ){
            if( !scoreFlag ){
                scoreFlag = true;
                score++;
            }
        }
    }
    //TODO Checa a colisão do dragão em relação as montanhas
    private void isCollision(){
        if(Intersector.overlaps(topMountainCollisionArea, dragonCollisionArea) || Intersector.overlaps(bottomMountainCollisionArea, dragonCollisionArea)) {
            gameState = GameState.GAMEOVER;

        }
    }
    //TODO Checa a colisão do dragão em relação a energia do tempo
    private void energyCollision(){
        if(Intersector.overlaps(timeEnergyArea, dragonCollisionArea)){
            gameState = GameState.TEMPORALFORM;
            temporalTimeFlag = false;
        }
    }
}
