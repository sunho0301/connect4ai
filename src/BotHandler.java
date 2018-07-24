package connect4ai;

import java.util.Scanner;

import connect4ai.BitBoard64;
import connect4ai.Bot;
import connect4ai.Status;

public class BotHandler {

    public static final String SETTINGS = "settings";
    public static final String UPDATE = "update";
    public static final String ACTION = "action";
    public static final String GAME = "game";
    public static final String MOVE = "move";
    public static final int DEFAULT_WIDTH = 7;
    public static final int DEFAULT_HEIGHT = 6;
    public static final int BOT_0_DP_VAL = 8;
    public static final int BOT_1_DP_VAL = 9;

    private Bot bot;
    private Scanner sc;
    private Status status;
    private BitBoard64 board;

    public BotHandler() {
        this.sc = new Scanner(System.in);
        this.board = new BitBoard64(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        this.bot = new Bot(this.board);
        this.status = new Status();
    }
    
    public Bot getBot() {
    	return this.bot;
    }

    // plays the game. assumes the client follows predifined steps: settings, update, and then asks for actions
    public void play() {
        while (sc.hasNextLine()) {
            String cur = sc.nextLine();
            if (cur.length() == 0) continue;
            String[] data = cur.split(" ");
            switch (data[0]) {
                // prioritized ACTION since it is called the most
                case ACTION:
                    if (data[1].equals(MOVE)) {
                        this.bot.makeMove();
                    }
                    break;
                case SETTINGS:
                    parseSettings(data[1], data[2]);
                    break;
                case UPDATE:
                    if (data[1].equals(GAME)) {
                        parseUpdate(data[2], data[3]);
                    }
                    break;
                default:
                    System.err.println("unknown request type");
                    break;
            }

        }
    }


    // handles setting commands by the engine
    private void parseSettings(String type, String val) {
        switch (type) {
            case "timebank":
                this.status.setTimeBank(Integer.parseInt(val));
                break;
            case "time_per_move":
                this.status.setTimePerMove(Integer.parseInt(val));
                break;
            case "player_names":
                String[] players = val.split(",");
                for (String p : players) {
                    this.status.getPlayerNames().add(p);
                }
                break;
            case "your_bot":
                this.status.setBotName(val);
                break;
            case "your_botid":
                this.status.setBotID(Integer.parseInt(val));
                if (val.equals("0")) {
                    this.bot.setDPPos(BOT_0_DP_VAL);
                } else {
                    this.bot.setDPPos(BOT_1_DP_VAL);
                }
                break;
            case "field_width":
                this.status.setWidth(Integer.parseInt(val));
                break;
            case "field_height":
                this.status.setHeight(Integer.parseInt(val));
                break;
            default:
                System.err.println("unknown setting type");
                break;

        }
    }

    // handles the update commands by the engine
    private void parseUpdate(String type, String val) {
        switch (type) {
            case "round":
                int num = Integer.parseInt(val);
                this.status.setRound(num);
                // ensures the board is clear
                if (num == 1) {
                    clearBoard();
                }
                break;
            case "field":
                this.bot.placeDiscToBoard(val);
                break;
            default:
                System.err.println("unknown update type");
                break;
        }
    }

    // clears board and ensures the board has the same dimension
    private void clearBoard() {
        if (this.status.getWidth() != this.board.getWidth() || this.status.getHeight() != this.board.getHeight()) {
            this.board.setWidth(this.status.getWidth());
            this.board.setHeight(this.status.getHeight());            
        }
        this.board.clear();
    }
    
}