package http.typeAdapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import util.ManagerUtil;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) jsonWriter.nullValue();
        else jsonWriter.value(localDateTime.format(ManagerUtil.FORMATTER));
    }

    @Override
    public LocalDateTime read(JsonReader jsonReader) throws IOException {
        if (jsonReader == null) return null;
        return LocalDateTime.parse(jsonReader.nextString(), ManagerUtil.FORMATTER);
    }
}
