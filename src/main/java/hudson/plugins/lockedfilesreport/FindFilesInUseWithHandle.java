package hudson.plugins.lockedfilesreport;

import hudson.plugins.lockedfilesreport.model.FileUsageDetails;
import hudson.util.ArgumentListBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindFilesInUseWithHandle extends FindFilesInUseCommand {

    private static final Pattern HANDLE_PATTERN = Pattern.compile("(\\S+)\\s+pid: (\\d+)\\s+(\\S+)\\s+(\\S*):\\s+(.*)");
    private final String exec;

    public FindFilesInUseWithHandle(String exec) {
        this.exec = exec;
    }
    public FindFilesInUseWithHandle() {
        this("handle.exe");
    }

    @Override
    public List<FileUsageDetails> parseOutput(int result, BufferedReader reader, String workspacePath) throws IOException {
        List<FileUsageDetails> list = new ArrayList<FileUsageDetails>();
        if (result == 1) {
            return list;
        }
        String line = reader.readLine();
        while (line != null) {
            Matcher matcher = HANDLE_PATTERN.matcher(line);
            if (matcher.matches()) {
                list.add(new FileUsageDetails(matcher.group(5), 
                        matcher.group(5).substring(workspacePath.length() + 1), 
                        matcher.group(2), 
                        matcher.group(1), 
                        matcher.group(3)));
            }
            line = reader.readLine();
        }
        return list;
    }

    @Override
    public ArgumentListBuilder getArguments(String workspacePath) {
        ArgumentListBuilder builder = new ArgumentListBuilder();
        builder.add(exec);
        builder.add("-u");
        builder.add(workspacePath.replace("/", "\\"));
        return builder;
    }
}
