package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    private TeamColor turnColor;


    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.turnColor = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.turnColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        /** This function iterates through every possible move from the
         * piece it's analysing and compares to see if the king will be in danger**/

        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        }

        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validateMoves = new ArrayList<>();

        for (ChessMove move : possibleMoves) {
            ChessPosition target_position = move.getEndPosition();

            ChessPiece target_piece = board.getPiece(target_position);

            board.addPiece(target_position, piece);
            board.addPiece(move.getStartPosition(), null);

            boolean stillInCheck = isInCheck(piece.getTeamColor());
            board.addPiece(startPosition, piece);
            board.addPiece(target_position, target_piece);
            if (!stillInCheck) {
                validateMoves.add(move);
            }
        }
        return validateMoves;


    }


    public void makeMove(ChessMove move) throws InvalidMoveException {
        /**
         * Makes a move in a chess game
         *
         * @param move chess move to preform
         * @throws InvalidMoveException if move is invalid
         */

        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("There is not a piece in there, Bruh!");
        }
        if (turnColor != piece.getTeamColor()) {
            throw new InvalidMoveException("It's not your turn :D");

        }

        if (isInCheck(turnColor)) {
            throw new InvalidMoveException("You are in Check :o ");
        }

        Collection<ChessMove> validated_moves = validMoves(move.getStartPosition());


        if (!validated_moves.contains(move)) {
            throw new InvalidMoveException("That's Illegal! D:");
        }

        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);

        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(turnColor, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), piece);
        }
        if (turnColor == TeamColor.WHITE) {
            turnColor = TeamColor.BLACK;
        } else {
            turnColor = TeamColor.WHITE;
        }

    }


    public boolean isInCheck(TeamColor teamColor) {
        /**
         * Determines if the given team is in check
         *
         * @param teamColor which team to check for check
         * @return True if the specified team is in check
         */


        ChessPosition kingsPosition = findKing(teamColor);
        int direction;
        if (teamColor == TeamColor.WHITE) {
            direction = -1;
        } else {
            direction = 1;
        }

        for (int i = 1; i < 9; i++) {
            for (int k = 1; k < 9; k++) {
                ChessPosition current_position = new ChessPosition(i, k);
                ChessPiece current_piece = board.getPiece(current_position);


                if (current_piece != null && current_piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> current_piece_moves = current_piece.pieceMoves(board, current_position);
                    if (current_piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                        Collection<ChessPosition> end_positions = if_pawn_could_captured(current_position, direction);
                        for (ChessPosition position : end_positions) {
                            if (position == kingsPosition) {
                                return true;
                            }
                        }
                    }
                    for (ChessMove move : current_piece_moves) {

                        if (move.getEndPosition().equals(kingsPosition)) {
                            return true;
                        }
                    }
                }
            }

        }

        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        /** Looks for the king in the board **/

        for (int i = 1; i < 9; i++) {
            for (int k = 1; k < 9; k++) {
                ChessPosition current_position = new ChessPosition(i, k);
                ChessPiece current_piece = board.getPiece(current_position);
                if (current_piece != null && current_piece.getPieceType() == ChessPiece.PieceType.KING && current_piece.getTeamColor() == teamColor) {
                    return current_position;
                }
            }

        }
        return null;

    }

    private Collection<ChessPosition> if_pawn_could_captured(ChessPosition position, int direction) {
        /** Simulates the moves a pawn will do if it could capture **/

        Collection<ChessPosition> endPositions = new ArrayList<>();
        int[][] diagonal_directions = {{direction, 1}, {direction, -1}};
        int row_position = position.getRow();
        int col_position = position.getColumn();
        for (int[] diagonal_position : diagonal_directions) {
            int diagonal_row = diagonal_position[0];
            int diagonal_col = diagonal_position[1];
            ChessPosition target_position = new ChessPosition(row_position + diagonal_row, col_position + diagonal_col);

            if (inBounds(target_position.getRow(), target_position.getColumn())) {
                endPositions.add(target_position);
            }
        }
        return endPositions;
    }


    boolean inBounds(int row, int col) {
        /** Makes sure that calculations are being done inside the limits of the board**/
        return row < 9 && row > 0 && col > 0 && col < 9;
    }


    public boolean isInCheckmate(TeamColor teamColor) {
        /**
         * Determines if the given team is in checkmate
         *
         * @param teamColor which team to check for checkmate
         * @return True if the specified team is in checkmate
         */

        if (!isInCheck(teamColor)) {
            return false;
        }
        return checkMate(teamColor);

    }

    private boolean checkMate(TeamColor teamColor) {
        /** isInCheckmate and isInStalemate use this function to check if the king is surrounded by danger **/

        for (int i = 1; i < 9; i++) {
            for (int k = 1; k < 9; k++) {

                ChessPosition myCurrentPosition = new ChessPosition(i, k);
                ChessPiece myCurrentPiece = board.getPiece(myCurrentPosition);

                if (myCurrentPiece != null && myCurrentPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> myPieceMoves = myCurrentPiece.pieceMoves(board, myCurrentPosition);
                    for (ChessMove move : myPieceMoves) {
                        ChessPosition target_position = move.getEndPosition();

                        ChessPiece target_piece = board.getPiece(target_position);

                        board.addPiece(target_position, myCurrentPiece);
                        board.addPiece(move.getStartPosition(), null);

                        boolean stillInCheck = isInCheck(teamColor);
                        board.addPiece(myCurrentPosition, myCurrentPiece);
                        board.addPiece(target_position, target_piece);
                        if (!stillInCheck) {
                            return false;
                        }
                    }

                }

            }
        }
        return true;
    }


    public boolean isInStalemate(TeamColor teamColor) {
        /**
         * Determines if the given team is in stalemate, which here is defined as having
         * no valid moves
         *
         * @param teamColor which team to check for stalemate
         * @return True if the specified team is in stalemate, otherwise false
         */


        if (isInCheck(teamColor)) {
            return false;
        }

        return checkMate(teamColor);

    }


    public void setBoard(ChessBoard board) {
        /**
         * Sets this game's chessboard with a given board
         *
         * @param board the new board to use
         */


        this.board = board;
    }


    public ChessBoard getBoard() {
        /**
         * Gets the current chessboard
         *
         * @return the chessboard
         */


        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && turnColor == chessGame.turnColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, turnColor);
    }
}


