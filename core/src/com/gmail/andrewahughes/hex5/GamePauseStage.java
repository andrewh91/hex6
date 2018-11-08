package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Andrew Hughes on 24/09/2018.
 */

public class GamePauseStage extends Stage {

    private boolean visible = false;
    private boolean pause = false;
    public StageInterface stageInterface;
    public Image pauseImg1;
    public float black=1.0f;
    Skin skin;
    private TextureAtlas atlas;


    private TextField zoomModeValue;
    private Label zoomMode;
    private TextField posXValue;
    private Label posX;
    private TextField posYValue;
    private Label posY;
    private TextField widthValue;
    private Label width;
    private TextField heightValue;
    private Label height;
    private TextField rowValue;
    private Label row;
    private TextField columnValue;
    private Label column;
    private TextField portrait1Landscape2Value;
    private Label portrait1Landscape2;


    public GamePauseStage(Viewport viewport, Texture texture,final StageInterface stageInterface) {
        super( viewport );

        this.stageInterface =stageInterface;

        Table table = new Table();
        table.setFillParent(true);
        table.left();

        skin = new Skin();
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));


        final Image pauseImg = new Image(texture);
        this.pauseImg1 =pauseImg;
        this.pauseImg1.setVisible(false);//start off invisible



        table.add(pauseImg);
        table.center();
        table.row().pad(40);
        table.row();
        prepareUI();

        addActor(table);
    }
    @Override
    public void draw() {
        act(Gdx.graphics.getDeltaTime());

        if (visible) {

            Gdx.gl.glClearColor(0.9f*black, 0.0f*black, 0.4f*black, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


            super.draw();
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public void setPause(boolean pause) {
        this.pause = pause;
    }


    public void prepareUI()
    {

        zoomModeValue= new TextField("", skin);
        zoomMode= new Label("quick1Zoom2:", skin);
        posXValue= new TextField("", skin);
        posX= new Label("posX:", skin);
        posYValue= new TextField("", skin);
        posY= new Label("posY:", skin);
        widthValue= new TextField("", skin);
        width= new Label("width:", skin);
        heightValue= new TextField("", skin);
        height= new Label("height:", skin);
        rowValue= new TextField("", skin);
        row= new Label("row:", skin);
        columnValue= new TextField("", skin);
        column= new Label("column:", skin);
        portrait1Landscape2Value= new TextField("", skin);
        portrait1Landscape2= new Label("portrait1Landscape2:", skin);
        TextButton button3 = new TextButton("return to main menu", skin);
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                    setPause(false);
                    black=1.0f;
                    setVisible(false);
                    GamePauseStage.this.stageInterface.goToMainStage();

            }
        });

        TextButton button2 = new TextButton("cancel changes", skin);
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                GamePauseStage.this.stageInterface.updateOptionsGoToGameStage(0,0,0,0,0,0,0,0);

            }
        });
        TextButton button1 = new TextButton("Save changes", skin);
        button1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int posX,posY,rows,columns,width,height,portrait1Landscape2,zoom;
                try {
                    zoom= Integer.valueOf(zoomModeValue.getText());
                } catch (NumberFormatException nfe) {
                    zoom= 0;
                }
                try {
                    rows= Integer.valueOf(rowValue.getText());
                } catch (NumberFormatException nfe) {
                    rows= 0;
                }
                try {
                    columns= Integer.valueOf(columnValue.getText());
                } catch (NumberFormatException nfe) {
                    columns= 0;
                }
                try {
                    portrait1Landscape2= Integer.valueOf(portrait1Landscape2Value.getText());
                } catch (NumberFormatException nfe) {
                    portrait1Landscape2= 0;
                }
                try {
                    width= Integer.valueOf(widthValue.getText());
                } catch (NumberFormatException nfe) {
                    width= 0;
                }
                try {
                    height= Integer.valueOf(heightValue.getText());
                } catch (NumberFormatException nfe) {
                    height= 0;
                }
                try {
                    posX= Integer.valueOf(posXValue.getText());
                } catch (NumberFormatException nfe) {
                    posX= 0;
                }
                try {
                    posY= Integer.valueOf(posYValue.getText());
                } catch (NumberFormatException nfe) {
                    posY= 0;
                }
                GamePauseStage.this.stageInterface.updateOptionsGoToGameStage(rows,columns,portrait1Landscape2,posX,posY,width,height,zoom);

            }
        });


        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        table.add(zoomModeValue);
        table.add(zoomMode);
        table.row();
        table.add(posXValue);
        table.add(posX);
        table.row();
        table.add(posYValue);
        table.add(posY);
        table.row();
        table.add(widthValue);
        table.add(width);
        table.row();
        table.add(heightValue);
        table.add(height);

        table.row();
        table.add(rowValue);
        table.add(row);
        table.row();
        table.add(columnValue);
        table.add(column);
        table.row();
        table.add(portrait1Landscape2Value);
        table.add(portrait1Landscape2);
        table.row();
        table.add(button1);

        table.add(button2);
        table.add(button3);
        addActor(table);


    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){

                setPause(false);
                //pauseImg1.setVisible(false);
                setVisible(false);
                //black = 1.f;
                GamePauseStage.this.stageInterface.goToGameStage();




        }
        return false;
    }
}
