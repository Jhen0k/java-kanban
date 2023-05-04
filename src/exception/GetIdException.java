package exception;

public class GetIdException extends Exception{

    public GetIdException() {
        System.out.println("Id генерируется автоматически при создании нового объекта.");
    }
}
