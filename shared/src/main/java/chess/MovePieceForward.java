package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MovePieceForward {

    boolean inBounds(int row, int col){
        return row < 9 && row > 0 && col > 0 && col < 9;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor, int[][] directions, ChessPiece.PieceType type) {


        int rowPosition = position.getRow();
        int colPosition = position.getColumn();

        return switch (type) {
            case KING, KNIGHT -> jumpAndEat(rowPosition, colPosition, board, directions, position, teamColor);
            case QUEEN, ROOK, BISHOP -> moveAndEat(rowPosition, colPosition, board, directions, position, teamColor);
            default -> List.of();
        };

    }

    private Collection<ChessMove> moveAndEat(int rowPosition, int colPosition, ChessBoard board, int[][] directions, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();


        ChessPosition targetPosition;
        ChessMove validMove;

        for (int[] direction : directions) {
            for (int i = 1; i < 9; i++) {

                int newRow = rowPosition + (direction[0] * i);
                int newColumn = colPosition + (direction[1] * i);

                if (inBounds(newRow, newColumn)) {
                    targetPosition = new ChessPosition(newRow, newColumn);
                    ChessPiece targetPiece = board.getPiece(targetPosition);

                    if (targetPiece == null) {
                        validMove = new ChessMove(position, targetPosition, null);
                        moves.add(validMove);
                    } else if (targetPiece.getTeamColor() != teamColor) {
                        validMove = new ChessMove(position, targetPosition, null);
                        moves.add(validMove);
                        break;
                    } else if (targetPiece.getTeamColor() == teamColor) {
                        break;
                    } else {
                        break;
                    }
                }

            }

        }
        return moves;

    }

    private Collection<ChessMove> jumpAndEat(int rowPosition, int colPosition, ChessBoard board, int[][] directions, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();

        ChessPosition targetPosition;
        ChessMove validMove;

        for (int[] direction : directions) {
            int newRow = rowPosition + direction[0];
            int newColumn = colPosition + direction[1];

            if (inBounds(newRow, newColumn)) {
                targetPosition = new ChessPosition(newRow, newColumn);
                ChessPiece targetPiece = board.getPiece(targetPosition);

                if (targetPiece == null) {
                    validMove = new ChessMove(position, targetPosition, null);
                    moves.add(validMove);
                } else if (targetPiece.getTeamColor() != teamColor) {
                    validMove = new ChessMove(position, targetPosition, null);
                    moves.add(validMove);
                }
            }
        }
        return moves;
    }
}