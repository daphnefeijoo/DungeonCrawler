package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;

import java.awt.Font;

public class Game {
    private World world;
    private final int width = 50;
    private final int height = 52;
    private final int offset = -2;
    TERenderer ter;
    private final Font titleFont = new Font("Papyrus", Font.BOLD, 60);
    private final Font optionsFont = new Font("Papyrus", Font.BOLD, 40);
    private final Font standardFont = new Font("Sans Serif", Font.PLAIN, 16);
    private final StringBuilder input;
    private final String SAVE_FILE_NAME = "game_save.txt";
    private TETile tile;
    private final int WHITE = 255;
    private final int TIME = 3000;

    public Game() {
        input = new StringBuilder();
    }

    public void play() {
        ter = new TERenderer();
        ter.initialize(width, height, 0, offset);
        renderMenu();
        menuSelect();
        moveAround();
    }

    public TETile[][] getWorld() {
        return world.getWorld();
    }

    public void load(String save) {
        StringBuilder seed = new StringBuilder();
        boolean isSeed = false;
        int pos = 0;
        for (int i = 0; i < save.length(); i++) {
            pos++;
            char key = save.charAt(i);
            if ((key == 'l' || key == 'L') && !isSeed) {
                String file = utils.FileUtils.readFile(SAVE_FILE_NAME);
                load(file);
                break;
            }
            if ((key == 'n' || key == 'N') && !isSeed) {
                isSeed = true;
            }
            if (isSeed && Character.isDigit(key)) {
                seed.append(key);
            }
            if (isSeed && (key == 's' || key == 'S')) {
                break;
            }
        }
        if (!seed.isEmpty()) {
            world = new World(Long.parseLong(seed.toString()));
            tile = world.getWorld()[0][0];
        }
        for (int j = pos; j < save.length(); j++) {
            char key = save.charAt(j);
            if (key == ':' && j < (save.length() - 1) && save.charAt(j + 1) == 'q') {
                save = save.substring(0, j);
                utils.FileUtils.writeFile(SAVE_FILE_NAME, save);
            }
            world.move(key);
        }
        input.append(save);
    }

    public void moveAround() {
        boolean aboutToQuit = false;
        boolean totallyQuit = false;
        boolean inRoom = false;
        while (!totallyQuit) {
            mousePos();
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                input.append(key);
                boolean isWASD = (key == 'w' || key == 'a' || key == 's' || key == 'd');
                boolean isWASDCaps = (key == 'W' || key == 'A' || key == 'S' || key == 'D');
                if (isWASD || isWASDCaps) {
                    world.move(key);
                    renderWorld();
                }
                if (key == ':') {
                    aboutToQuit = true;
                } else if ((key == 'q' || key == 'Q') && aboutToQuit) {
                    totallyQuit = true;
                } else if (aboutToQuit) {
                    aboutToQuit = false;
                }
                if (!inRoom && world.roomStatus()) {
                    inRoom = true;
                    coinsMessage();
                } else if (inRoom && !world.roomStatus()) {
                    inRoom = false;
                }
            }
        }
        quit();
    }

    public void coinsMessage() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setFont(optionsFont);
        StdDraw.setPenColor(WHITE, WHITE, WHITE);
        String foundCoins = "You've found a stash full of coins!";
        StdDraw.text((double) width / 2, (double) height / 2, foundCoins);
        StdDraw.show();
        StdDraw.pause(TIME);
        StdDraw.setFont(standardFont);
        renderWorld();
    }

    public void mousePos() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY() + 2;
        if (x < width && y < height - 2 && y >= 0) {
            TETile tileChange = world.getWorld()[x][y];
            if (tileChange.id() != tile.id()) {
                tile = tileChange;
                renderWorld();
            }
        }
    }

    public void createWorld(long seed) {
        world = new World(seed);
        tile = world.getWorld()[0][0];
        renderWorld();
    }

    public void renderMenu() {
        StdDraw.setFont(titleFont);
        StdDraw.setPenColor(WHITE, WHITE, WHITE);
        String title = "CS61B: The Game";
        StdDraw.text((double) width / 2, (double) 3 * height / 4, title);
        StdDraw.setFont(optionsFont);
        String newGame = "New Game (N)";
        StdDraw.text((double) width / 2, (double) height / 2, newGame);
        String loadGame = "Load Game (L)";
        StdDraw.text((double) width / 2, (double) height / 2 - 3, loadGame);
        String quit = "Quit (Q)";
        StdDraw.text((double) width / 2, (double) height / 2 - 6, quit);
        StdDraw.setFont(standardFont);
        StdDraw.show();
    }

    public void renderWorld() {
        ter.renderFrame(world.getWorld());
        StdDraw.setPenColor(WHITE, WHITE, WHITE);
        StdDraw.setFont(optionsFont);
        String type = tile.description();
        StdDraw.text((double) width / 8 * 7, (double) height - 2, type);
        String numCoins = "Coins: " + world.getCoins();
        StdDraw.text(6, (double) height - 2, numCoins);
        StdDraw.show();
        StdDraw.setFont(standardFont);
    }

    public void menuSelect() {
        boolean selectionMade = false;
        while (!selectionMade) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                input.append(key);
                selectionMade = switch (key) {
                    case 'n', 'N' -> {
                        newWorldFromMenu();
                        yield true;
                    }
                    case 'l', 'L' -> {
                        loadSaved();
                        yield true;
                    }
                    case 'q', 'Q' -> {
                        quit();
                        yield true;
                    }
                    default -> selectionMade;
                };
            }
        }
    }

    public void renderSeedInput() {
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setFont(titleFont);
        StdDraw.setPenColor(WHITE, WHITE, WHITE);
        String inputSeed = "Input Seed Below:";
        StdDraw.text((double) width / 2, (double) 3 * height / 4, inputSeed);
        StdDraw.setFont(optionsFont);
        String currSeed = "Current Seed:";
        StdDraw.text((double) width / 2, (double) height / 2, currSeed);
        String save = "Save (S)";
        StdDraw.text((double) width / 2, (double) height / 2 - 6, save);
        StdDraw.setFont(standardFont);
        StdDraw.show();

    }

    public void seedSelect() {
        boolean selectionMade = false;
        StringBuilder seed = new StringBuilder();
        while (!selectionMade) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                input.append(key);
                boolean isNum = String.valueOf(key).matches("\\d");
                if (isNum) {
                    seed.append(key);
                    renderSeedInput();
                    StdDraw.setFont(optionsFont);
                    StdDraw.text((double) width / 2, (double) height / 2 - 3, seed.toString());
                    StdDraw.show();
                }
                if ((key == 's' || key == 'S') && !seed.isEmpty()) {
                    selectionMade = true;
                }
            }
        }
        StdDraw.setFont(standardFont);
        long longFormSeed = Long.parseLong(seed.toString());
        createWorld(longFormSeed);
    }

    public void newWorldFromMenu() {
        renderSeedInput();
        seedSelect();
        renderWorld();
    }

    public void loadSaved() {
        String file = utils.FileUtils.readFile(SAVE_FILE_NAME);
        input.append(file);
        load(file);
        renderWorld();
    }

    public void quit() {
        String history = input.toString();
        utils.FileUtils.writeFile(SAVE_FILE_NAME, history);
        System.exit(0);
    }
}
