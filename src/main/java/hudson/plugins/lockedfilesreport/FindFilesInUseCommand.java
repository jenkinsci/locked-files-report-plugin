package hudson.plugins.lockedfilesreport;

import hudson.plugins.lockedfilesreport.model.FileUsageDetails;
import hudson.util.ArgumentListBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public abstract class FindFilesInUseCommand {

    /**
     * Return an argument builder containing the necessaary arguments for the command
     * @param workspacePath path to workspace with OS dependent file chars
     * @return an argument builder 
     */
    public abstract ArgumentListBuilder getArguments(String workspacePath);

    /**
     * Parses the output from the command.
     * @param result result code from command
     * @param commandOutput stream containing the command output
     * @param workspacePath path to workspace with OS dependent file chars 
     * @return list of locked files; empty if none found.
     * @throws IOException thrown if there was any problems with the stream 
     */
    public abstract List<FileUsageDetails> parseOutput(int result, BufferedReader commandOutput, String workspacePath) throws IOException;
}
