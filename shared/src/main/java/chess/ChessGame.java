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
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> valid_moves = piece.pieceMoves(board, startPosition);

        return valid_moves;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null){
            throw new InvalidMoveException("There is not a piece in there, Bruh!");
        }
        if(turnColor != piece.getTeamColor()){
            throw  new InvalidMoveException("It's not your turn :D");

        }
        Collection<ChessMove> validated_moves = validMoves(move.getStartPosition());

        if(isInCheck(turnColor)){
            throw  new InvalidMoveException("You are in Check :o ");
        }

        if(!validated_moves.contains(move)){
            throw  new InvalidMoveException("That's Illegal! D:");
        }

        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);

        if(move.getPromotionPiece() != null){
            piece = new ChessPiece(turnColor, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), piece);
        }
        if(turnColor == TeamColor.WHITE){
            turnColor = TeamColor.BLACK;
        }else{
            turnColor = TeamColor.WHITE;
        }

    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingsPosition = findKing(teamColor);
        int direction;
        if (teamColor == TeamColor.WHITE) {
            direction = -1;
        } else {
            direction = 1;}

        for(int i = 1; i < 9; i++){
            for(int k = 1; k < 9; k++){
                ChessPosition current_position = new ChessPosition(i, k);
                ChessPiece current_piece = board.getPiece(current_position);


                if(current_piece != null && current_piece.getTeamColor() != teamColor){
                    Collection<ChessMove> current_piece_moves = current_piece.pieceMoves(board, current_position);
                    if(current_piece.getPieceType() == ChessPiece.PieceType.PAWN){
                        Collection<ChessPosition> end_positions = if_pawn_could_captured(board, current_position, direction);
                        for(ChessPosition position : end_positions){
                            if(position == kingsPosition){
                                return true;
                            }
                        }
                    }
                    for(ChessMove move : current_piece_moves){

                        if(move.getEndPosition().equals(kingsPosition)){
                            return true;
                        }
                    }
                }
            }

        }

        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for(int i = 1; i < 9; i++){
            for(int k = 1; k < 9; k++){
                ChessPosition current_position = new ChessPosition(i, k);
                ChessPiece current_piece = board.getPiece(current_position);
                if(current_piece != null && current_piece.getPieceType() == ChessPiece.PieceType.KING && current_piece.getTeamColor() == teamColor){
                    return current_position;
                }
            }

        }
        return null;

    }

    private Collection<ChessPosition> if_pawn_could_captured(ChessBoard board, ChessPosition position, int direction) {
        Collection<ChessPosition> endPositions = new ArrayList<>();
        int [][] diagonal_directions = {{direction, 1}, {direction, -1}};
        int row_position = position.getRow();
        int col_position = position.getColumn();
        for(int[] diagonal_position : diagonal_directions){
            int diagonal_row = diagonal_position[0];
            int diagonal_col =diagonal_position[1];
            ChessPosition target_position = new ChessPosition(row_position+diagonal_row, col_position+diagonal_col);

            if (inBounds(target_position.getRow(), target_position.getColumn())){
                endPositions.add(target_position);
            }
        }
        return endPositions;
    }


    boolean inBounds(int row, int col){
        return row < 9 && row > 0 && col > 0 && col < 9;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return false;
        }
        for(int i = 1; i < 9; i++){
            for(int k = 1; k < 9; k++){
                ChessPosition myCurrentPosition = new ChessPosition(i, k);
                ChessPiece myCurrentPiece = board.getPiece(myCurrentPosition);
                if(myCurrentPiece != null && myCurrentPiece.getTeamColor() == teamColor){
                    Collection<ChessMove> myPieceMoves =  myCurrentPiece.pieceMoves(board, myCurrentPosition);

                    for(ChessMove move : myPieceMoves){
                        ChessPosition target_position = move.getEndPosition();

                        ChessPiece target_piece = board.getPiece(target_position);

                        board.addPiece(target_position, myCurrentPiece);
                        board.addPiece(myCurrentPosition, null);

                    }
                }

            }










        ChessPosition kingsPosition = findKing(teamColor);
        ChessPiece myKingPiece = board.getPiece(kingsPosition);
        Collection<ChessMove> kingMoves = myKingPiece.pieceMoves(board, kingsPosition);
        Collection<ChessPosition> allEndPositions = new ArrayList<>();
        for(int i = 1; i < 9; i++){
            for(int k = 1; k < 9; k++){
                ChessPosition current_position = new ChessPosition(i, k);
                ChessPiece current_piece = board.getPiece(current_position);

                if(current_piece != null && current_piece.getTeamColor() != teamColor){
                    Collection<ChessMove> current_piece_moves = current_piece.pieceMoves(board, current_position);

                    for(ChessMove move : current_piece_moves){
                        allEndPositions.add(move.getEndPosition());

                    }
                }
            }

        }
        boolean allInCheck = false;

        for(ChessMove move : kingMoves){
            if (allEndPositions.contains(move.getEndPosition())) {
                allInCheck = true;
            }else {
                allInCheck = false;
                break;
            }
        }

        return allInCheck;

    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
         this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
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


