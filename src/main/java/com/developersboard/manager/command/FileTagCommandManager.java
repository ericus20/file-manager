package com.developersboard.manager.command;

import com.developersboard.exception.NoSuchTagException;
import com.developersboard.exception.TagExistsException;
import com.developersboard.manager.file.TagManager;
import com.developersboard.manager.file.impl.AbstractFileTagManager;
import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@SuppressWarnings("unused")
public class FileTagCommandManager {

    private static final Logger LOG = LoggerFactory.getLogger(FileTagCommandManager.class);

    private final TagManager tagManager;

    public FileTagCommandManager(TagManager tagManager) {
        this.tagManager = tagManager;

        init(tagManager);
    }

    private static void init(TagManager tagManager) {
        try {
            tagManager.init(listAllFiles());
        } catch (IOException e) {
            LOG.error("Could not initialize tag manager", e);
        }
    }

    public static List<Path> listAllFiles() throws IOException {
        try (var stream = Files.walk(AbstractFileTagManager.BASEDIR)) {
            return stream.filter(Files::isRegularFile).collect(Collectors.toList());
        }
    }

    @ShellMethod("List the files")
    public CharSequence listFiles(final String tag) {
        try {
            var stringBuilder = new StringBuilder();
            for (TaggedFile taggedFile : tagManager.listFilesByTag(tag)) {
                stringBuilder.append(' ').append(taggedFile.getName());
            }

            return stringBuilder.toString();
        } catch (NoSuchTagException ex) {
            LOG.error("Tag {} does not exist", tag, ex);
            return new AttributedString("Error: Tag " + tag + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
    }

    @ShellMethod("List the tags")
    public CharSequence tags() {
        var stringBuilder = new StringBuilder();
        for (Tag tag : tagManager.listTags()) {
            stringBuilder.append(' ').append(tag.getName());
        }

        return stringBuilder.toString();
    }

    @ShellMethod("Add a tag to a file")
    public CharSequence addTag(final String tag) {
        try {
            tagManager.addTag(tag);
        } catch (TagExistsException ex) {
            LOG.error("Tag {} already exist", tag, ex);
            return new AttributedString("Error: Tag " + tag + " exists", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }

        return null;
    }

    @ShellMethod("Edit tag name")
    public CharSequence editTag(String oldTagName, String newTagName) {
        try {
            tagManager.editTag(oldTagName, newTagName);
        } catch (NoSuchTagException ex) {
            LOG.error("Tag {} does not exist", oldTagName, ex);
            return new AttributedString("Error: Tag " + oldTagName + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } catch (TagExistsException ex) {
            LOG.error("Tag {} already exist", newTagName, ex);
            return new AttributedString("Error: Tag " + newTagName + " exists", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
        
        return null;
    }

    @ShellMethod("Delete a tag")
    public CharSequence deleteTag(String tagName) {
        try {
            tagManager.deleteTag(tagName);
        } catch (NoSuchTagException ex) {
            LOG.error("Tag {} does not exist", tagName, ex);
            return new AttributedString("Error: Tag " + tagName + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } catch (DirectoryNotEmptyException e) {
            LOG.error("Attempt to delete a non-empty directory", e);
            return new AttributedString("Error: Tag " + tagName + " is not empty", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
        
        return null;
    }

    @ShellMethod("Add a tag to a file")
    public CharSequence tagFile(String file, String tag) {
        try {
            tagManager.tagFile(file, tag);
        } catch (NoSuchTagException ex) {
            LOG.error("Tag {} does not exist", tag, ex);
            return new AttributedString("Error: Tag " + tag + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } catch (NoSuchFileException ex) {
            LOG.error("File: {} does not exist", file, ex);
            return new AttributedString("Error: File " + file + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
        
        return null;
    }

    @ShellMethod("Remove a tag from a file")
    public CharSequence removeTag(String file, String tag) {
        try {
            if (!tagManager.removeTag(file, tag)) {
                return new AttributedString("Error: Tag " + tag + " does not exist on file " + file, AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
            }
        } catch (NoSuchTagException ex) {
            LOG.error("Tag {} does not exist", tag, ex);
            return new AttributedString("Error: Tag " + tag + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } catch (NoSuchFileException ex) {
            LOG.error("File: {} does not exist", file, ex);
            return new AttributedString("Error: File " + file + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
        return null;
    }

    @ShellMethod("List all tags on a file")
    public CharSequence getTags(String file) {
        try {
            var stringBuilder = new StringBuilder();
            for (Tag tag : tagManager.getTags(file)) {
                stringBuilder.append(' ').append(tag.getName());
            }
            
            return stringBuilder.toString();
        } catch (NoSuchFileException ex) {
            LOG.error("File: {} does not exist", file, ex);
            return new AttributedString("Error: File " + file + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
    }

    @ShellMethod("Cat a file")
    public CharSequence cat(String file) {
        try {
            return tagManager.readFile(file);
        } catch (IOException e) {
            LOG.error("Error reading file: {}", file, e);
        }

        return null;
    }

    @ShellMethod("Echo text into a file")
    public CharSequence echo(String file, String text) {
        try {
            tagManager.writeFile(file, text);
        } catch (IOException e) {
            LOG.error("Error echoing text: {} to file: {}", text, file, e);
            return new AttributedString("Error: " + e.getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }

        return null;
    }

    @ShellMethod("Cat all files matching a tag")
    public CharSequence catAll(String tag) {
        try {
            return tagManager.catAllFiles(tag);
        } catch (NoSuchTagException ex) {
            LOG.error("Tag {} does not exist", tag, ex);
            return new AttributedString("Error: Tag " + tag + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } catch (IOException e) {
            LOG.error("Error catting all files with tag: {}", tag, e);
            return new AttributedString("Error: " + e.getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
    }

    @ShellMethod("Echo text into all files matching a tag")
    public CharSequence echoAll(String tag, String text) {
        try {
            tagManager.echoToAllFiles(tag, text);
            return null;
        } catch (NoSuchTagException ex) {
            LOG.error("Tag {} does not exist", tag, ex);
            return new AttributedString("Error: Tag " + tag + " does not exist", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        } catch (IOException e) {
            LOG.error("Error echoing all files with tag: {} and text: {}", tag, text, e);
            return new AttributedString("Error: " + e.getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
    }
}
