package fr.ubx.poo.ubomb.go.decor;

import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Position;
import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.Takeable;

public abstract class Decor extends GameObject implements Takeable {

    public Decor(Game game, Position position) {
        super(game, position);
    }

    public Decor(Position position) {
        super(position);
    }

}