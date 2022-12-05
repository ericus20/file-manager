package com.developersboard.manager.file.impl;

import com.developersboard.exception.NoSuchTagException;
import com.developersboard.exception.TagExistsException;
import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

@Component
public class FileTagManager extends AbstractFileTagManager {

    @Override
    public void init(List<Path> files) {
        // TODO Auto-generated method stub
    }

    @Override
    public Iterable<? extends Tag> listTags() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tag addTag(String name) throws TagExistsException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tag editTag(String oldTagName, String newTagName) throws TagExistsException, NoSuchTagException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Tag deleteTag(String tagName) throws NoSuchTagException, DirectoryNotEmptyException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<? extends TaggedFile> listAllFiles() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterable<? extends TaggedFile> listFilesByTag(String tag) throws NoSuchTagException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean tagFile(String file, String tag) throws NoSuchFileException, NoSuchTagException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean removeTag(String file, String tag) throws NoSuchFileException, NoSuchTagException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterable<? extends Tag> getTags(String file) throws NoSuchFileException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String catAllFiles(String tag) throws NoSuchTagException, IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void echoToAllFiles(String tag, String content) throws NoSuchTagException, IOException {
        // TODO Auto-generated method stub
    }

    @Override
    public long lockFile(String name, boolean forWrite) throws NoSuchFileException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void unLockFile(String name, long stamp, boolean forWrite) throws NoSuchFileException {
        // TODO Auto-generated method stub
    }
}
