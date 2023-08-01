package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Task;
import tasks.enums.Status;

import java.io.IOException;
import java.time.Instant;

public class TaskTypeAdapter extends TypeAdapter<Task> {


    @Override
    public void write(JsonWriter out, Task task) throws IOException {
        out.beginObject();
        out.name("id").value(task.getId());
        out.name("type").value(task.getType().toString());
        out.name("name").value(task.getName());
        out.name("description").value(task.getDescription());
        out.name("status").value(task.getStatus().toString());
        if (task.getStartTime() != null) {
            out.name("startTime").value(task.getStartTime().toString());
        }
        if (task.getDuration() != 0) {
            out.name("duration").value(task.getDuration());
        }
        if (task.getEndTime() != null) {
            out.name("endTime").value(task.getEndTime().toString());
        }
        out.endObject();
    }

    @Override
    public Task read(JsonReader in) throws IOException {
        Task task = new Task();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id" -> task.setId(Integer.parseInt(in.nextString()));
                case "type" -> in.nextString();
                case "name" -> task.setName(in.nextString());
                case "description" -> task.setDescription(in.nextString());
                case "status" -> task.setStatus(Status.valueOf(in.nextString()));
                case "startTime" -> task.setStartTime(Instant.parse(in.nextString()));
                case "duration" -> task.setDuration(Integer.parseInt(in.nextString()));
                case "endTime" -> task.setEndTime(Instant.parse(in.nextString()));
            }
        }
        in.endObject();
        return task;
    }
}
