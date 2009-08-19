package hudson.plugins.lockedfilesreport;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import hudson.plugins.lockedfilesreport.FindFilesInUseWithHandle;
import hudson.plugins.lockedfilesreport.model.FileUsageDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

public class FindFilesInUseWithHandleTest {
    
    @Test public void assertArgumentsContainFilePath() {
        assertThat( new FindFilesInUseWithHandle().getArguments("c:/temp/folder").toCommandArray(), is(new String[]{"handle.exe", "-u", "c:\\temp\\folder"}));
    }
    
    @Test public void assertParsingOfTwoEntriesWorks() throws IOException {
        List<FileUsageDetails> list = new FindFilesInUseWithHandle().parseOutput(
                0, 
                new BufferedReader(new InputStreamReader(FindFilesInUseWithHandleTest.class.getResourceAsStream("handle-two-entries.log"))),
                "C:\\Projects\\hudson\\trunk\\hudson");
        assertThat(list.size(), is (2));
        assertThat(list.get(0).getFileName(), is("C:\\Projects\\hudson\\trunk\\hudson\\plugins\\file-usage-report"));
        assertThat(list.get(0).getFileNameRelativeToWorkspace(), is("plugins\\file-usage-report"));
        assertThat(list.get(0).getProcessName(), is("cmd.exe"));
        assertThat(list.get(0).getProcessPid(), is("10784"));
        assertThat(list.get(0).getProcessOwner(), is("HIQ\\erikra"));
        assertThat(list.get(1).getFileName(), is("C:\\Projects\\hudson\\eclipse-workspace\\.metadata\\.lock"));
        assertThat(list.get(1).getProcessName(), is("eclipse.exe"));
        assertThat(list.get(1).getProcessPid(), is("8368"));
        assertThat(list.get(1).getProcessOwner(), is("HIQ\\erikra"));
    }
    
    @Test public void assertParsingOfNoFoundEntriesWorks() throws IOException {
        List<FileUsageDetails> list = new FindFilesInUseWithHandle().parseOutput(
                1, 
                new BufferedReader(new InputStreamReader(FindFilesInUseWithHandleTest.class.getResourceAsStream("handle-no-match.log"))),
                "C:\\Projects\\hudson\\");
        assertThat(list.size(), is(0));
    }
    
    @Test public void assertParsingNoLongerThrowsOutOfBoundsException() throws IOException {
        List<FileUsageDetails> list = new FindFilesInUseWithHandle("C:\\Projects\\hudson\\").parseOutput(
                0, 
                new BufferedReader(new InputStreamReader(FindFilesInUseWithHandleTest.class.getResourceAsStream("handle-outofbounds.log"))),
                "C:\\Projects\\hudson\\");
        assertThat(list.size(), is(122));
    }
}
