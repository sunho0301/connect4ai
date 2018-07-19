package connect4ai;

import java.util.Set;
import java.util.HashSet;

public class Status {

    private int timeBank;
    private int timePerMove;
    private Set<String> playerNames;
    private String botName;
    private int botID;
    private int width;
    private int height;
    private int round;
    private int moveT;


    public Status() {
        this.playerNames = new HashSet<String>();
    }

    public void setTimeBank(int timeBank) {
        this.timeBank = timeBank;
    }

    public void setTimePerMove(int timePerMove) {
        this.timePerMove = timePerMove;
    } 

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public void setBotID(int botID) {
        this.botID = botID;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public void setMoveT(int moveT) {
        this.moveT = moveT;
    }

    public int getTimeBank() {
        return this.timeBank;
    }

    public int getTimePerMove() {
        return this.timePerMove;
    }

    public Set<String> getPlayerNames() {
        return this.playerNames;
    }

    public String getBotName() {
        return this.botName;
    }

    public int getBotID() {
        return this.botID;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRound() {
        return this.round;
    }

    public int getMoveT() {
        return this.moveT;
    }

}