package fr.ubx.poo.ubomb.game;


import fr.ubx.poo.ubomb.go.decor.Decor;
import fr.ubx.poo.ubomb.launcher.Entity;

import java.util.Collection;

public interface Grid {
    int width();

    int height();

    Decor get(Position position);

    void remove(Position position);

    Collection<Decor> values();


    boolean inside(Position nextPos);

    void set(Position position, Decor decor);

    public Position getDecorPosition(Entity e);
}
