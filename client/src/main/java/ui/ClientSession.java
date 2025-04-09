package ui;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class ClientSession {
    public State state = State.LOGEDOUT;
    public String username = null;
    public String authToken;
    public Integer currentGameId = null;
    public ChessGame.TeamColor playerColor = null;
    public ChessGame game = null;
    public Collection<GameData> lastGameList = new ArrayList<>();
}