package tileengine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('⚇', Color.white, new Color(34, 139, 60), "you", "src/tileengine/little_guy.jpg",0);
    //@웃⚉
    public static final TETile WALL = new TETile('▣', Color.lightGray, Color.darkGray, "wall", "src/tileengine/gray_walls.jpg",1); //#, ▣, Color.darkGray
    public static final TETile FLOOR = new TETile('▧', new Color(128, 192, 128), new Color(34, 139, 60), "floor", "src/tileengine/brown_floor.jpg", 2);
    //.
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 3);
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass", 4);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 5);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower", 6);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 7);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 8);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 9);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 10);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 11);

    public static final TETile CELL = new TETile('█', Color.white, Color.black, "cell", 12);
    public static final TETile CHEST = new TETile('□', Color.yellow, Color.black, "stash", "src/tileengine/chest.jpg",13);
    public static final TETile COIN = new TETile('•', Color.yellow, new Color(34, 139, 60), "coin", "src/tileengine/coin.jpg",14);

}


