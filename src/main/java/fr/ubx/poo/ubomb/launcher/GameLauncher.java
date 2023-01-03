package fr.ubx.poo.ubomb.launcher;

import fr.ubx.poo.ubomb.game.Configuration;
import fr.ubx.poo.ubomb.game.Game;
import fr.ubx.poo.ubomb.game.Level;
import fr.ubx.poo.ubomb.game.Position;

import java.io.File;

public class GameLauncher {

    public static Game load() {
        Configuration configuration = new Configuration(new Position(0, 0), 3, 5, 4000, 5, 1000);
        return new Game(configuration, new Level[]{new Level(new MapLevelDefault())});
    }

    public static Game load(File file) {
        WorldParser newWorld = new WorldParser(file);
        return new Game(newWorld.buildConfig(), newWorld.buildLevels());
    }
}
