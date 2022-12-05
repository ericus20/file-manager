package com.developersboard.shared.impl;

import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;

@Data
@Component
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DefaultTag implements Tag {

    @EqualsAndHashCode.Include
    private String name;

    private final StampedLock lock = new StampedLock();

    private final Set<TaggedFile> files = new HashSet<>();
}
