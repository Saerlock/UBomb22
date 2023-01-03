package fr.ubx.poo.ubomb.launcher;

import fr.ubx.poo.ubomb.game.Configuration;
import fr.ubx.poo.ubomb.game.Level;
import fr.ubx.poo.ubomb.game.Position;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

public class WorldParser {

    Properties config;

    public WorldParser(File world) throws MapException {
        this.config = new Properties();
        try {
            Reader in = new FileReader(world);
            config.load(in);
        } catch (Exception e) {
            throw new MapException("Erreur : fichier invalide");
        }
    }

    public Configuration buildConfig() {
        return new Configuration(getStartPosition(), getBombBagCapacity(), getPlayerLives(), getPlayerInvincibilityTime(), getMonsterVelocity(), getMonsterInvincibilityTime());
    }

    public Level[] buildLevels() {
        ArrayList<Level> listLevels = new ArrayList<>();
        for (int i = 0; i < getLevels(); i++) {
            listLevels.add(buildLevel(i+1));
        }

        return listLevels.toArray(new Level[0]);
    }

    public Level buildLevel(int index) {
        return new Level(new MapLevelCustom(getLevelX(index)));
    }

    public Boolean getCompression() {
        return Boolean.parseBoolean(config.getProperty("compression", Boolean.toString(false)));
    }

    public int getLevels() {
        return Integer.parseInt(config.getProperty("levels", Integer.toString(1)));
    }

    public String[] getLevelX(int index) {
        String[] grid = config.getProperty("level" + index).split("x");

        if (getCompression()) {
            ArrayList<String> newGrid = new ArrayList<>();
            for (String s : grid) {
                String newS = unzip(s);
                newGrid.add(newS);
            }

            grid = newGrid.toArray(new String[0]);
        }

        return grid;
    }

    public int getPlayerLives() {
        return Integer.parseInt(config.getProperty("playerLives", Integer.toString(5)));
    }

    public int getMonsterVelocity() {
        return Integer.parseInt(config.getProperty("monsterVelocity", Integer.toString(5)));
    }

    public int getPlayerInvincibilityTime() {
        return Integer.parseInt(config.getProperty("playerInvincibilityTime", Integer.toString(4000)));
    }

    public int getMonsterInvincibilityTime() {
        return Integer.parseInt(config.getProperty("monsterInvincibilityTime", Integer.toString(1000)));
    }

    public int getBombBagCapacity() {
        return Integer.parseInt(config.getProperty("bombBagCapacity", Integer.toString(3)));
    }

    public Position getStartPosition() {
        String[] splitPosition = config.getProperty("player", "0x0").split("x");
        return new Position(Integer.parseInt(splitPosition[0]), Integer.parseInt(splitPosition[1]));
    }

    private String unzip(String s) {
        StringBuilder sb = new StringBuilder();
        char lastChar = 'x';

        for (int i = 0; i < s.length(); i++) {
            char actualChar = s.charAt(i);
            if (i == 0 && Character.isDigit(actualChar)) {
                throw new MapException("Erreur : une ligne de level ne peut pas commencer par un nombre dans le fichier config");
            }

            if (Character.isDigit(actualChar)) {
                int nbOccurrence = Character.getNumericValue(actualChar) - 1;

                while (nbOccurrence > 0) {
                    sb.append(lastChar);
                    nbOccurrence--;
                }
            } else {
                sb.append(actualChar);
                lastChar = actualChar;
            }
        }

        return sb.toString();
    }
}

