package hudson.plugins.lockedfilesreport;

import hudson.plugins.lockedfilesreport.model.FileUsageDetails;
import hudson.util.ArgumentListBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindFilesInUseWithLsof extends FindFilesInUseCommand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public ArgumentListBuilder getArguments(String workspacePath) {
        ArgumentListBuilder builder = new ArgumentListBuilder();
        builder.add("lsof");
        builder.add("+D");
        builder.add(workspacePath.replace("\\", "/"));
        return builder;
    }
    
    @Override
    public List<FileUsageDetails> parseOutput(int result, BufferedReader reader, String workspacePath) throws IOException {
        List<FileUsageDetails> list = new ArrayList<FileUsageDetails>();
        if (result == 1) {
            return list;
        }
        Pattern HANDLE_PATTERN = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+" + workspacePath + "(\\S+)");
        String line = reader.readLine();
        line = reader.readLine();
        while (line != null) {
            Matcher matcher = HANDLE_PATTERN.matcher(line);
            if (matcher.matches()) {
                String filename = matcher.group(9);
                list.add(new FileUsageDetails(workspacePath + filename, filename.substring(1), matcher.group(2), matcher.group(1), matcher.group(3)));
            }
            line = reader.readLine();
        }
        return list;
    }

}
