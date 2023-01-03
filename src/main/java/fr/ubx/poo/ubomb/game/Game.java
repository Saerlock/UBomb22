package fr.ubx.poo.ubomb.game;

import fr.ubx.poo.ubomb.go.GameObject;
import fr.ubx.poo.ubomb.go.character.Player;
import fr.ubx.poo.ubomb.go.decor.Box;
import fr.ubx.poo.ubomb.go.decor.DoorNextClosed;
import fr.ubx.poo.ubomb.go.decor.DoorNextOpened;
import fr.ubx.poo.ubomb.launcher.Entity;

import java.util.LinkedList;
import java.util.List;

public class Game {

    private final Configuration configuration;
    private final Player player;
    private final Grid[] grids;
    private int activeGrid = 1;
    private boolean changingGrid = false;

    public Game(Configuration configuration, Grid[] grids) {
        this.configuration = configuration;
        this.grids = grids;
        player = new Player(this, configuration.playerPosition());
    }

    public boolean getChangingGrid() {
        return changingGrid;
    }

    public int getActiveGrid() {
        return activeGrid;
    }

    public void resetChangingGrid() {
        this.changingGrid = false;
    }

    public void changeGrid(String instruction) throws Exception {
        switch (instruction) {
            case "down" -> {
                if (activeGrid <= 1) {
                    throw new Exception("Erreur: on ne peut acceder a une grille d'indice < 1");
                }
                activeGrid--;
                changingGrid = true;
            }
            case "up" -> {
                if (activeGrid >= grids.length) {
                    throw new Exception("Erreur: on ne peut acceder a une grille d'indice >= au nb de grilles");
                }
                activeGrid++;
                changingGrid = true;
            }
            default -> throw new Exception("Erreur: le switch de changeGrid ne supporte pas ce choix");
        }
    }

    public Configuration configuration() {
        return configuration;
    }

    // Returns the player, monsters and bomb at a given position
    public List<GameObject> getGameObjects(Position position) {
        List<GameObject> gos = new LinkedList<>();
        if (player().getPosition().equals(position))
            gos.add(player);
        return gos;
    }

    public Grid grid() {
        return grids[activeGrid-1];
    }

    public Player player() {
        return this.player;
    }

    public DoorNextOpened requestOpenDoor() throws Exception {
        Position testPos;
        switch (player.getDirection()) {
            case UP -> testPos = new Position(player.getPosition().x(), player.getPosition().y() - 1);
            case DOWN -> testPos = new Position(player.getPosition().x(), player.getPosition().y() + 1);
            case LEFT -> testPos = new Position(player.getPosition().x() - 1, player.getPosition().y());
            case RIGHT -> testPos = new Position(player.getPosition().x() + 1, player.getPosition().y());
            default -> throw new Exception("Erreur: cette direction n'existe pas");
        }

        if ((grid().get(testPos) instanceof DoorNextClosed d) && player.getKeyCount() > 0) {
            grid().remove(testPos);
            player.decKeyCount();
            DoorNextOpened newDoor = new DoorNextOpened(testPos);
            grid().set(testPos, newDoor);
            return newDoor;
        }
        return null;
    }

    public void moveBox(Position position, Direction direction) {
        if (grid().get(position) instanceof Box b) {
            b.remove();
            grid().remove(position);
            Box newBox = new Box(position);
            grid().set(position, newBox);
            return;
        }
    }
}
