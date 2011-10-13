package hudson.plugins.lockedfilesreport;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import hudson.plugins.lockedfilesreport.FindFilesInUseCommand;
import hudson.plugins.lockedfilesreport.FindFilesInUseWithLsof;
import hudson.plugins.lockedfilesreport.model.FileUsageDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

public class FindFilesInUseWithLsofTest {
    
    @Test public void assertArgumentsContainFilePath() {
        assertThat( new FindFilesInUseWithLsof().getArguments("/temp/folder").toCommandArray(), is(new String[]{"lsof", "+D", "/temp/folder"}));
    }
    
    @Test public void assertParsingOfTwoEntriesWorks() throws IOException {
        List<FileUsageDetails> list = new FindFilesInUseWithLsof().parseOutput(
                0, 
                new BufferedReader(new InputStreamReader(FindFilesInUseCommand.class.getResourceAsStream("lsof-two-entries.log"))),
                "/tmp/hsperfdata_erik");
        assertThat(list.size(), is (1));
        assertThat(list.get(0).getFileName(), is("/tmp/hsperfdata_erik/13241"));
        assertThat(list.get(0).getFileNameRelativeToWorkspace(), is("13241"));
        assertThat(list.get(0).getProcessName(), is("java"));
        assertThat(list.get(0).getProcessPid(), is("13241"));
        assertThat(list.get(0).getProcessOwner(), is("erik"));
    }
    
    @Test public void assertParsingOfNoFoundEntriesWorks() throws IOException {
        List<FileUsageDetails> list = new FindFilesInUseWithLsof().parseOutput(
                1, 
                new BufferedReader(new InputStreamReader(FindFilesInUseCommand.class.getResourceAsStream("lsof-no-match.log"))),
                "/tmpasdaa");
        assertThat(list.size(), is(0));
    }
    
    @Test public void assertParsingWhenWorkspaceFolderIsLocked() throws IOException {
        List<FileUsageDetails> list = new FindFilesInUseWithLsof().parseOutput(
                0, 
                new BufferedReader(new InputStreamReader(FindFilesInUseCommand.class.getResourceAsStream("lsof-issue-8323.log"))),
                "/tmp");
        assertThat(list.size(), is(2));
        assertThat(list.get(1).getFileName(), is("/tmp/"));
    }
}
