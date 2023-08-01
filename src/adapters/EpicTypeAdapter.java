package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import tasks.Epic;
import tasks.enums.Status;

import java.io.IOException;
import java.util.ArrayList;

public class EpicTypeAdapter extends TypeAdapter<Epic> {

    @Override
    public void write(JsonWriter out, Epic epic) throws IOException {
        out.beginObject();
        out.name("id").value(epic.getId());
        out.name("type").value(epic.getType().toString());
        out.name("name").value(epic.getName());
        out.name("description").value(epic.getDescription());
        out.name("status").value(epic.getStatus().toString());
        out.endObject();
    }

    @Override
    public Epic read(JsonReader in) throws IOException {
        Epic epic =new Epic();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id" -> epic.setId(Integer.parseInt(in.nextString()));
                case "type" -> in.nextString();
                case "name" -> epic.setName(in.nextString());
                case "description" -> epic.setDescription(in.nextString());
                case "status" -> epic.setStatus(Status.valueOf(in.nextString()));
            }
        }
        epic.setSubtasks(new ArrayList<>());
        in.endObject();
        return epic;
    }
}