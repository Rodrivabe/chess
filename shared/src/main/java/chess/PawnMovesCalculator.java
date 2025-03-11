package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> endPositions = new ArrayList<>();

        int direction;

        if (teamColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
        } else {
            direction = -1;}

        if (canMoveForward(board, position, direction)) {
             addValidPositions(1, position, direction, endPositions);
            if (isFirstMove(position, teamColor) && canMoveForward(board, position, direction*2)) {
                addValidPositions(2, position, direction, endPositions);
            }
        }
        canCaptureEnemy(board, position, direction, teamColor, endPositions);

        destinationIsPromotionAndAddMoves(position, endPositions, teamColor, moves);

        return moves;
    }

    private void destinationIsPromotionAndAddMoves(ChessPosition startPosition, Collection<ChessPosition> endPositions, ChessGame.TeamColor teamColor, Collection<ChessMove> moves) {
        for(ChessPosition endPosition: endPositions){
            int endPositionRow = endPosition.getRow();
            if (endPositionRow == 8 && teamColor == ChessGame.TeamColor.WHITE || endPositionRow == 1 && teamColor == ChessGame.TeamColor.BLACK) {
                ChessPiece.PieceType [] pieceTypes = {
                        ChessPiece.PieceType.QUEEN,
                        ChessPiece.PieceType.BISHOP,
                        ChessPiece.PieceType.KNIGHT,
                        ChessPiece.PieceType.ROOK};
                for(ChessPiece.PieceType pieceType: pieceTypes){
                    moves.add(new ChessMove(startPosition, endPosition, pieceType));
                }
            }
            else{
                moves.add(new ChessMove(startPosition, endPosition, null));
            }
        }


    }

    private void canCaptureEnemy(ChessBoard board, ChessPosition position, int direction, ChessGame.TeamColor teamColor, Collection<ChessPosition> endPositions) {
        int [][] diagonalDirections = {{direction, 1}, {direction, -1}};
        int rowPosition = position.getRow();
        int colPosition = position.getColumn();
        for(int[] diagonalPosition : diagonalDirections){
            int diagonalRow = diagonalPosition[0];
            int diagonalCol =diagonalPosition[1];
            ChessPosition targetPosition = new ChessPosition(rowPosition+diagonalRow, colPosition+diagonalCol);

            if (inBounds(targetPosition.getRow(), targetPosition.getColumn())){
                ChessPiece targetPiece = board.getPiece(targetPosition);
                boolean isEnemy;
                isEnemy = targetPiece != null && targetPiece.getTeamColor() != teamColor;
                if(isEnemy){
                    endPositions.add(targetPosition);
                }
            }
        }
    }


    private boolean isFirstMove(ChessPosition position, ChessGame.TeamColor teamColor) {
        int positionRow = position.getRow();
        return teamColor == ChessGame.TeamColor.BLACK && positionRow == 7 || teamColor == ChessGame.TeamColor.WHITE && positionRow == 2;
    }


    private void addValidPositions(int moveType, ChessPosition position, int direction, Collection<ChessPosition> endPositions ) {
        //move forward
        int rowPosition = position.getRow();
        int positionColumn = position.getColumn();
        ChessPosition chessPosition = new ChessPosition(rowPosition+(direction*moveType), positionColumn);


        endPositions.add(chessPosition);
    }

    private boolean canMoveForward(ChessBoard board, ChessPosition position, int direction) {
        int rowPosition = position.getRow();
        int colPosition = position.getColumn();
        ChessPosition targetPosition = new ChessPosition(rowPosition+direction, colPosition);
        ChessPiece targetPiece = board.getPiece(targetPosition);
        boolean isEmpty;
        isEmpty = targetPiece == null;
        return inBounds(targetPosition.getRow(), targetPosition.getColumn()) && isEmpty;
    }

    boolean inBounds(int row, int col){
        return row < 9 && row > 0 && col > 0 && col < 9;
    }
}