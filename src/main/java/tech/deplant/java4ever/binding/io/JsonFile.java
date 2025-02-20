package tech.deplant.java4ever.binding.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record JsonFile(String filePath) implements Supplier<String>, Consumer<String> {
    @Override
    public String get() {
        try {
            return Files.readString(Paths.get(filePath()))
                    .replaceAll("[\u0000-\u001f]", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void accept(String jsonString) {
        try {
            Files.writeString(Paths.get(filePath()),
                    jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
