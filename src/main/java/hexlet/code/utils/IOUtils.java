package hexlet.code.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Component
public class IOUtils {

    public String readFileContent(String path) throws IOException {
        InputStream resource = new ClassPathResource(path).getInputStream();
        String resultString;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource))) {
            resultString = reader.lines()
                    .collect(Collectors.joining("\n"));
        }
        return resultString;
    }
}
