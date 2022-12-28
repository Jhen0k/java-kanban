public class SingleTask extends Task{
    private Status status;

    public SingleTask(Integer id, String name, Status status) {
        super(id, name);
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Type getType() {
        return Type.SINGLE;
    }
}
