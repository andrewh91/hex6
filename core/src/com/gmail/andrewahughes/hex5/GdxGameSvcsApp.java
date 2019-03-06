package com.gmail.andrewahughes.hex5;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.MockGameServiceClient;
import de.golfgl.gdxgamesvcs.achievement.IAchievement;
import de.golfgl.gdxgamesvcs.achievement.IFetchAchievementsResponseListener;
import de.golfgl.gdxgamesvcs.gamestate.IFetchGameStatesListResponseListener;
import de.golfgl.gdxgamesvcs.gamestate.ILoadGameStateResponseListener;
import de.golfgl.gdxgamesvcs.gamestate.ISaveGameStateResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.IFetchLeaderBoardEntriesResponseListener;
import de.golfgl.gdxgamesvcs.leaderboard.ILeaderBoardEntry;

public class GdxGameSvcsApp extends ApplicationAdapter implements IGameServiceListener,StageInterface {
    public static final String LEADERBOARD1 = "BOARD1";
    public static final String ACHIEVEMENT1 = "ACH1";
    public static final String EVENT1 = "EVENT1";
    public static final String REPOLINK = "https://github.com/MrStahlfelge/gdx-gamesvcs";
    public static final String FILE_ID = "cloud";
Platform platform;
    GameStage gameStage;
    GamePauseStage gamePauseStage;
    GameOverStage gameOverStage;
    Texture badlogic;
    private boolean visible = true;
    public IGameServiceClient gsClient;
    Skin skin;
    Stage mainStage;//main menu
    SpriteBatch batch;
    Label gsStatus;
    Label gsUsername;
    private TextButton signInButton;
    private TextureAtlas atlas;
    private TextField scoreFillin;
    private TextField cloudData;
    OrthographicCamera cam;
    StretchViewport stretchViewport;
    public int vpWidth,vpHeight,vpShort,vpLong;
    int portrait=1;

   public GdxGameSvcsApp(Platform platform)
   {
       this.platform=platform;
   }
    @Override
    public void create() {
       cam=new OrthographicCamera();
       vpLong=1280;// the long edge of the screen
       vpShort=720;
vpWidth=vpLong;
vpHeight=vpShort;
        stretchViewport = new StretchViewport(vpWidth,vpHeight);
        badlogic = new Texture("badlogic.jpg");
        gameStage = new GameStage(stretchViewport,badlogic,this,portrait);
        gamePauseStage = new GamePauseStage(stretchViewport,badlogic,this);
        gameOverStage = new GameOverStage(stretchViewport,badlogic,this);
        //Gdx.input.setInputProcessor(gameStage);

        mainStage = new Stage(stretchViewport);
        Gdx.input.setInputProcessor(mainStage);
        Gdx.input.setCatchBackKey(true);
        setPortrait();

        prepareSkin();

        if (gsClient == null)
            gsClient = new MockGameServiceClient(1) {
                @Override
                protected Array<ILeaderBoardEntry> getLeaderboardEntries() {
                    return null;
                }

                @Override
                protected Array<String> getGameStates() {
                    return null;
                }

                @Override
                protected byte[] getGameState() {
                    return new byte[0];
                }

                @Override
                protected Array<IAchievement> getAchievements() {
                    return null;
                }

                @Override
                protected String getPlayerName() {
                    return null;
                }
            };

        gsClient.setListener(this);

        prepareUI();

        gsClient.resumeSession();

        // needed in case the connection is pending
        refreshStatusLabel();
    }

    private void prepareUI() {
        gsStatus = new Label("", skin);
        gsUsername = new Label("", skin);
        scoreFillin = new TextField("100", skin);
        cloudData = new TextField("", skin);

        Label repoLink = new Label(REPOLINK, skin);
        repoLink.setColor(.3f, .3f, 1f, 1f);
        repoLink.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI(REPOLINK);
            }
        });

        signInButton = new TextButton("", skin);
        signInButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                gsSignInOrOut();
            }
        });

        TextButton showLeaderBoards = new TextButton("Show", skin);
        showLeaderBoards.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gsClient.showLeaderboards(null);
                } catch (GameServiceException e) {
                    e.printStackTrace();
                }
            }
        });
        showLeaderBoards.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.ShowAllLeaderboardsUI));

        TextButton fetchLeaderboards = new TextButton("Fetch", skin);
        fetchLeaderboards.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                fetchLeaderboard(false);
            }
        });
        fetchLeaderboards.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.FetchLeaderBoardEntries));

        TextButton submitToLeaderboard = new TextButton("Submit", skin);
        submitToLeaderboard.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int score;
                try {
                    score = Integer.valueOf(scoreFillin.getText());
                } catch (NumberFormatException nfe) {
                    score = 0;
                }

                if (score > 0)
                    gsClient.submitToLeaderboard(LEADERBOARD1, score, gsClient.getGameServiceId());
            }
        });

        TextButton showAchievements = new TextButton("Show", skin);
        showAchievements.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.ShowAchievementsUI));
        showAchievements.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                try {
                    gsClient.showAchievements();
                } catch (GameServiceException e) {
                    e.printStackTrace();
                }
            }
        });

        TextButton fetchAchievements = new TextButton("Fetch", skin);
        fetchAchievements.setVisible(
                gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.FetchAchievements));
        fetchAchievements.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final MyDialog dialog = new MyDialog("Achievements");
                boolean fetchingNow = gsClient.fetchAchievements(new IFetchAchievementsResponseListener() {
                    @Override
                    public void onFetchAchievementsResponse(final Array<IAchievement> achievements) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                showAchievementsList(dialog, achievements);
                            }
                        });

                    }
                });

                dialog.text(fetchingNow ? "Fetching..." : "Could not fetch");
                dialog.show(mainStage);
            }
        });

        TextButton unlockAchievement = new TextButton("unlock", skin);
        unlockAchievement.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsClient.unlockAchievement(ACHIEVEMENT1);
            }
        });

        TextButton submitEvent1Btn = new TextButton("go to new mainStage", skin);
        submitEvent1Btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //gsClient.submitEvent(EVENT1, 1);

                //reset the camera since we are starting a new game
                goToGameStageRefresh();
            }
        });

        TextButton loadFromCloud = new TextButton("load", skin);
        loadFromCloud.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsClient.loadGameState(FILE_ID, new ILoadGameStateResponseListener() {
                    @Override
                    public void gsGameStateLoaded(byte[] gameState) {
                        cloudData.setText(gameState != null ? new String(gameState) : "failed");
                    }
                });
            }
        });
        TextButton saveToCloud = new TextButton("save", skin);
        saveToCloud.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsClient.saveGameState(FILE_ID, cloudData.getText().getBytes(), 0,
                        new ISaveGameStateResponseListener() {
                            @Override
                            public void onGameStateSaved(boolean success, String errorCode) {
                                if (!success) {
                                    Dialog dialog = new MyDialog("Save");
                                    dialog.text("Failure: " + errorCode);
                                }
                            }
                        });
            }
        });
        TextButton deleteFromCloud = new TextButton("delete", skin);
        deleteFromCloud.setVisible(gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.GameStateDelete));
        deleteFromCloud.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gsClient.deleteGameState(FILE_ID, null);
            }
        });
        TextButton fetchCloudList = new TextButton("list", skin);
        fetchCloudList.setVisible(gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.FetchGameStates));
        fetchCloudList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                final MyDialog dialog = new MyDialog("Cloud saved states");
                boolean fetchingNow = gsClient.fetchGameStates(new IFetchGameStatesListResponseListener() {
                    @Override
                    public void onFetchGameStatesListResponse(final Array<String> gameStates) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                dialog.getContentTable().clear();
                                if (gameStates == null)
                                    dialog.text("Error fetching game states");
                                else {
                                    String allKeys = "";
                                    for (int i = 0; i < gameStates.size; i++)
                                        allKeys += gameStates.get(i) + "\n";

                                    if (allKeys.isEmpty())
                                        allKeys = "(no saved game state)";

                                    dialog.text(allKeys);
                                }
                                dialog.reshow();
                            }
                        });
                    }
                });
                dialog.text(fetchingNow ? "Fetching..." : "Could not fetch");
                dialog.show(mainStage);
            }
        });

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setFillParent(true);
        table.defaults().pad(5);
        mainStage.addActor(table);

        table.add(new Label("Gdx-GameServices Demo App", skin)).colspan(3).padBottom(0);
        table.row();
        table.add(repoLink).padBottom(20).colspan(3);
        table.row();

        table.add(new Label("Game Service ID:", skin)).right();
        table.add(new Label(gsClient.getGameServiceId(), skin)).left();
        table.add();

        table.row();
        table.add(new Label("Status:", skin)).right();
        table.add(gsStatus).left();
        table.add(signInButton);

        table.row();
        table.add(new Label("User name:", skin)).right();
        table.add(gsUsername).left();
        table.add();

        table.row().padTop(10);
        table.add(new Label("Leaderboard:", skin)).right();
        table.add(new Label(LEADERBOARD1, skin)).left();

        Table leaderBoardButtons = new Table();
        leaderBoardButtons.defaults().uniform().pad(5);
        leaderBoardButtons.add(showLeaderBoards);
        leaderBoardButtons.add(fetchLeaderboards);
        table.add(leaderBoardButtons);

        table.row();
        table.add();
        table.add(scoreFillin);
        table.add(submitToLeaderboard);

        table.row().padTop(10);
        table.add(new Label("Achievements:", skin)).right();
        table.add(new Label(ACHIEVEMENT1, skin)).left();

        Table achievementsButtons = new Table();
        achievementsButtons.defaults().uniform().pad(5);
        achievementsButtons.add(showAchievements);
        achievementsButtons.add(fetchAchievements);
        achievementsButtons.add(unlockAchievement);
        table.add(achievementsButtons);

        table.row();
        table.add(new Label("Events:", skin)).right();
        table.add(new Label(EVENT1, skin));
        table.add(submitEvent1Btn);

        if (gsClient.isFeatureSupported(IGameServiceClient.GameServiceFeature.GameStateStorage)) {
            table.row();
            table.add(new Label("Cloud storage:", skin)).right();
            table.add(cloudData);
            Table storageButtons = new Table();
            storageButtons.add(saveToCloud);
            storageButtons.add(loadFromCloud);
            storageButtons.add(deleteFromCloud);
            storageButtons.add(fetchCloudList);
            table.add(storageButtons);
        }
    }

    private void fetchLeaderboard(boolean playerRelated) {
        final MyDialog dialog = new MyDialog("Leaderboard");

        if (!playerRelated) {
            TextButton nowPlayer = new TextButton("Player related", skin);
            nowPlayer.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    fetchLeaderboard(true);
                }
            });
            dialog.button(nowPlayer);
        }

        boolean fetchingNow = gsClient.fetchLeaderboardEntries(LEADERBOARD1, 8, playerRelated,
                new IFetchLeaderBoardEntriesResponseListener() {
                    @Override
                    public void onLeaderBoardResponse(final Array<ILeaderBoardEntry> leaderBoard) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                showLeaderBoardEntries(dialog, leaderBoard);
                            }
                        });
                    }
                });

        dialog.text(fetchingNow ? "Fetching..." : "Could not fetch");
        dialog.show(mainStage);
    }

    private void showAchievementsList(MyDialog dialog, Array<IAchievement> achievements) {
        dialog.getContentTable().clear();

        if (achievements == null) {
            dialog.text("Could not fetch achievements");
        } else if (achievements.size > 0) {
            Table resultTable = new Table();
            resultTable.defaults().pad(3, 5, 3, 5);

            for (int i = 0; i < achievements.size; i++) {
                IAchievement ach = achievements.get(i);
                resultTable.row();
                resultTable.add(new Label(ach.getTitle(), skin));
                resultTable.add(new Label(ach.isUnlocked() ? "unlocked" : "locked", skin));
                resultTable.add(new Label(Integer.toString((int) (ach.getCompletionPercentage() * 100)) + "%", skin));
            }

            dialog.getContentTable().add(resultTable);
        } else
            dialog.text("No achievements");

        dialog.reshow();
    }

    private void showLeaderBoardEntries(MyDialog dialog, Array<ILeaderBoardEntry> leaderBoard) {
        dialog.getContentTable().clear();

        if (leaderBoard == null) {
            dialog.text("Could not fetch leaderboard");
        } else if (leaderBoard.size > 0) {
            Table resultTable = new Table();
            resultTable.defaults().pad(3, 5, 3, 5);

            for (int i = 0; i < leaderBoard.size; i++) {
                ILeaderBoardEntry le = leaderBoard.get(i);
                resultTable.row();
                resultTable.add(new Label(le.getScoreRank(), skin));

                String userDisplayName = le.getUserDisplayName();
                if (le.getUserId() == null)
                    userDisplayName = "(" + userDisplayName + ")";
                else if (le.isCurrentPlayer())
                    userDisplayName = "*" + userDisplayName;
                resultTable.add(new Label(userDisplayName, skin));

                resultTable.add(new Label(le.getFormattedValue(), skin));
                resultTable.add(new Label(le.getScoreTag(), skin));
            }

            dialog.getContentTable().add(resultTable);
        } else
            dialog.text("No leaderboard entries");

        dialog.reshow();
    }

    private void gsSignInOrOut() {
        if (gsClient.isSessionActive())
            gsClient.logOff();
        else {
            if (!gsClient.logIn())
                Gdx.app.error("GS_ERROR", "Cannot sign in: No credentials or session id given.");

            refreshStatusLabel();
        }
    }

    private void refreshStatusLabel() {
        String newStatusText;
        String newUserText;

        if (gsClient.isSessionActive())
            newStatusText = "SESSION ACTIVE";
        else if (gsClient.isConnectionPending())
            newStatusText = "CONNECTING SESSION...";
        else
            newStatusText = "NO SESSION";

        gsStatus.setText(newStatusText);

        signInButton.setText(gsClient.isSessionActive() ? "Sign out" : "Sign in");

        newUserText = gsClient.getPlayerDisplayName();
        gsUsername.setText(newUserText != null ? newUserText : "(none)");
    }

    private void prepareSkin() {
        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but
        // strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();
        atlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
        skin.addRegions(atlas);
        skin.load(Gdx.files.internal("skin/uiskin.json"));

    }

    @Override
    public void render() {
        if(visible) {
            Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            mainStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            mainStage.draw();
        }


            gameStage.draw();
            gamePauseStage.draw();
            gameOverStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        //mainStage.getViewport().update(vpWidth, vpHeight, true);
    }

    @Override
    public void dispose() {
        mainStage.dispose();
        skin.dispose();
        atlas.dispose();


        gameStage.dispose();
        badlogic.dispose();
    }

    @Override
    public void pause() {
        super.pause();

        gsClient.pauseSession();
    }

    @Override
    public void resume() {
        super.resume();

        gsClient.resumeSession();
    }

    @Override
    public void gsOnSessionActive() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                refreshStatusLabel();
            }
        });
    }

    @Override
    public void gsOnSessionInactive() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                refreshStatusLabel();
            }
        });
    }

    @Override
    public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
        Dialog dialog = new MyDialog("Error");
        dialog.text(et.toString() + ": " + msg);
        dialog.show(mainStage);
    }

    public class MyDialog extends Dialog {
        public MyDialog(String title) {
            super(title, skin);
            super.button("OK");
        }

        public void reshow() {
            this.show(mainStage, Actions.alpha(1)).setPosition(Math.round((mainStage.getWidth() - this.getWidth()) / 2),
                    Math.round((mainStage.getHeight() - this.getHeight()) / 2));

        }
    }
    @Override
    public void goToGameStage() {

hideAllStages();
        gameStage.returnCameraZoom();
        gameStage.returnCameraPos();
        gameStage.setVisible(true);
        Gdx.input.setInputProcessor(gameStage);
        Gdx.input.setCatchBackKey(true);
    }


    @Override
    public void goToGameStageRefresh() {

        hideAllStages();
        gameStage.returnCameraZoom();
        gameStage.returnCameraPos();
        gameStage.setVisible(true);
        gameStage.updateFieldRefresh();
        Gdx.input.setInputProcessor(gameStage);
        Gdx.input.setCatchBackKey(true);
    }


    @Override
    public void setPortrait()
    {
        vpWidth =vpShort;
        vpHeight=vpLong;//portrait mode is tall and narrow

        //gameStage.getViewport().apply();
        gameStage.getViewport().update(vpWidth,vpHeight);
        gameStage.getViewport().setWorldSize(vpWidth,vpHeight);
        gameStage.getViewport().getCamera().viewportHeight = vpHeight;
        gameStage.getViewport().getCamera().viewportWidth = vpWidth;

        gameStage.getViewport().getCamera().position.set(vpWidth/2,vpHeight/2,0);
        gameStage.setNewCameraPos(vpWidth/2,vpHeight/2);
        gameStage.setDefaultCameraPos(vpWidth/2,vpHeight/2);
        gameStage.setNewCameraZoomTarget(1);
        gameStage.setDefaultCameraZoom(1);
        gameStage.getViewport().getCamera().update();
        platform.SetOrientation("portrait");

        gamePauseStage.changeOrientation(true);
    }
    @Override
    public void setLandscape()
    {
        vpWidth =vpLong;
        vpHeight=vpShort;//landscape mode is tshort snd wide

        //gameStage.getViewport().apply();
        gameStage.getViewport().update(vpWidth,vpHeight);
        gameStage.getViewport().setWorldSize(vpWidth,vpHeight);
        gameStage.getViewport().getCamera().viewportHeight = vpHeight;
        gameStage.getViewport().getCamera().viewportWidth = vpWidth;

        gameStage.getViewport().getCamera().position.set(vpWidth/2,vpHeight/2,0);
        gameStage.setNewCameraPos(vpWidth/2,vpHeight/2);
        gameStage.setDefaultCameraPos(vpWidth/2,vpHeight/2);
        gameStage.setNewCameraZoomTarget(1);
        gameStage.setDefaultCameraZoom(1);
        gameStage.getViewport().getCamera().update();
        platform.SetOrientation("landscape");

        gamePauseStage.changeOrientation(false);
    }

    @Override
    public void updateFieldSwapOrientation()
    {
        gameStage.updateFieldSwapOrientation();
    }

    @Override
    public void updateOptionsGoToGameStage(int newNoOfRows, int newNoOfColumns,
                                           int newPortrait1Landscape2, int newFieldPosX,
                                           int newFieldPosY, int newFieldWidth,
                                           int newFieldHeight,int newZoom,int newDifficulty,
                                           int newGameMode, int newSymbolType) {
        hideAllStages();
        gameStage.returnCameraZoom();
        gameStage.returnCameraPos();
        gameStage.setVisible(true);

        //these options can be changed without recalculating the field
        if(newZoom+newDifficulty+newSymbolType>0)
        {
            if(newZoom!=0)
            {gameStage.updateZoom(newZoom);}
            if(newDifficulty!=0){gameStage.updateDifficulty(newDifficulty);}
            if(newSymbolType!=0){gameStage.updateSymbolType(newSymbolType);}
        }

        //certain options require the entire field to be recalculated, if any of those options are altered, recalculate new field,
        if(gameStage.isNewGameMode(newGameMode)||gameStage.isNewPortrait1Landscape2(newPortrait1Landscape2)||gameStage.isNewNoOfColumns(newNoOfColumns)||gameStage.isNewNoOfRows(newNoOfRows))
        {
            if(newPortrait1Landscape2==1)
            {
                setPortrait();
            }
            else if(newPortrait1Landscape2==2)
            {
                setLandscape();
            }
            gameStage.updateField(newNoOfRows, newNoOfColumns, newPortrait1Landscape2,
                    newFieldPosX, newFieldPosY, newFieldWidth, newFieldHeight,
                    newGameMode,newSymbolType);
        }
        setVisible(false);
        gamePauseStage.setVisible(false);
        Gdx.input.setInputProcessor(gameStage);
        Gdx.input.setCatchBackKey(true);
    }





    @Override
    public void goToGamePauseStage(boolean gameOver) {
        hideAllStages();
        gamePauseStage.setGameOver(gameOver);
        if(gameOver)
        {
            goToScoreboardOption();
        }
        gamePauseStage.setVisible(true);
        Gdx.input.setInputProcessor(gamePauseStage);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void goToScoreboardOption()
    {
        disableAllOptions();
        gamePauseStage.scoreboardOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(9);
    }


    @Override
    public void goToDifficultyOption()
    {
        disableAllOptions();
        gamePauseStage.difficultyOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(2);

    }
    @Override
    public void goToSymbolOption()
    {
        disableAllOptions();
        gamePauseStage.symbolOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(4);

    }
    @Override
    public void goToMainOption()
    {
        disableAllOptions();
        gamePauseStage.hexOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(0);
    }
    @Override
    public void goToZoomModeOption()
    {
        disableAllOptions();
        gamePauseStage.zoomModeOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(8);
    }
    @Override
    public void goToNoOfHexesOption()
    {
        disableAllOptions();
        gamePauseStage.noOfHexesOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(7);
    }
    @Override
    public void goToGameModeOption()
    {
        disableAllOptions();
        gamePauseStage.gameModeOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(6);
    }

    @Override
    public void goToSwapOrientationOption()
    {
        disableAllOptions();
        gamePauseStage.swapOrientationOptionField.enableOptions();
        gamePauseStage.setActiveFieldIndex(3);
    }
    public void disableAllOptions()
    {
        gamePauseStage.hexOptionField.disableOptions();
        gamePauseStage.difficultyOptionField.disableOptions();
        gamePauseStage.swapOrientationOptionField.disableOptions();
        gamePauseStage.symbolOptionField.disableOptions();
        gamePauseStage.gameModeOptionField.disableOptions();
        gamePauseStage.noOfHexesOptionField.disableOptions();
        gamePauseStage.zoomModeOptionField.disableOptions();
        gamePauseStage.scoreboardOptionField.disableOptions();
    }

    @Override
    public  void setScoreboardMode(int scoreboardMode)
    {
        gamePauseStage.setScoreboardMode(scoreboardMode);
    }

    @Override
    public  void showScoreboard()
    {
        try {
            gsClient.showLeaderboards(null);
        } catch (GameServiceException e) {
            e.printStackTrace();
        }
    }
    @Override
    public  boolean submitScore(int score, int difficulty, int noOfHexes)
    {
        //i intend to have multiple scoreboards which will cover the different difficulties and noofhexes
        //        gsClient.submitToLeaderboard(difficulty+LEADERBOARD1+noOfHexes, score, gsClient.getGameServiceId());

        return gsClient.submitToLeaderboard(LEADERBOARD1, score, gsClient.getGameServiceId());
    }

    @Override
    public  void setDifficulty(int difficulty)
    {
        gamePauseStage.setDifficulty(difficulty);
    }

    @Override
    public  void setSymbol(int symbol)
    {
        gamePauseStage.setSymbol(symbol);
    }
    @Override
    public void goToMainStage() {
       hideAllStages();
        setVisible(true);
        Gdx.input.setInputProcessor(mainStage);
        Gdx.input.setCatchBackKey(true);
    }
    @Override
    public  void setOrientation(int orientation)
    {
        gamePauseStage.setOrientation(orientation);
    }
    @Override
    public  void setGameMode(int gameMode)
    {
        gamePauseStage.setGameMode(gameMode);
    }
    @Override
    public  void setNoOfHexes(int noOfHexes)
    {
        gamePauseStage.setNoOfHexes(noOfHexes);
    }
    @Override
    public  void setZoomMode(int zoomMode)
    {
        gamePauseStage.setZoomMode(zoomMode);
    }
    @Override
    public int getScreenHeight()
    {
        return vpHeight;
    }
    @Override
    public int getScreenWidth()
    {
        return vpWidth;
    }


    @Override
    public void setScore(int score )
    {
       // gameOverStage.setScore(timeValueArg,difficultyValueArg,gameModeArg);
        gamePauseStage.setScore(score);
    }
    @Override
    public void goToGameOverStage(int noOfHexes, int score, int difficulty,int gameMode) {

        hideAllStages();
        gameOverStage.setScore(noOfHexes,score,difficulty, gameMode);
        gameOverStage.setVisible(true);
        Gdx.input.setInputProcessor(gameOverStage);
        Gdx.input.setCatchBackKey(true);
    }

    void hideAllStages()
    {
        gameStage.setVisible(false);
        setVisible(false);//main stage
        gamePauseStage.setVisible(false);
        gameOverStage.setVisible(false);
        gameStage.resetCameraZoom();
        gameStage.resetCameraPos();
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }




}
