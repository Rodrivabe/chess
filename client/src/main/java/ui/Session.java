package ui;

import chess.ChessGame;

public class Session {
    public State state = State.LOGEDOUT;
    public String username = null;
    public String authToken;
    public Integer currentGameId = null;
    public ChessGame.TeamColor playerColor = null;
}