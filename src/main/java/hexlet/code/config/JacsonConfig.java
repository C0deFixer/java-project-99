package hexlet.code.config;


import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openapitools.jackson.nullable.JsonNullableModule;

import java.time.format.DateTimeFormatter;

@Configuration
public class JacsonConfig {

    private static final String dateFormat = "yyyy-MM-dd";

    @Bean
    Jackson2ObjectMapperBuilder objectMapperBuilder() {
        var builder = new Jackson2ObjectMapperBuilder();
        builder.simpleDateFormat(dateFormat);
        builder.serializers(new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormat)));
        builder.serializationInclusion(JsonInclude.Include.NON_NULL)
                .modulesToInstall(new JsonNullableModule());
        return builder;
    }

}
