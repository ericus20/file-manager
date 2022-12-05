package com.developersboard.shared;

import java.util.Collection;
import java.util.concurrent.locks.StampedLock;

public interface TaggedFile {

    String getName();

    StampedLock getLock();

    Collection<Tag> getTags();
}
