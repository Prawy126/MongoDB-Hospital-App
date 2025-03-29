package backend.mongo;

import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Klasa LocalDateCodec implementuje Codec dla typu LocalDate.
 * Umożliwia kodowanie i dekodowanie obiektów LocalDate do formatu BSON.
 */
public class LocalDateCodec implements Codec<LocalDate> {
    @Override
    public void encode(BsonWriter writer, LocalDate value, EncoderContext encoderContext) {
        writer.writeDateTime(value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Override
    public LocalDate decode(BsonReader reader, DecoderContext decoderContext) {
        if (reader.getCurrentBsonType() == BsonType.STRING) {
            String dateString = reader.readString();
            return LocalDate.parse(dateString);
        } else {
            long dateTime = reader.readDateTime();
            return Instant.ofEpochMilli(dateTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
    }

    @Override
    public Class<LocalDate> getEncoderClass() {
        return LocalDate.class;
    }
}