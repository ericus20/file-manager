package com.developersboard.shared.impl;

import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;

public class DefaultTaggedFile implements TaggedFile {

    private final Path path;
    private final Set<Tag> tags = new HashSet<>();
    private final StampedLock lock = new StampedLock();

    public DefaultTaggedFile(Path path) {
        this.path = path;
    }

    @Override
    public String getName() {
        return this.path.toString();
    }

    @Override
    public StampedLock getLock() {
        return this.lock;
    }

    @Override
    public Collection<Tag> getTags() {
        return this.tags;
    }
}
