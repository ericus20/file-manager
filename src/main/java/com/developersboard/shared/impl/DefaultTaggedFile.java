package com.developersboard.shared.impl;

import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;

@Data
@RequiredArgsConstructor
public class DefaultTaggedFile implements TaggedFile {

    @NonNull private final Path path;
    private final Set<Tag> tags = new HashSet<>();
    private final StampedLock lock = new StampedLock();

    @Override
    public String getName() {
        return this.path.toString();
    }
}
