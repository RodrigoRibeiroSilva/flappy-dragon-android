package com.android.flappydragon.scenes;

import com.android.flappydragon.FlappyDragon;
import com.android.flappydragon.User;
import com.android.flappydragon.webservice.Request;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class MainMenu implements Screen{

    final FlappyDragon game;
    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewPort;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;
    private Texture imageTitle = new Texture("window_panel_title.png");
    public BitmapFont font;
    public float displayHeight;
    public float displayWidth;
    private Stage stage;
    private Table table;
    private Table tableBackground;
    private Table tableTitleBackground;
    private Skin skin;

    public MainMenu(final FlappyDragon game){
        this.batch = new SpriteBatch();
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2,0);
        this.viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        this.font = new BitmapFont(Gdx.files.internal("font.fnt"));
        this.font.getData().setScale(2);
        this.displayHeight = VIRTUAL_HEIGHT;
        this.displayWidth = VIRTUAL_WIDTH;
    }

    @Override
    public void show() {

        stage = new Stage();
        table = new Table();
        tableBackground = new Table();
        tableTitleBackground = new Table();

        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2,0);
        viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        stage.setViewport(viewPort);

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        Label user = new Label("User", skin);
        Label senha = new Label("Password", skin);
        Label gameTitle = new Label("FLAPPY DRAGON", skin);
        TextField fieldUser = new TextField("", skin);
        TextField fieldSenha = new TextField("", skin);
        TextButton submit = new TextButton("Submit" , skin);
        user.setFontScale(2);
        senha.setFontScale(2);
        gameTitle.setFontScale(3);
        submit.setBackground(new NinePatchDrawable(getNinePatch(("button_text.png"))));

        table.add(user).fill().height(80);
        table.add(fieldUser).fill().height(80);
        table.row();
        table.add(senha).fill().height(80);
        table.add(fieldSenha).fill().height(80).width(300);
        table.row();
        table.add(submit).colspan(2).fill().height(80);

        table.setBackground(new NinePatchDrawable(getNinePatch(("window_panel.png"))));
        tableBackground.setBackground(new NinePatchDrawable(getNinePatch(("1.png"))));
        tableTitleBackground.setBackground(new NinePatchDrawable(getNinePatch(("window_panel_title.png"))));

        tableTitleBackground.add(gameTitle);
        tableTitleBackground.center();
        tableBackground.setFillParent(true);
        stage.addActor(tableBackground);
        stage.addActor(table);
        stage.addActor(tableTitleBackground);
        Gdx.input.setInputProcessor(stage);

        submit.addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                User userLogin = new User("Arc", "123", 0);

                HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
                Net.HttpRequest request = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("https://api-android-node.herokuapp.com/User").build();
                Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

                    public void handleHttpResponse(Net.HttpResponse httpResponse) {

                        try {

                            int statusCode = httpResponse.getStatus().getStatusCode();
                            if(statusCode == 200) {

                                Gdx.app.log("Sucesso: " , "Deu bom");
                                Gdx.app.log("MSG", httpResponse.getResultAsString());
                                return;
                            }else{
                                String responseJson = httpResponse.getResultAsString();
                                Gdx.app.log("MSG", httpResponse.getResultAsString());
                            }

                        }
                        catch(Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                    public void failed(Throwable t) {
                        System.out.println("Request Failed Completely");
                        Gdx.app.log("Erro1: " , "Request Failed Completely");
                    }

                    @Override
                    public void cancelled() {
                        System.out.println("request cancelled");
                        Gdx.app.log("Erro2: " , "request cancelled");
                    }

                });

               /* Request request = new Request();
                request.sendRequest( userLogin ,"POST");
*/
               /* game.screnPerspective = false;
                game.gamePerspective = true;
                game.render();
                dispose();*/
            };
        });


    }
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

    }

    private NinePatch getNinePatch(String fname) {
        final Texture t = new Texture(Gdx.files.internal(fname));
        return new NinePatch( new TextureRegion(t, 1, 1 , t.getWidth() - 2, t.getHeight() - 2), 10, 10, 10, 10);
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width,height);
        table.setSize(stage.getWidth() - 150,stage.getHeight() - 150);
        table.setPosition(stage.getWidth() / 2, stage.getHeight() / 2 , 1);
        tableTitleBackground.setSize(table.getWidth(),imageTitle.getHeight() + 40);
        tableTitleBackground.setPosition(stage.getWidth() / 2, stage.getHeight() - 150, 1);

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
        this.stage.dispose();
    }

}
