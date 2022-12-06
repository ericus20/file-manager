package com.developersboard.manager.file;

import com.developersboard.exception.NoSuchTagException;
import com.developersboard.exception.TagExistsException;
import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface TagManager {

    Path BASEDIR = Paths.get(System.getProperty("user.dir"), "sampleDir");

    String DEFAULT_TAG_NAME = "untagged";

    /**
     * Initialize your FileTagManager with the starting set of files. You do not
     * need to persist tags on files from run to run. Each file should start
     * with the default tag "untagged"
     *
     * @param files Starting list of files to consider
     */
    void init(List<Path> files);

    /**
     * List all currently known tags.
     *
     * @return List of tags (in any order)
     */
    Iterable<? extends Tag> listTags();

    /**
     * Add a new tag to the list of known tags
     *
     * @param name Name of tag
     * @return The newly created Tag object
     * @throws TagExistsException If a tag already exists with this name
     */
    Tag addTag(String name) throws TagExistsException;

    /**
     * Update the name of a tag, also updating any references to that tag to
     * point to the new one
     *
     * @param oldTagName Old name of tag
     * @param newTagName New name of tag
     * @return The newly updated Tag object
     * @throws TagExistsException If a tag already exists with the newly requested name
     * @throws NoSuchTagException If no tag exists with the old name
     */
    Tag editTag(String oldTagName, String newTagName) throws TagExistsException, NoSuchTagException;

    /**
     * Delete a tag by name
     *
     * @param tagName Name of tag to delete
     * @return The Tag object representing the tag that was deleted
     * @throws NoSuchTagException         If no tag exists with that name
     * @throws DirectoryNotEmptyException If tag currently has files still associated with it
     */
    Tag deleteTag(String tagName) throws NoSuchTagException, DirectoryNotEmptyException;

    /**
     * List all files, regardless of their tag
     *
     * @return A list of all files. Each file must appear exactly once in this
     * list.
     */
    Iterable<? extends TaggedFile> listAllFiles();

    /**
     * List all files that have a given tag
     *
     * @param tag Tag to look for
     * @return A list of all files that have been labeled with the specified tag
     * @throws NoSuchTagException If no tag exists with that name
     */
    Iterable<? extends TaggedFile> listFilesByTag(String tag) throws NoSuchTagException;

    /**
     * Label a file with a tag
     * <p>
     * Files can have any number of tags - this tag will simply be appended to
     * the collection of tags that the file has. However, files can be tagged
     * with a given tag exactly once: repeatedly tagging a file with the same
     * tag should return "false" on subsequent calls.
     * <p>
     * If the file currently has the special tag "untagged" then that tag should
     * be removed - otherwise, this tag is appended to the collection of tags on
     * this file.
     *
     * @param file Path to file to tag
     * @param tag  The desired tag
     * @return true if succeeding tagging the file, false if the file already
     * has that tag
     * @throws NoSuchFileException If no file exists with the given name/path
     * @throws NoSuchTagException  If no tag exists with the given name
     */
    boolean tagFile(String file, String tag) throws NoSuchFileException, NoSuchTagException;

    /**
     * Remove a tag from a file
     * <p>
     * If removing this tag causes the file to no longer have any tags, then the
     * special "untagged" tag should be added.
     * <p>
     * The "untagged" tag can not be removed (return should be false)
     *
     * @param file Path to file to untag
     * @param tag  The desired tag to remove from that file
     * @return True if the tag was successfully removed, false if there was no
     * tag by that name on the specified file
     * @throws NoSuchFileException If no file exists with the given name/path
     * @throws NoSuchTagException  If no tag exists with the given name
     */
    boolean removeTag(String file, String tag) throws NoSuchFileException, NoSuchTagException;

    /**
     * List all the tags that are applied to a file
     *
     * @param file The file to inspect
     * @return A list of all tags that have been applied to that file in any
     * order
     * @throws NoSuchFileException If the file specified does not exist
     */
    Iterable<? extends Tag> getTags(String file) throws NoSuchFileException;

    /**
     * Prints out all files that have a given tqg. Must internally synchronize
     * to guarantee that the list of files with the given tag does not change
     * during its call, and that each file printed does not change during its
     * execution (using a read/write lock). You should acquire all the locks,
     * then read all the files and release the locks. Your code should not
     * deadlock while waiting to acquire locks.
     *
     * @param tag Tag to query for
     * @return The concatenation of all the files
     * @throws NoSuchTagException If no tag exists with the given name
     * @throws IOException        if any IOException occurs in the underlying write
     */
    String catAllFiles(String tag) throws NoSuchTagException, IOException;

    /**
     * Echos some content into all files that have a given tag. Must internally
     * synchronize to guarantee that the list of files with the given tag does
     * not change during its call, and that each file being printed to do not
     * change during its execution (using a read/write lock)
     * <p>
     * Given two concurrent calls to echoToAllFiles, it will be indeterminite
     * which call happens first and which happens last. But what you can (and
     * must) guarantee is that all files will have the *same* value (and not
     * some the result of the first, qnd some the result of the second). Your
     * code should not deadlock while waiting to acquire locks.
     *
     * @param tag     Tag to query for
     * @param content The content to write out to each file
     * @throws NoSuchTagException If no tag exists with the given name
     * @throws IOException        if any IOException occurs in the underlying write
     */
    void echoToAllFiles(String tag, String content) throws NoSuchTagException, IOException;

    /**
     * Acquires a read or write lock for a given file.
     *
     * @param name     File to lock
     * @param forWrite True if write lock is requested, else false
     * @return A stamp representing the lock owner (e.g. from a StampedLock)
     * @throws NoSuchFileException If the file doesn't exist
     */
    long lockFile(String name, boolean forWrite) throws NoSuchFileException;

    /**
     * Releases a read or write lock for a given file.
     *
     * @param name     File to lock
     * @param stamp    the Stamp representing the lock owner (returned from lockFile)
     * @param forWrite True if write lock is requested, else false
     * @throws NoSuchFileException If the file doesn't exist
     */
    void unLockFile(String name, long stamp, boolean forWrite) throws NoSuchFileException;

    /**
     * Return a file as a byte array.
     *
     * @param file Path to file requested
     * @return String representing the file
     * @throws IOException if any IOException occurs in the underlying read
     */
    String readFile(String file) throws IOException;

    /**
     * Write (or overwrite) a file
     *
     * @param file    Path to file to write out
     * @param content String representing the content desired
     * @throws IOException if any IOException occurs in the underlying write
     */
    void writeFile(String file, String content) throws IOException;
}
