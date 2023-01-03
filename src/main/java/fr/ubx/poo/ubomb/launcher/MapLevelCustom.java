package fr.ubx.poo.ubomb.launcher;

import java.util.ArrayList;

public class MapLevelCustom extends MapLevel {



    public MapLevelCustom(String[] level) {
        super(level[0].length(), level.length);
        Entity[][] decodedLevel = decode(level);
        for (int i = 0; i < level[0].length(); i++)
            for (int j = 0; j < level.length; j++)
                set(i, j, decodedLevel[j][i]);
    }

    private Entity[][] decode(String[] level) {

        Entity[][] levelGrid = new Entity[this.height()][this.width()];
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[0].length(); j++) {
                levelGrid[i][j] = Entity.fromCode(level[i].charAt(j));
            }
        }

        return levelGrid;
    }
}
