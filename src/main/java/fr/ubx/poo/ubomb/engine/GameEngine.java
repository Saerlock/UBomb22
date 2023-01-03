/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.engine;

import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Level;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.launcher.Entity;
import fr.ubx.poo.ubomb.view.ImageResource;
import fr.ubx.poo.ubomb.view.Sprite;
import fr.ubx.poo.ubomb.view.SpriteFactory;
import fr.ubx.poo.ubomb.view.SpritePlayer;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public final class GameEngine {

    private static AnimationTimer gameLoop;
    private final Game game;
    private final Player player;
    private final List<Sprite> sprites = new LinkedList<>();
    private final Set<Sprite> cleanUpSprites = new HashSet<>();
    private final Stage stage;
    private StatusBar statusBar;
    private Pane layer;
    private Input input;

    public GameEngine(Game game, final Stage stage) {
        this.stage = stage;
        this.game = game;
        this.player = game.player();
        initialize();
        buildAndSetGameLoop();
    }

    private void initialize() {
        Group root = new Group();
        layer = new Pane();

        int height = game.grid().height();
        int width = game.grid().width();
        int sceneWidth = width * ImageResource.size;
        int sceneHeight = height * ImageResource.size;
        Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        stage.setScene(scene);
        stage.setResizable(false);
        stage.sizeToScene();
        stage.hide();
        stage.show();

        input = new Input(scene);
        root.getChildren().add(layer);
        statusBar = new StatusBar(root, sceneWidth, sceneHeight, game);

        // Create sprites
        for (var decor : game.grid().values()) {
            sprites.add(SpriteFactory.create(layer, decor));
            decor.setModified(true);
        }

        sprites.add(new SpritePlayer(layer, player));
    }

    void buildAndSetGameLoop() {
        gameLoop = new AnimationTimer() {
            public void handle(long now) {
                // Check keyboard actions
                processInput(now);

                // Do actions
                update(now);
                createNewBombs(now);
                checkCollision(now);
                checkExplosions();

                // Graphic update
                cleanupSprites();
                render();
                statusBar.update(game);
            }
        };
    }

    private void checkExplosions() {
        // Check explosions of bombs
    }

    private void animateExplosion(Position src, Position dst) {
        ImageView explosion = new ImageView(ImageResource.EXPLOSION.getImage());
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), explosion);
        tt.setFromX(src.x() * Sprite.size);
        tt.setFromY(src.y() * Sprite.size);
        tt.setToX(dst.x() * Sprite.size);
        tt.setToY(dst.y() * Sprite.size);
        tt.setOnFinished(e -> {
            layer.getChildren().remove(explosion);
        });
        layer.getChildren().add(explosion);
        tt.play();
    }

    private void createNewBombs(long now) {
        // Create a new Bomb is needed
    }

    private void checkCollision(long now) {
        // Check a collision between a monster and the player
    }

    private void processInput(long now) {
        if (input.isExit()) {
            gameLoop.stop();
            Platform.exit();
            System.exit(0);
        } else if (input.isMoveDown()) {
            player.requestMove(Direction.DOWN);
        } else if (input.isMoveLeft()) {
            player.requestMove(Direction.LEFT);
        } else if (input.isMoveRight()) {
            player.requestMove(Direction.RIGHT);
        } else if (input.isMoveUp()) {
            player.requestMove(Direction.UP);
        } else if (input.isKey()) {
            try {
                DoorNextOpened newDoor = game.requestOpenDoor();
                if (newDoor != null) {
                    sprites.add(SpriteFactory.create(layer, newDoor));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (input.isBomb()) {
            player.dropBomb();
        }
        input.clear();
    }

    private void showMessage(String msg, Color color) {
        Text waitingForKey = new Text(msg);
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(60));
        waitingForKey.setFill(color);
        StackPane root = new StackPane();
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, 400, 200, Color.WHITE);
        stage.setScene(scene);
        input = new Input(scene);
        stage.show();
        new AnimationTimer() {
            public void handle(long now) {
                processInput(now);
            }
        }.start();
    }


    private void update(long now) {
        player.update(now);

        if (player.getLives() == 0) {
            gameLoop.stop();
            showMessage("Perdu!", Color.RED);
        }

        if (this.game.grid().get(player.getPosition()) instanceof Princess) {
            gameLoop.stop();
            showMessage("Gagné!", Color.GREEN);
        }

        if (this.game.grid().get(player.getPosition()) instanceof Monster) {
            if (!player.invincibilityTimer().isRunning()){
                player.looseLife();
                player.invincibilityTimer().start();
                System.out.println(player.getLives());
            }
        }

        if (this.game.grid().get(Level.nextPosition(player.getPosition(), player.getDirection())) instanceof Box b) {
            Position posBox = Level.nextPosition(player.getPosition(), player.getDirection());
            Position nextPosBox = Level.nextPosition(posBox, player.getDirection());
            if (this.game.grid().inside(nextPosBox) && this.game.grid().get(nextPosBox) == null) {
                this.game.grid().remove(posBox);
                b.remove();
                Box newBox = new Box(nextPosBox);
                this.game.grid().set(nextPosBox, newBox);
                sprites.add(SpriteFactory.create(layer, newBox));
            }
        }

        if (this.game.grid().get(player.getPosition()) instanceof Key s) {
            player.incKeyCount();
            this.game.grid().remove(player.getPosition());
            s.remove();
        }

        if (this.game.grid().get(player.getPosition()) instanceof Heart h) {
            player.gainLife();
            this.game.grid().remove(player.getPosition());
            h.remove();
        }

        if (this.game.grid().get(player.getPosition()) instanceof BombNumberInc b) {
            player.incBombCount();
            this.game.grid().remove(player.getPosition());
            b.remove();
        }

        if (this.game.grid().get(player.getPosition()) instanceof BombNumberDec b) {
            if (player.getBombCount() > 1) {
                player.decBombCount();
            }
            this.game.grid().remove(player.getPosition());
            b.remove();
        }

        if (this.game.grid().get(player.getPosition()) instanceof BombRangeInc b) {
            player.incBombRange();
            this.game.grid().remove(player.getPosition());
            b.remove();
        }

        if (this.game.grid().get(player.getPosition()) instanceof BombRangeDec b) {
            if (player.getBombRange() > 1) {
                player.decBombRange();
            }
            this.game.grid().remove(player.getPosition());
            b.remove();
        }

        if (player.canTakeDoor() && this.game.grid().get(player.getPosition()) instanceof DoorNextOpened) {
            try {
                cleanUpSprites.addAll(sprites);
                gameLoop.stop();
                this.game.changeGrid("up");
                this.player.setPosition(this.game.grid().getDecorPosition(Entity.DoorPrevOpened));
                this.player.setCanTakeDoor(false);
                this.player.resetDirection();
                initialize();
                buildAndSetGameLoop();
                gameLoop.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if (player.canTakeDoor() && this.game.grid().get(player.getPosition()) instanceof DoorPrevOpened) {
            try {
                cleanUpSprites.addAll(sprites);
                gameLoop.stop();
                this.game.changeGrid("down");
                this.player.setPosition(this.game.grid().getDecorPosition(Entity.DoorNextOpened));
                this.player.setCanTakeDoor(false);
                this.player.resetDirection();
                initialize();
                buildAndSetGameLoop();
                gameLoop.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void cleanupSprites() {
        sprites.forEach(sprite -> {
            if (sprite.getGameObject().isDeleted()) {
                game.grid().remove(sprite.getPosition());
                cleanUpSprites.add(sprite);
            }
        });
        cleanUpSprites.forEach(Sprite::remove);
        sprites.removeAll(cleanUpSprites);
        cleanUpSprites.clear();
    }

    private void render() {
        sprites.forEach(Sprite::render);
    }

    public void start() {
        gameLoop.start();
    }
}