package http.typeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) jsonWriter.nullValue();
        else jsonWriter.value(duration.toSeconds());
    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        if (jsonReader == null) return null;
        return Duration.ofSeconds(Long.parseLong(jsonReader.nextString()));
    }
}
