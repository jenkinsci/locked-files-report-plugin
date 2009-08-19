package hudson.plugins.lockedfilesreport.model;

import java.io.Serializable;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

@ExportedBean(defaultVisibility=999)
public class FileUsageDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private final String filename;
    private final String processName;
    private final String processPid;
    private final String processOwner;
    private final String filenameRelativeToWorkspace;
    
    public FileUsageDetails(String filename, String filenameRelativeToWorkspace, String processPid, String processName, String processOwner) {
        this.filename = filename;
        this.processPid = processPid;
        this.processName = processName;
        this.processOwner = processOwner;
        this.filenameRelativeToWorkspace =  filenameRelativeToWorkspace;
    }

    @Exported
    public String getFileName() {
        return filename;
    }

    @Exported
    public String getProcessName() {
        return processName;
    }

    @Exported
    public String getProcessPid() {
        return processPid;
    }

    @Exported
    public String getProcessOwner() {
        return processOwner;
    }

    @Exported
    public String getFileNameRelativeToWorkspace() {
        return filenameRelativeToWorkspace;
    }
}
