package ui;

import chess.ChessGame;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class BoardPrint {
    private final Session session;

    public BoardPrint(Session session){
        this.session = session;
    }

    public void printBoard(){

        if(session.playerColor == null || session.playerColor == WHITE){

        }
        else if (session.playerColor == BLACK){

        }
    }
}
