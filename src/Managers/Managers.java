package Managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HttpTaskManager getHttpTaskManager(String serverUrl) {
        HttpTaskManager httpTaskManager = new HttpTaskManager(serverUrl);
        httpTaskManager.loadFromServer();

        return httpTaskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static FileBackedTasksManager loadTaskManagerFromFile(File fileName) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(fileName);
        fileBackedTasksManager.loadFromFile(fileName);

        return fileBackedTasksManager;
    }
    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
    }
}
