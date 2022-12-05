package com.developersboard.shared;

import java.util.Collection;
import java.util.concurrent.locks.StampedLock;

public interface Tag {

    String getName();

    StampedLock getLock();

    Collection<TaggedFile> getFiles();
}
