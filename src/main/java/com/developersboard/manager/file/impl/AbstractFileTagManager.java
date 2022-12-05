package com.developersboard.manager.file.impl;

import com.developersboard.manager.file.TagManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractFileTagManager implements TagManager {

    public final String readFile(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public final void writeFile(String file, String content) throws IOException {
        Path path = Paths.get(file);
        if (!path.startsWith(BASEDIR))
            throw new IOException("Can only write to files in " + BASEDIR);
        Files.write(path, content.getBytes());
    }
}
