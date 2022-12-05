package com.developersboard.manager.file;

import com.developersboard.exception.NoSuchTagException;
import com.developersboard.exception.TagExistsException;
import com.developersboard.manager.command.FileTagCommandManager;
import com.developersboard.manager.file.impl.FileTagManager;
import com.developersboard.shared.Tag;
import com.developersboard.shared.TaggedFile;
import org.junit.jupiter.api.*;

import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;


class TagManagerNonConcurrentTest {

    public static final String DEFAULT_TAG_NAME = "untagged";
    // cut = class under test
    private TagManager cut;

    @BeforeEach
    @Timeout(value = 8000, unit = TimeUnit.MILLISECONDS)
        // 8 seconds
    void setUp() {
        cut = new FileTagManager();
    }

    @Test
    void shouldAddTagSuccessfully(TestInfo testInfo) throws Exception {
        // given
        var tagName = testInfo.getDisplayName();

        // when
        var addedTag = cut.addTag(tagName);

        // then
        Assertions.assertNotNull(addedTag);
    }

    @Test
    void shouldListAddedTagSuccessfully(TestInfo testInfo) throws Exception {
        // given
        var tagName = testInfo.getDisplayName();
        cut.addTag(tagName);
        var foundTag = false;

        // when
        var tags = cut.listTags();

        // then
        for (Tag tag : tags) {
            if (tag.getName().equals(tagName)) {
                foundTag = true;
            } else if (!tag.getName().equals(DEFAULT_TAG_NAME)) {
                Assertions.fail("Unexpected tag found: " + tag);
            }
        }

        Assertions.assertTrue(foundTag, "Tag: '" + tagName + "' already exists");
    }

    @Test
    void shouldThrowExceptionWhenAddingExistingTag(TestInfo testInfo) throws Exception {
        var tagName = testInfo.getDisplayName();
        cut.addTag(tagName); // make sure tag already exists

        Assertions.assertThrows(TagExistsException.class, () -> cut.addTag(tagName), "Expected an exception to be thrown");
    }

    @Test
    void shouldEditExistingTagSuccessfully(TestInfo testInfo) throws Exception {
        // given
        var tagName = testInfo.getDisplayName();
        var newTagName = tagName.repeat(1);
        cut.addTag(tagName);

        // when
        cut.editTag(tagName, newTagName);

        // then
        var res = cut.listTags();
        var foundNewTag = false;

        for (Tag tag : res) {
            if (tag.getName().equals(newTagName)) {
                foundNewTag = true;
            } else if (!tag.getName().equals(DEFAULT_TAG_NAME)) {
                Assertions.fail("Unexpected tag found: " + tag);
            }
        }

        Assertions.assertTrue(foundNewTag, "Tag: '" + newTagName + "' already exists");
    }

    @Test
    void shouldThrowExceptionOnEditingNonExistingTag(TestInfo testInfo) {
        // given
        var tagName = testInfo.getDisplayName();
        var newTagName = tagName.repeat(1);
        
        Assertions.assertThrows(NoSuchTagException.class, () -> cut.editTag(tagName, newTagName));
    }

    @Test
    void testP1EditTagAlreadyExists(TestInfo testInfo) throws Exception {
        // given
        var tagName = testInfo.getDisplayName();
        var newTagName = tagName.repeat(1);
        cut.addTag(tagName);
        cut.addTag(newTagName);

        var exception = false;
        try {
            cut.editTag(tagName, newTagName);
        } catch (TagExistsException ex) {
            exception = true;
        }

        Assertions.assertTrue(exception, "Expected an exception to be thrown");
    }

    @Test
    void shouldDeleteTagSuccessfully(TestInfo testInfo) throws Exception {
        // given
        var tagName = testInfo.getDisplayName();
        cut.addTag(tagName);

        // when
        cut.deleteTag(tagName);

        // then
        Iterable<? extends Tag> tags = cut.listTags();
        Iterator<? extends Tag> iterator = tags.iterator();

        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    void shouldThrowExceptionOnDeletingTagThatDoesntExist(TestInfo testInfo) {
        Assertions.assertThrows(NoSuchTagException.class, () -> cut.deleteTag(testInfo.getDisplayName()));
    }

    @Test
    public void shouldNotBeAbleToDeleteTagNotEmpty(TestInfo testInfo) throws Exception {
        // given
        var fileName = testInfo.getDisplayName();
        var tagName = testInfo.getDisplayName().repeat(1);
        cut.init(Collections.singletonList(Paths.get(fileName)));
        cut.addTag(tagName);
        cut.tagFile(fileName, tagName);
        var caught = false;

        try {
            cut.deleteTag(tagName);
        } catch (DirectoryNotEmptyException ex) {
            caught = true;
        }
        var found =false;
        for(Tag tag: cut.listTags()) {
            if (tag.getName().equals(tagName)) {
                found = true;
            }
        }

        Assertions.assertTrue(caught, "DirectoryNotEmptyException expected");
        Assertions.assertTrue(found, "Not empty tag should not have been deleted");
    }

    @Test
    void shouldInitSuccessfully(TestInfo testInfo) throws Exception {
        // given
        var fileName = testInfo.getDisplayName();
        cut.init(Collections.singletonList(Paths.get(fileName)));

        Iterable<? extends TaggedFile> files = cut.listFilesByTag(DEFAULT_TAG_NAME);
        Iterator<? extends TaggedFile> iter = files.iterator();
        TaggedFile file = iter.next();
        Assertions.assertEquals(file.getName(), fileName);
        Assertions.assertFalse(iter.hasNext());

        Iterator<? extends Tag> tags = cut.getTags(fileName).iterator();
        Assertions.assertEquals(DEFAULT_TAG_NAME, tags.next().getName());
        Assertions.assertFalse(tags.hasNext());
    }

    @Test
    void shouldTagFileSuccessfully(TestInfo testInfo) throws Exception {
        // given
        var fileName = testInfo.getDisplayName();
        var tagName = testInfo.getDisplayName().repeat(1);
        cut.init(Collections.singletonList(Paths.get(fileName)));
        cut.addTag(tagName);

        // when
        cut.tagFile(fileName, tagName);

        // then
        Iterator<? extends Tag> tags = cut.getTags(fileName).iterator();
        Assertions.assertEquals(tagName, tags.next().getName());
        Assertions.assertFalse(tags.hasNext());

        Iterable<? extends TaggedFile> files = cut.listFilesByTag(tagName);
        Iterator<? extends TaggedFile> iter = files.iterator();
        TaggedFile file = iter.next();

        Assertions.assertEquals(file.getName(), fileName);
        Assertions.assertFalse(iter.hasNext());
    }

    @Test
    void shouldNotTagFileWhenItIsAlreadyTagged(TestInfo testInfo) throws Exception {
        // given
        var fileName = testInfo.getDisplayName();
        var tagName = testInfo.getDisplayName().repeat(1);
        cut.init(Collections.singletonList(Paths.get(fileName)));
        cut.addTag(tagName);
        cut.tagFile(fileName, tagName);

        Assertions.assertFalse(cut.tagFile(fileName, tagName));
    }

    @Test
    void shouldThrowExceptionOnEditTagWhenTagDoesNotExist(TestInfo testInfo) throws Exception {
        Assertions.assertThrows(NoSuchTagException.class,
                () -> cut.editTag(testInfo.getDisplayName(), testInfo.getDisplayName()));
    }

    @Test
    void shouldRemoveTagSuccessfully(TestInfo testInfo) throws Exception {
        // given
        var file = testInfo.getDisplayName();
        var tagName = testInfo.getDisplayName().repeat(1);
        cut.init(Collections.singletonList(Paths.get(file)));
        cut.addTag(tagName);
        cut.tagFile(file, tagName);

        // when
        cut.removeTag(file, tagName);

        // then
        Iterator<? extends Tag> tags = cut.getTags(file).iterator();

        Assertions.assertAll(() -> {
            Assertions.assertEquals(DEFAULT_TAG_NAME, tags.next().getName());
            Assertions.assertFalse(tags.hasNext());
        });
    }

    @Test
    void shouldNotBeAbleToRemoveDefaultUntagged(TestInfo testInfo) throws Exception {
        // given
        var file = testInfo.getDisplayName();
        var tagName = testInfo.getDisplayName().repeat(1);
        cut.init(Collections.singletonList(Paths.get(file)));

        // when
        var status = cut.removeTag(file, tagName);

        // then
        Assertions.assertFalse(status, "Removing the untagged tag should be impossible");
    }

    @Test
    void shouldThrowExceptionOnRemoveTagWhenTagDoesNotExist(TestInfo testInfo) {
        // given
        var file = testInfo.getDisplayName();
        cut.init(Collections.singletonList(Paths.get(file)));

        Assertions.assertThrows(NoSuchTagException.class,
                () -> cut.removeTag(file, testInfo.getDisplayName()));
    }

    @Test
    void shouldThrowExceptionOnRemoveTagWhenFileDoesNotExist(TestInfo testInfo) throws Exception {
        // given
        var file = testInfo.getDisplayName();
        var tagName = testInfo.getDisplayName().repeat(1);
        cut.init(Collections.singletonList(Paths.get(file)));
        cut.addTag(tagName);

        Assertions.assertThrows(NoSuchFileException.class,
                () -> cut.removeTag(file, tagName));
    }

    @Test
    void shouldCatToAllExistingTagsSuccessfully() throws Exception {
        cut.init(FileTagCommandManager.listAllFiles());
        cut.catAllFiles(DEFAULT_TAG_NAME);
    }

    @Test
    void shouldThrowExceptionOnCatAllWhenTagDoesNotExist(TestInfo testInfo) throws Exception {
        cut.init(FileTagCommandManager.listAllFiles());

        Assertions.assertThrows(NoSuchTagException.class,
                () -> cut.catAllFiles(testInfo.getDisplayName()));
    }

    @Test
    void shouldEchoToAllExistingTagsSuccessfully(TestInfo testInfo) throws Exception {
        cut.init(FileTagCommandManager.listAllFiles());
        cut.echoToAllFiles(DEFAULT_TAG_NAME, testInfo.getDisplayName());
    }

    @Test
    void shouldThrowExceptionOnEchoAllWhenTagDoesNotExist(TestInfo testInfo) throws Exception {
        cut.init(FileTagCommandManager.listAllFiles());

        Assertions.assertThrows(NoSuchTagException.class,
                () -> cut.echoToAllFiles(testInfo.getDisplayName(), testInfo.getDisplayName()));
    }
}