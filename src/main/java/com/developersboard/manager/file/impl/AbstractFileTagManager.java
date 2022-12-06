package com.developersboard.manager.file.impl;

import com.developersboard.exception.NoSuchTagException;
import com.developersboard.exception.TagExistsException;
import com.developersboard.manager.file.TagManager;
import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;
import com.developersboard.shared.impl.DefaultTag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public abstract class AbstractFileTagManager implements TagManager {

    /**
     * Internal file and tag for this file tag manager
     */
    protected final Tag tag;
    protected final TaggedFile taggedFile;

    protected AbstractFileTagManager(Tag tag, TaggedFile taggedFile) {
        this.tag = tag;
        this.taggedFile = taggedFile;
    }

    public final String readFile(String file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file)));
    }

    public final void writeFile(String file, String content) throws IOException {
        Path path = Paths.get(file);
        if (!path.startsWith(BASEDIR))
            throw new IOException("Can only write to files in " + BASEDIR);
        Files.write(path, content.getBytes());
    }

    /**
     * Checks for non-existence of a tag to prevent duplication.
     *
     * @param tagName the name of the tag to create and validate.
     * @return the newly created tag
     * @throws TagExistsException if a tag already exists with the provided name
     */
    protected final Tag createUniqueTag(String tagName) throws TagExistsException {
        long stamp = this.taggedFile.getLock().readLock();
        try {
            var tag = new DefaultTag(tagName);
            if (this.taggedFile.getTags().contains(tag)) {
                throw new TagExistsException();
            }

            return tag;
        } finally {
            this.taggedFile.getLock().unlockRead(stamp);
        }
    }

    /**
     * Adds the specified tag to the tags associated with this taggedFile.
     *
     * @param tag the new tag
     */
    protected final void addTagToTaggedFiles(Tag tag) {
        long stamp = this.taggedFile.getLock().writeLock();
        try {
            this.taggedFile.getTags().add(tag);
        } finally {
            this.taggedFile.getLock().unlockWrite(stamp);
        }
    }

    /**
     * Removes a tag from the tags associated with this taggedFile.
     *
     * @param tag the tag to remove
     */
    protected final void removeTagFromFile(String tag) {
       removeTagFromFile(new DefaultTag(tag));
    }

    /**
     * Removes a tag from the tags associated with this taggedFile.
     *
     * @param tag the tag to remove
     */
    protected final void removeTagFromFile(Tag tag) {
        long stamp = this.taggedFile.getLock().writeLock();
        try {
            this.taggedFile.getTags().remove(tag);
        } finally {
            this.taggedFile.getLock().unlockWrite(stamp);
        }
    }

    /**
     * Checks for existence of a tag to prevent duplication.
     *
     * @param tag the name of the tag
     * @return the existing tag
     * @throws NoSuchTagException if no tag exits with the provided name.
     */
    protected final Tag getExistingTag(String tag) throws NoSuchTagException {
        long stamp = this.taggedFile.getLock().readLock();
        try {
            return this.taggedFile.getTags().stream()
                    .filter(new DefaultTag(tag)::equals)
                    .findFirst()
                    .orElseThrow(NoSuchTagException::new);
        } finally {
            this.taggedFile.getLock().unlockRead(stamp);
        }
    }
}
