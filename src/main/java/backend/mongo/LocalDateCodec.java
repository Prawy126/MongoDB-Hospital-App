package backend.mongo;
import org.bson.*;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateCodec implements Codec<LocalDate> {
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    @Override
    public void encode(BsonWriter writer, LocalDate value, EncoderContext encoderContext) {
        writer.writeString(value.format(formatter)); // LocalDate → String
    }

    @Override
    public LocalDate decode(BsonReader reader, DecoderContext decoderContext) {
        String dateString = reader.readString();
        return LocalDate.parse(dateString, formatter); // String → LocalDate
    }

    @Override
    public Class<LocalDate> getEncoderClass() {
        return LocalDate.class;
    }
}