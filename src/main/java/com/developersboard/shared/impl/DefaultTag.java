package com.developersboard.shared.impl;

import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;

@Component
public class DefaultTag implements Tag {

    private String name;

    private final StampedLock lock = new StampedLock();

    private final Set<TaggedFile> files = new HashSet<>();

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public StampedLock getLock() {
        return this.lock;
    }

    @Override
    public Collection<TaggedFile> getFiles() {
        return this.files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (DefaultTag) o;

        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return "DefaultTag{" +
                "name='" + name + '\'' +
                '}';
    }
}
