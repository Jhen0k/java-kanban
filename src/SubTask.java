public class SubTask extends Task{
    private Status status;
    private Epic epic;
    public SubTask(Integer id, String name, Status status, Epic epic) {
        super(id, name);
        this.status = status;
        this.epic = epic;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.SUB;
    }
}
