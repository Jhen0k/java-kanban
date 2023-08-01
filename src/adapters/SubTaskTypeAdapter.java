package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.SubTask;
import tasks.enums.Status;

import java.io.IOException;
import java.time.Instant;

public class SubTaskTypeAdapter extends TypeAdapter<SubTask> {
    @Override
    public void write(JsonWriter out, SubTask task) throws IOException {
        out.beginObject();
        out.name("id").value(task.getId());
        out.name("type").value(task.getType().toString());
        out.name("name").value(task.getName());
        out.name("description").value(task.getDescription());
        out.name("status").value(task.getStatus().toString());
        out.name("epicId").value(task.getEpicId());
        if(task.getStartTime() != null) {
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
    public SubTask read(JsonReader in) throws IOException {
        SubTask subTask = new SubTask();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id" -> subTask.setId(Integer.parseInt(in.nextString()));
                case "name" -> subTask.setName(in.nextString());
                case "type" -> in.nextString();
                case "description" -> subTask.setDescription(in.nextString());
                case "status" -> subTask.setStatus(Status.valueOf(in.nextString()));
                case "epicId" -> subTask.setEpicId(Integer.parseInt(in.nextString()));
                case "startTime" -> subTask.setStartTime(Instant.parse(in.nextString()));
                case "duration" -> subTask.setDuration(Integer.parseInt(in.nextString()));
                case "endTime" ->  subTask.setEndTime(Instant.parse(in.nextString()));
            }
        }
        in.endObject();
        return subTask;
    }
}

