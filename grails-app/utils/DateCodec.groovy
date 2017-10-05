import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateCodec {
    static encode = { theTarget ->
        Date.from(OffsetDateTime.parse(theTarget.toString()).toInstant())
    }

    static decode = { theTarget ->
        OffsetDateTime.ofInstant(((Date) theTarget).toInstant(), ZoneId.of("UTC"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmxxx"))
    }
}
