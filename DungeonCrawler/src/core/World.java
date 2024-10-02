package core;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import tileengine.TETile;
import tileengine.Tileset;


import java.util.*;

public class World {
    private final int width = 50;
    private final int height = 50;
    private final int numRooms;
    private final Random random;
    private TETile[][] world;
    private TETile[][] savedWorld;
    private int[] savedPos;
    private final int[][] rooms;
    private final int MIN_ROOMS = 7;
    private final int MAX_ROOMS = 12;
    private final int MIN_ROOM_SIZE = 3;
    private final int MAX_ROOM_SIZE = 8;
    int avX;
    int avY;
    int numChests;
    boolean[] chestsVisited;
    int numCoins;
    int coinsCollected;
    boolean inRoom;
    private final int COINS_ROOM_PERCENT = 10;

    public World(long seed) {
        //creates world and fill with nothing tiles
        world = new TETile[width][height];
        fillNothing(world);
        //creates new random object to be used whenever we do smth random
        random = new Random(seed);
        //gives us the number of rooms in the world
        numRooms = random.nextInt(MIN_ROOMS, MAX_ROOMS);
        //creates empty list of center room coordinates;
        rooms = new int[numRooms][2];
        roomsMaker();
        //creates halls
        connectRooms();
        //shows world
        createAvatar();
        createChests();
    }

    public int getCoins() {
        return coinsCollected;
    }

    public boolean roomStatus() {
        return inRoom;
    }

    public void createChests() {
        numChests = random.nextInt(1, 4);
        chestsVisited = new boolean[numChests];
        for (int i = 1; i < 4; i++) {
            world[rooms[i][0]][rooms[i][1]] = Tileset.CHEST;
        }
    }

    public void openChest() {
        savedPos = new int[]{avX, avY};
        savedWorld = world;
        TETile[][] chestRoom = new TETile[width][height];
        fillNothing(chestRoom);
        int centerX = width / 2;
        int centerY = height / 2;
        int roomWidth = random.nextInt(MIN_ROOM_SIZE, MAX_ROOM_SIZE);
        int roomHeight = random.nextInt(MIN_ROOM_SIZE, MAX_ROOM_SIZE);
        for (int x = (centerX - roomWidth); x < (centerX + roomWidth); x++) {
            for (int y = (centerY - roomHeight); y < (centerY + roomHeight); y++) {
                if (x == (centerX - roomWidth) || x == (centerX + roomWidth - 1)) {
                    chestRoom[x][y] = Tileset.WALL;
                } else if (y == (centerY - roomHeight) || y == (centerY + roomHeight - 1)) {
                    chestRoom[x][y] = Tileset.WALL;
                } else {
                    chestRoom[x][y] = Tileset.FLOOR;
                }
            }
        }
        chestRoom[centerX][centerY] = Tileset.AVATAR;
        avX = centerX;
        avY = centerY;
        world = chestRoom;
        fillCoins(centerX, centerY, roomWidth, roomHeight);
        inRoom = true;
    }

    private void fillCoins(int centerX, int centerY, int roomWidth, int roomHeight) {
        world[centerX - roomWidth + 1][centerY - roomHeight + 1] = Tileset.COIN;
        numCoins = 1;
        for (int x = (centerX - roomWidth); x < (centerX + roomWidth); x++) {
            for (int y = (centerY - roomHeight); y < (centerY + roomHeight); y++) {
                int fill = random.nextInt(width + height);
                if (world[x][y] == Tileset.FLOOR && fill < COINS_ROOM_PERCENT) {
                    world[x][y] = Tileset.COIN;
                    numCoins++;
                }
            }
        }
    }

    private void createAvatar() {
        avX = rooms[0][0];
        avY = rooms[0][1];
        world[avX][avY] = Tileset.AVATAR;
    }

    public void move(char key) {
        int[] posSwitch = switch (key) {
            case 'w', 'W' -> new int[]{0, 1};
            case 'a', 'A' -> new int[]{-1, 0};
            case 's', 'S' -> new int[]{0, -1};
            case 'd', 'D' -> new int[]{1, 0};
            default -> new int[]{0, 0};
        };
        TETile tile = world[avX + posSwitch[0]][avY + posSwitch[1]];
        if (tile == Tileset.FLOOR || tile == Tileset.CHEST || tile == Tileset.COIN) {
            world[avX][avY] = Tileset.FLOOR;
            avX += posSwitch[0];
            avY += posSwitch[1];
            world[avX][avY] = Tileset.AVATAR;
        }
        if (tile == Tileset.CHEST) {
            openChest();
        }
        if (tile == Tileset.COIN) {
            coinsCollected++;
            numCoins--;
            exitCoinRoom();
        }
    }

    public void exitCoinRoom() {
        if (inRoom && numCoins == 0) {
            inRoom = false;
            world = savedWorld;
            avX = savedPos[0];
            avY = savedPos[1];
        }
    }

    public TETile[][] getWorld() {
        return world;
    }

    //fills tile world with nothing tiles
    private void fillNothing(TETile[][] tiles) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    // creates non overlapping rooms of random sizes and stores their coordinates
    private void roomsMaker() {
        int roomsLeft = numRooms;
        //boolean avatarYet = false;
        while (roomsLeft > 0) {
            int centerX = random.nextInt(MAX_ROOM_SIZE, width - MAX_ROOM_SIZE);
            int centerY = random.nextInt(MAX_ROOM_SIZE, height - MAX_ROOM_SIZE);
            int roomWidth = random.nextInt(MIN_ROOM_SIZE, MAX_ROOM_SIZE);
            int roomHeight = random.nextInt(MIN_ROOM_SIZE, MAX_ROOM_SIZE);
            boolean assignmentWorked = roomCheck(centerX, centerY, roomWidth, roomHeight);
            if (assignmentWorked) {
                rooms[roomsLeft - 1][0] = centerX;
                rooms[roomsLeft - 1][1] = centerY;
                roomsLeft--;
            }
        }
    }

    // checks if there is already a room in a set of coords and assigns room if it's fine
    private boolean roomCheck(int centerX, int centerY, int roomWidth, int roomHeight) {
        if ((centerX + roomWidth) >= width || (centerX - roomWidth) < 0) {
            return false;
        }
        if ((centerY + roomHeight) >= height || (centerY + roomHeight) < 0) {
            return false;
        }
        TETile[][] checkArr = arrayCopier(world);
        for (int x = (centerX - roomWidth); x < (centerX + roomWidth); x++) {
            for (int y = (centerY - roomHeight); y < (centerY + roomHeight); y++) {
                if (checkArr[x][y] == Tileset.NOTHING) {
                    if (x == (centerX - roomWidth) || x == (centerX + roomWidth - 1)) {
                        checkArr[x][y] = Tileset.WALL;
                    } else if (y == (centerY - roomHeight) || y == (centerY + roomHeight - 1)) {
                        checkArr[x][y] = Tileset.WALL;
                    } else {
                        checkArr[x][y] = Tileset.FLOOR;
                    }
                } else {
                    return false;
                }
            }
        }
        world = checkArr;
        return true;
    }

    //creates a copy of the world
    private TETile[][] arrayCopier(TETile[][] copyThis) {
        TETile[][] copy = new TETile[width][height];
        for (int inner = 0; inner < width; inner++) {
            System.arraycopy(copyThis[inner], 0, copy[inner], 0, height);
            //@source: https://docs.oracle.com/javase/8/docs/api/java/lang/System.html
        }
        return copy;
    }

    //returns euclidian distance between two coordinate points
    private double distance(int[] point1, int[] point2) {
        double xDiff = Math.pow((point1[0] - point2[0]), 2);
        double yDiff = Math.pow((point1[1] - point2[1]), 2);
        return Math.pow((xDiff + yDiff), .5);
    }

    private void connectRooms() {
        WeightedQuickUnionUF uf = new WeightedQuickUnionUF(numRooms);
        ArrayList<int[]> edges = new ArrayList<>();
        for (int i = 0; i < numRooms; i++) {
            for (int j = i + 1; j < numRooms; j++) {
                int dist = (int) distance(rooms[i], rooms[j]);
                edges.add(new int[]{dist, i, j});
            }
        }
        //@source: https://stackoverflow.com/questions/35761864/java-sort-list-of-lists
        edges.sort(Comparator.comparingInt(a -> a[0]));
        for (int[] edge : edges) {
            int room1 = edge[1];
            int room2 = edge[2];
            if (!uf.connected(room1, room2)) {
                singleConnect(rooms[room1], rooms[room2]);
                uf.union(room1, room2);
            }
        }
    }

    private void singleConnect(int[] point1, int[] point2) {
        int startX = point1[0];
        int startY = point1[1];
        int endX = point2[0];
        int endY = point2[1];
        int stepX;
        int stepY;
        if (startX < endX) {
            stepX = 1;
        } else {
            stepX = -1;
        }
        if (startY < endY) {
            stepY = 1;
        } else {
            stepY = -1;
        }
        for (int x = startX; x != endX; x += stepX) {
            world[x][startY] = Tileset.FLOOR;
            if (world[x][startY + 1] == Tileset.NOTHING) {
                world[x][startY + 1] = Tileset.WALL;
            }
            if (world[x][startY - 1] == Tileset.NOTHING) {
                world[x][startY - 1] = Tileset.WALL;
            }
        }
        cornerCheat(endX, startY);
        for (int y = startY; y != endY; y += stepY) {
            world[endX][y] = Tileset.FLOOR;
            if (world[endX + 1][y] == Tileset.NOTHING) {
                world[endX + 1][y] = Tileset.WALL;
            }
            if (world[endX - 1][y] == Tileset.NOTHING) {
                world[endX - 1][y] = Tileset.WALL;
            }
        }

    }

    private void cornerCheat(int x, int y) {
        int[] dxList = new int[]{-1, 0, 1};
        int[] dyList = new int[]{-1, 0, 1};
        for (int dx : dxList) {
            int xCheck = x + dx;
            for (int dy : dyList) {
                int yCheck = y + dy;
                if (world[xCheck][yCheck] == Tileset.NOTHING) {
                    world[xCheck][yCheck] = Tileset.WALL;
                }
            }
        }
    }

}
