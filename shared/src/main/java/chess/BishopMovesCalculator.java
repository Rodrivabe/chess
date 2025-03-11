package chess;

import java.util.Collection;

public class BishopMovesCalculator extends MovePieceForward implements PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {

        int[][] directions = {{1, -1}, {1, 1}, {-1, -1}, {-1, 1}};
        return calculateMoves(board, position, teamColor, directions, ChessPiece.PieceType.BISHOP);

    }
}
