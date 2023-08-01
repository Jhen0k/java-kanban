package adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Instant;

public  class CustomGson {

    private final Gson gson;
    private static final TaskTypeAdapter taskTypeAdapter = new TaskTypeAdapter();
    private static final EpicTypeAdapter epicTypeAdapter = new EpicTypeAdapter();
    private static final SubTaskTypeAdapter subTaskTypeAdapter = new SubTaskTypeAdapter();
    private static final InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();

    public CustomGson() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, taskTypeAdapter)
                .registerTypeAdapter(Epic.class, epicTypeAdapter)
                .registerTypeAdapter(SubTask.class, subTaskTypeAdapter)
                .registerTypeAdapter(Instant.class, instantTypeAdapter)
                .create();
    }

    public final Gson customGson() {
        return gson;
    }
}

