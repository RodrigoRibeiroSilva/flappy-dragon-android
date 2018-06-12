package com.android.flappydragon.scenes;

import com.android.flappydragon.FlappyDragon;
import com.android.flappydragon.GameState;
import com.android.flappydragon.User;
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

import com.badlogic.gdx.net.HttpParametersUtils;
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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;


public class Login implements Screen{

    final FlappyDragon game;
    private SpriteBatch batch;

    private OrthographicCamera camera;
    private BitmapFont errorMessage;
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
    private boolean invalidPassword;
    private float timeSeconds = 0f;
    private float period = 2f;
    private Toast.ToastFactory toastFactory;
    private Toast toast;
    private long startTime = 0;

    public Login(final FlappyDragon game){
        this.batch = new SpriteBatch();
        this.game = game;
        this.camera = new OrthographicCamera();
        this.camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2,0);
        this.viewPort = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
        this.font = new BitmapFont(Gdx.files.internal("font.fnt"));
        this.font.getData().setScale(2);
        this.displayHeight = VIRTUAL_HEIGHT;
        this.displayWidth = VIRTUAL_WIDTH;
        this.errorMessage = new BitmapFont(Gdx.files.internal("font.fnt"));
        this.errorMessage.getData().setScale(2);
        this.invalidPassword = false;
        this.toastFactory= new Toast.ToastFactory.Builder().font(font).build();
        this.toast = toastFactory.create("Incorrect Password", Toast.Length.SHORT);

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
        final TextField fieldUser = new TextField("", skin);
        final TextField fieldSenha = new TextField("", skin);
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
                final User userLogin = new User(fieldUser.getText(), fieldSenha.getText(), 0.0);

                HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
                Net.HttpRequest request = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("https://api-android-node.herokuapp.com/User/" + userLogin.getNickName()).build();
                Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

                    public void handleHttpResponse(Net.HttpResponse httpResponse) {

                        try {
                            //TODO RESULTADO DO GET DA API
                            String var = httpResponse.getResultAsString();


                            Gdx.app.log("Resultado",var);
                            //TODO SE O RESULTADO FOR VAZIO CHAMA O MÉTODO POST E CRIA A CONTA
                            if(var.equals("[]")){
                                Map parameters = new HashMap();
                                parameters.put("username", userLogin.getNickName());
                                parameters.put("password" ,  userLogin.getPassword());


                                Net.HttpRequest httpPost = new Net.HttpRequest(Net.HttpMethods.POST);
                                httpPost.setUrl("https://api-android-node.herokuapp.com/User/");
                                httpPost.setContent(HttpParametersUtils.convertHttpParameters(parameters));

                                Gdx.net.sendHttpRequest(httpPost, new Net.HttpResponseListener() {
                                    @Override
                                    public void handleHttpResponse(Net.HttpResponse httpResponse) {
                                        game.screnPerspective = false;
                                        game.gamePerspective = true;
                                        game.render();
                                        dispose();
                                    }

                                    @Override
                                    public void failed(Throwable t) {
                                        Gdx.app.log("Erro", t.getMessage());
                                    }

                                    @Override
                                    public void cancelled() {
                                        Gdx.app.log("Cancelado", "Cancelou");
                                    }
                                });
                            }
                            //TODO PEGA OS RESULTADOS DA REQUISIÇÃO E PASSA PARA UMA CLASSE
                            else{

                                //TODO PASSANDO O RESULTADO DA REQUISIÇÃO DA API PARA PODER PEGAR OS VALORES
                                JsonValue user = new JsonReader().parse(var);

                                //TODO CRIANDO UM USUÁRIO E PASSANDO OS VALORES DA REQUISIÇÃO PARA OS ATRIBUTOS DA CLASSE
                                User userJson = new User("", "", 0.0 );
                                userJson.setScore(user.child.child.asDouble());
                                userJson.setNickName(user.child.child.next.next.asString());
                                userJson.setPassword(user.child.child.next.next.next.asString());

                                //TODO SE O PASSWORD DA REQUISIÇÃO FOR IGUAL AO DIGITADO ELE CHAMA O JOGO
                                if(userLogin.getPassword().equals(userJson.getPassword())){
                                    userLogin.setScore(userJson.getScore());
                                    Gdx.app.log("user", userLogin.getNickName());
                                    Gdx.app.log("Password", userLogin.getPassword());
                                    Gdx.app.log("Score", String.valueOf(userLogin.getScore()));
                                    invalidPassword = false;
                                    game.screnPerspective = false;
                                    game.gamePerspective = true;
                                    game.logedUser.setNickName(userJson.getNickName());
                                    game.logedUser.setPassword(userJson.getPassword());
                                    game.logedUser.setScore(userJson.getScore());
                                    game.render();
                                    dispose();
                                }
                                else{
                                    //TODO Mostrar mensagem de password Errado
                                    Gdx.app.log("PasswordErrado", "Password Errado");
                                    invalidPassword = true;

                                }

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

            };
        });


    }
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

       if (invalidPassword) {
            invalidPassword = false;
            toast.render((float) 0.0001);

        }
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
