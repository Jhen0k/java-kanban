package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantTypeAdapter extends TypeAdapter<Instant> {
    @Override
    public void write(JsonWriter out, Instant instant) throws IOException {
        if (instant != null) {
            out.value(instant.toEpochMilli());
        } else {
            out.nullValue();
        }
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        long millisEpoch = in.nextLong();
        if (millisEpoch == -1) {
            return null;
        } else {
            return Instant.ofEpochMilli(millisEpoch);
        }
    }
}
