package com.android.flappydragon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

import java.util.Random;


public class FlappyDragon extends ApplicationAdapter {

	//TODO Componentes do jogo
	private SpriteBatch batch;
	private ShapeRenderer shape;

	private Texture[] dragonAnimation;
	private Texture[] smokeAnimation;

	private Texture backGround;
	private Texture topTreeHigh;
	private Texture topTree;
	private Texture bottomTreeHigh;
	private Texture bottomTree;
	private BitmapFont font;
	private BitmapFont stateMessage;
	//Todo figuras para colisões
	private Rectangle dragonCollisionArea;
	private Rectangle topTreeCollisionArea;
	private Rectangle bottomTreeCollisionArea;
	private Circle topTreeCircleArea;
	private Polygon retanglePolygon;
	private Polygon ellipsePolygon;


	//TODO Configurações do jogo
	private float deltaTime;
    private int displayHeight;
    private int displayWidth;
    private Random randomNumber = new Random();
    private int score = 0;
    private  boolean scoreFlag = false;


    //TODO Configurações do Dragão
	private float dragonDropSpeed = 0;
    private float dragonStartVerticalPosition;
    private float dragonStartHorizontalPosition;
    private float dragonFrameVariation = 0;
    private int dragonWidth;
    private int dragonHeight;
    private final int agilityOfWings= 8;
    private final int flyAgility = -8;

    //TODO Configurações da Fumaça
    private float frameSmokeVariation = 0;
    private Random randomSmokeFrame = new Random();
    private int randomVerticalSmokePosition = 0;

    //TODO Configurações das árvores
    private int treeVariableHorizontalPosition;
    private int topTreeRandomVerticalPosition;
    private int treeHeight;
    private int treeWidth;
	private int spaceTrees = 200;
	private float randomTreeHeight;

	//TODO gameState = PAUSED -> Jogo não iniciado // gameState = STARTED -> Jogo Iniciado // gameState = GHOSTFORM -> Dragão na forma fantasma
	private GameState gameState = GameState.PAUSED;



	@Override
	public void create () {

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

        //TODO Inicializando os frames da Fumaça


        //TODO Inicializando os obstáculos
		bottomTree = new Texture("bottom-mountain.png");
		topTree = new Texture("top-mountain.png");


		//TODO inicializando texto do score e gameover
        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.getData().setScale(2);

        stateMessage = new BitmapFont(Gdx.files.internal("font.fnt"));
        stateMessage.getData().setScale(2);




		//TODO Inicializando a imagem de fundo
		backGround = new Texture("1.png");

		//TODO Dimensões iniciais dos componentes
        displayHeight = Gdx.graphics.getHeight();
        displayWidth = Gdx.graphics.getWidth();

        dragonStartHorizontalPosition = displayWidth / 2;
        dragonStartVerticalPosition =  displayHeight / 2;
        dragonWidth = dragonAnimation[0].getWidth();
        dragonHeight = dragonAnimation[0].getHeight();


        treeVariableHorizontalPosition = displayWidth;

        //TODO Inicializando área de colisão dos componentes
        dragonCollisionArea = new Rectangle();
        topTreeCollisionArea = new Rectangle();
        bottomTreeCollisionArea = new Rectangle();
	}

	@Override
	public void render () {

        deltaTime = Gdx.graphics.getDeltaTime();

		if(gameState.equals(GameState.PAUSED)){
		    drawFramePaused();
            dragonStartHorizontalPosition = displayWidth / 2;
            dragonStartVerticalPosition =  displayHeight / 2;
            treeVariableHorizontalPosition = displayWidth;
		    score = 0;

			if(Gdx.input.justTouched()){
				gameState = GameState.STARTED;
			}
		}else if (gameState.equals(GameState.STARTED)){
		    drawFrameStarted();
            drawSmokeFrame();
			chkTreePosition();
            isCollision();
			gameScore();
			dragonFall();
			if (Gdx.input.isTouched()) {
				dragonFly();
			}

		}else if (gameState.equals(GameState.GAMEOVER)){
            dragonFall();
            drawFrameStarted();
            if( dragonStartVerticalPosition <= 0 ){
                gameState = GameState.RESTART;

            }
        }else if (gameState.equals(GameState.RESTART)){
            if (Gdx.input.isTouched()) {
                gameState = GameState.PAUSED;
            }
        }

        //TODO Renderiza os componentes do jogo
        batch.begin();

            batch.draw(backGround, 0, 0, displayWidth, displayHeight);
            batch.draw(topTree, treeVariableHorizontalPosition, (displayHeight / 2) + (spaceTrees / 2) + (randomTreeHeight));
            batch.draw(bottomTree, treeVariableHorizontalPosition, ((displayHeight / 2) - bottomTree.getHeight()) - (spaceTrees / 2) + (randomTreeHeight));
            batch.draw(dragonAnimation[(int) dragonFrameVariation], dragonStartHorizontalPosition - (dragonWidth / 2), dragonStartVerticalPosition);
            //batch.draw(smokeAnimation[(int) frameSmokeVariation], treeVariableHorizontalPosition + 20, displayHeight/ 2 + 30);
            font.draw(batch, "Score: " + String.valueOf(score), displayWidth - displayWidth + 10, displayHeight - 20);

            if(gameState.equals(GameState.GAMEOVER)){
                stateMessage.draw(batch, "GAME OVER" ,0, displayHeight / 2 , displayWidth, Align.center, true);
            }
            else if( gameState.equals(GameState.RESTART)){
                //stateMessage.draw(batch, "Click to Restart!" ,(displayWidth / 2 ) - stateMessage.getRegion().getRegionWidth(), displayHeight / 2);
                stateMessage.draw(batch, "Click\nto\nRestart!", 0, displayHeight / 2 , displayWidth, Align.center, true);
            }

        batch.end();

        //TODO Inicializando as formas para as colisões
        dragonCollisionArea.set( dragonStartHorizontalPosition - (dragonWidth / 2), dragonStartVerticalPosition, dragonWidth, dragonHeight);
        topTreeCollisionArea.set(treeVariableHorizontalPosition, (displayHeight / 2) + (spaceTrees / 2) + (randomTreeHeight), topTree.getWidth(), topTree.getHeight() );
        bottomTreeCollisionArea.set(treeVariableHorizontalPosition, (displayHeight / 2) - (bottomTree.getHeight()) - (spaceTrees / 2) + (randomTreeHeight) ,bottomTree.getWidth(), bottomTree.getHeight() );



        //TODO Desenha as formas de colisão

        /*
        shape.begin( ShapeRenderer.ShapeType.Filled );
            shape.rect(dragonCollisionArea.x, dragonCollisionArea.y, dragonCollisionArea.width, dragonCollisionArea.height);
            shape.rect(topTreeCollisionArea.x, topTreeCollisionArea.y, topTreeCollisionArea.width, topTreeCollisionArea.height);
            shape.rect(bottomTreeCollisionArea.x, bottomTreeCollisionArea.y, bottomTreeCollisionArea.width, bottomTreeCollisionArea.height);
        shape.end();
        */

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
        }if(dragonDropSpeed > 0){
        dragonFrameVariation = 5;
        }
        return dragonFrameVariation;
    }

    private float drawSmokeFrame(){
        frameSmokeVariation += deltaTime * 5;
        if(frameSmokeVariation > 3){
            frameSmokeVariation = 0;
        }
        return frameSmokeVariation;
    }

	//TODO Decrementa a posição do dragão
    private void dragonFall(){
	    if(dragonStartVerticalPosition > 0 || dragonDropSpeed < 0 )
	        dragonStartVerticalPosition = dragonStartVerticalPosition - (++dragonDropSpeed);
    }

	//TODO Incrementa a posição do dragão quando a tela é tocada
    private void dragonFly(){
        dragonDropSpeed = flyAgility;
    }

	//TODO Verifica se o cano saiu inteiramente da tela
    private void chkTreePosition(){
		treeVariableHorizontalPosition -= deltaTime * 200 + score;
		if(treeVariableHorizontalPosition < -topTree.getWidth()){
			treeVariableHorizontalPosition = displayWidth;
			randomTreeHeight = randomNumber.nextInt((200) - 100);
            scoreFlag = false;
		}
	}
    //TODO Cálcula o score do jogo
    private void gameScore(){
        if( treeVariableHorizontalPosition < 100 ){
            if( !scoreFlag ){
                scoreFlag = true;
                score++;
            }
        }
    }

    private void isCollision(){
        if(Intersector.overlaps(topTreeCollisionArea, dragonCollisionArea) || Intersector.overlaps(bottomTreeCollisionArea, dragonCollisionArea)) {
            gameState = GameState.GAMEOVER;

        }
    }

    /*
    private void setRectanglePolygon(){
        float w = dragonCollisionArea.x;
        float h = dragonCollisionArea.y;

        float[] vertices = {0,0, w,0, w,h, 0,h};
        retanglePolygon = new Polygon(vertices);
    }

    private void setEllipsePolygonTop(int numSides){
        float w = topTreeCollisionArea.width;
        float h = topTreeCollisionArea.height;

        float[] vertices = new float[2 * numSides];

        for(int i = 0; i < numSides; i++){

            float angle =  i * 6.28f / numSides;
            //TODO coordenada x da elipse
            vertices[2*1] = w/2 * MathUtils.cos(angle) + w/2;
            //TODO coordenada y da elipse
            vertices[2*1+1] = h/2 * MathUtils.sin(angle) + h/2;
        }

        ellipsePolygon = new Polygon(vertices);
    }

    private Polygon getEllipsePolygonTop(){
        ellipsePolygon.setPosition( treeVariableHorizontalPosition,(displayHeight / 2) + (spaceTrees / 2) + (randomTreeHeight));
        ellipsePolygon.setOrigin( topTreeCollisionArea.width / 2 , topTreeCollisionArea.width / 2 );
        ellipsePolygon.setRotation( ellipsePolygon.getRotation());
        ellipsePolygon.setScale( ellipsePolygon.getScaleX() , ellipsePolygon.getScaleY());

        return ellipsePolygon;
    }
    */


}
