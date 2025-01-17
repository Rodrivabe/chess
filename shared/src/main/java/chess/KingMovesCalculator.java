package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator {
    //private ChessPosition KingPosition = new ChessPosition(0,0);

    public ArrayList<ChessMove> get2DArray() {
        ArrayList<ChessMove> moves = new ArrayList<>();
        moves.add(new ChessMove(6, 5), null);
        moves.add(new ChessMove(7, 6));
        moves.add(new ChessMove(8, 7));
        moves.add(new ChessMove(4, 5));
        moves.add(new ChessMove(3, 6));
        moves.add(new ChessMove(2, 7));
        moves.add(new ChessMove(1, 8));
        moves.add(new ChessMove(4, 3));
        moves.add(new ChessMove(3, 2));
        moves.add(new ChessMove(2, 1));
        moves.add(new ChessMove(6, 3));
        moves.add(new ChessMove(7, 2));
        moves.add(new ChessMove(8, 1));
        return moves;

    }
}
