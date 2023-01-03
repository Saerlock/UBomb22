package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.decor.*;
import fr.ubx.poo.ubomb.go.decor.bonus.*;
import fr.ubx.poo.ubomb.launcher.Entity;
import fr.ubx.poo.ubomb.launcher.MapLevel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Level implements Grid {

    private final int width;

    private final int height;

    private final MapLevel entities;


    private final Map<Position, Decor> elements = new HashMap<>();

    public Level(MapLevel entities) {
        this.entities = entities;
        this.width = entities.width();
        this.height = entities.height();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(i, j);
                Entity entity = entities.get(i, j);
                switch (entity) {
                    case Box:
                        elements.put(position, new Box(position));
                        break;
                    case Stone:
                        elements.put(position, new Stone(position));
                        break;
                    case Tree:
                        elements.put(position, new Tree(position));
                        break;
                    case BombRangeDec:
                        elements.put(position, new BombRangeDec(position));
                        break;
                    case BombRangeInc:
                        elements.put(position, new BombRangeInc(position));
                        break;
                    case BombNumberDec:
                        elements.put(position, new BombNumberDec(position));
                        break;
                    case BombNumberInc:
                        elements.put(position, new BombNumberInc(position));
                        break;
                    case Heart:
                        elements.put(position, new Heart(position));
                        break;
                    case Key:
                        elements.put(position, new Key(position));
                        break;
                    case DoorPrevOpened:
                        elements.put(position, new DoorPrevOpened(position));
                        break;
                    case DoorNextOpened:
                        elements.put(position, new DoorNextOpened(position));
                        break;
                    case DoorNextClosed:
                        elements.put(position, new DoorNextClosed(position));
                        break;
                    case Monster:
                        elements.put(position, new Monster(position));
                        break;
                    case Princess:
                        elements.put(position, new Princess(position));
                        break;
                    case Empty:
                        break;
                    default:
                        throw new RuntimeException("EntityCode " + entity.name() + " not processed");
                }
            }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public Decor get(Position position) {
        return elements.get(position);
    }

    @Override
    public void remove(Position position) {
        elements.remove(position);
    }

    public Collection<Decor> values() {
        return elements.values();
    }

    @Override
    public boolean inside(Position position) {
        return position.x() >= 0 && position.x() < this.width && position.y() >= 0 && position.y() < this.height;
    }

    @Override
    public void set(Position position, Decor decor) {
        if (!inside(position))
            throw new IllegalArgumentException("Illegal Position");
        if (decor != null) {
            elements.put(position, decor);
            if (decor instanceof DoorNextOpened) {
                setDoor(position, Entity.DoorNextOpened);
            }
        }
    }

    public void setDoor(Position position, Entity entity) {
        entities.set(position.x(), position.y(), entity);
    }

    public Position getDecorPosition(Entity e) {
        for (int i = 0; i < entities.width(); i++) {
            for (int j = 0; j < entities.height(); j++) {
                if (entities.get(i, j) == e) {
                    return new Position(i, j);
                }
            }
        }
        throw new RuntimeException("Erreur : aucune entite \"" + e + "\" trouvee");
    }

    public static Position nextPosition(Position position, Direction direction) {
        switch (direction) {
            case UP -> {
                return new Position(position.x(), position.y() - 1);
            }
            case DOWN -> {
                return new Position(position.x(), position.y() + 1);
            }
            case LEFT -> {
                return new Position(position.x() - 1, position.y());
            }
            case RIGHT -> {
                return new Position(position.x() + 1, position.y());
            }
        }

        return null;
    }
}
