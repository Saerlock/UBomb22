/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubomb.go.character;

import fr.ubx.poo.ubomb.engine.Timer;
import fr.ubx.poo.ubomb.game.Direction;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Level;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Movable;
import fr.ubx.poo.ubomb.go.TakeVisitor;
import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.Key;
import fr.ubx.poo.ubomb.launcher.Entity;

public class Player extends GameObject implements Movable, TakeVisitor {

    private Direction direction;
    private boolean moveRequested = false;
    private final int lives;
    private int missingLives; //I added a missing lives counter, to be able to always "remember" the max number of lives
    private Timer invincibilityTimer;
    private int bombCount = 0;
    private int bombRange = 1;
    private int keyCount = 0;

    private boolean canTakeDoor = true;



    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.DOWN;
        this.lives = game.configuration().playerLives();
        this.missingLives = 0;
        this.invincibilityTimer = new Timer(game.configuration().playerInvincibilityTime());
    }

    public boolean canTakeDoor() {
        return canTakeDoor;
    }

    public void setCanTakeDoor(boolean canTakeDoor) {
        this.canTakeDoor = canTakeDoor;
    }

    public int getBombCount() {
        return bombCount;
    }

    public void decBombCount() {
        bombCount -= 1;
    }

    public void incBombCount() {
        bombCount += 1;
    }

    public int getBombRange() {
        return bombRange;
    }

    public void decBombRange () {
        bombRange -= 1;
    }

    public void incBombRange () {
        bombRange += 1;
    }

    public int getKeyCount() {
        return keyCount;
    }

    public void decKeyCount() {
        keyCount -= 1;
    }

    public void incKeyCount() {
        keyCount += 1;
    }

    public int getMissingLives() {
        return missingLives;
    }

    public Timer invincibilityTimer() {
        return invincibilityTimer;
    }

    public void looseLife() {
        this.missingLives += 1;
    }

    public void gainLife() {
        this.missingLives -= 1;
    }



    @Override
    public void take(Key key) {
        System.out.println("Take the key ...");
    }

    public void doMove(Direction direction) {
        // This method is called only if the move is possible, do not check again
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.grid().get(nextPos);
        if (next != null) {
            next.takenBy(this);
        }
        setPosition(nextPos);
        if(!canTakeDoor) {
            canTakeDoor = true;
        }
    }


    public int getLives() {
        return lives - missingLives;
    }

    public Direction getDirection() {
        return direction;
    }

    public void resetDirection() {
        this.direction = Direction.DOWN;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }

        moveRequested = true;
    }

    public final boolean canMove(Direction direction) {
        if (!this.game.grid().inside(direction.nextPosition(getPosition())))
            return false;
        Decor nextItem = this.game.grid().get(direction.nextPosition(getPosition()));
        return !(nextItem instanceof Box) && !(nextItem instanceof Stone) && !(nextItem instanceof Tree) && !(nextItem instanceof DoorNextClosed);
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
            }
        }
        moveRequested = false;

        invincibilityTimer.update(now);
    }

    @Override
    public void explode() {
        // TODO
    }

    public void dropBomb() {
        if (getBombCount() < 1) {
            return;
        }

    }
}
