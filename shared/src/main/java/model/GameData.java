package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public GameData setGameId(int id){
        return new GameData(id, this.whiteUsername, this.blackUsername, this.gameName, this.game);
    }
}
