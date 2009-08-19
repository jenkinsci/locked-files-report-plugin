package hudson.plugins.lockedfilesreport;

import java.util.List;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.plugins.lockedfilesreport.model.FileUsageDetails;

@ExportedBean(defaultVisibility = 999)
public class LockedFilesReportAction implements Action {

    private final List<FileUsageDetails> files;
    private final AbstractBuild<?,?> build;

    public LockedFilesReportAction(AbstractBuild<?,?> build, List<FileUsageDetails> files) {
        this.build = build;
        this.files = files;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public String getDisplayName() {
        return "Locked files report";
    }

    public String getIconFileName() {
        return "/plugin/locked-files-report/icons/emblem-readonly-22x22.png";
    }

    public String getUrlName() {
        return "locked-files-report";
    }

    @Exported
    public List<FileUsageDetails> getFiles() {
        return files;
    }

    @Exported
    public int getFilesCount() {
        return files.size();
    }
}
