package chess;

import java.util.Collection;

public class KnightMovesCalculator extends MovePieceForward implements PieceMovesCalculator{

    public Collection<ChessMove> pieceMoves(ChessBoard board,  ChessPosition position,  ChessGame.TeamColor teamColor){
        int [][] directions = {{1,-2}, {-1,-2}, {2,-1}, {2,1}, {1, 2}, {-1,2}, {-2,-1}, {-2,1}};
        return calculateMoves(board, position, teamColor, directions, ChessPiece.PieceType.KNIGHT);
    }




}
