package chess;

import java.util.Collection;

public class RookMovesCalculator extends MovePieceForward implements PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor){
        int [][] directions = {{0,-1}, {1,0}, {0,1}, {-1,0}};
        return calculateMoves(board, position, teamColor, directions, ChessPiece.PieceType.ROOK);
    }
}
