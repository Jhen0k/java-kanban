package manager.http;

import adapters.CustomGson;
import com.google.gson.*;
import manager.file.FileBackedTasksManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.enums.Type;

public class HttpTaskManager extends FileBackedTasksManager {

    private final Gson gson;
    private final KvsKlient kvsKlient;

    private final String body;

    public HttpTaskManager() {
        kvsKlient = new KvsKlient();
        gson = new CustomGson().customGson();
        body = kvsKlient.loadState("load");
        load();
    }

    @Override
    public void save() {
        String task = gson.toJson(getAllTasks());
        kvsKlient.saveState("save", task);
    }

    public void load() {
        if (body.isEmpty()) {
            System.out.println("Сохраненых задач нет!");
            return;
        }

        JsonElement jsonElement = JsonParser.parseString(body);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = (JsonObject) jsonArray.get(i);
            Type type = Type.valueOf(jsonObject.get("type").getAsString());
            switch (type) {
                case TASK -> addNewTask(gson.fromJson(jsonObject, Task.class));
                case EPIC -> addNewEpicTask(gson.fromJson(jsonObject, Epic.class));
                case SUBTASK -> addNewSubTask(gson.fromJson(jsonObject, SubTask.class));
                default -> System.out.println("Тип задачи не определен.");
            }
        }
    }
}
