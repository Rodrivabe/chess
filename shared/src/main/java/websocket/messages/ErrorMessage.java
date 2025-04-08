package websocket.messages;

public class ErrorMessage extends ServerMessage extends ServerMessage{

    public private String errorMessage;

    public ErrorMessage(String errorMessage){

        this.errorMessage = errorMessage;
    }

}
