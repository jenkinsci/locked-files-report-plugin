package hudson.plugins.lockedfilesreport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.FilePath.FileCallable;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.TopLevelItem;
import hudson.plugins.lockedfilesreport.model.FileUsageDetails;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

public class LockedFilesReporter extends Recorder implements Serializable {

    private static final long serialVersionUID = 1L;

    @DataBoundConstructor
    public LockedFilesReporter() {
        super();
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        return checkForFileUsage(build, listener, launcher);
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        return checkForFileUsage(build, listener, build.getBuiltOn().createLauncher(listener));
    }

    @Override
    public LockedFilesReporter.DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    
    private boolean checkForFileUsage(AbstractBuild<?, ?> build, BuildListener listener, Launcher launcher) {
        if (build.getResult().isWorseOrEqualTo(Result.FAILURE) ||  build.getAction(LockedFilesReportAction.class) != null) {
            return true;
        }
        
        FindFilesInUseCommand command;
        if (launcher.isUnix()) {
            command = new FindFilesInUseWithLsof();                
        } else {
            command = new FindFilesInUseWithHandle(getDescriptor().getHandleExecutable());
        }

        try {
            listener.getLogger().println("Searching for locked files in workspace.");
            FilePath workspace = build.getBuiltOn().getWorkspaceFor((TopLevelItem) build.getProject());
            List<FileUsageDetails> list = workspace.act(new GetUsedFiles(command, launcher));
            if (list.size() > 0) {
                build.getActions().add(new LockedFilesReportAction(build, list));
                listener.error("Build was failed as the workspace contained files that were locked by another process. See File usage report for more information.");
                build.setResult(Result.FAILURE);
            }
            
            return (list.size() == 0);          
        } catch (IOException e) {
          listener.error("There was an IOException while launching a process. Please report it to the Hudson user mailing list.");
          listener.error(e.getMessage());
      } catch (InterruptedException e) {
          listener.error("There was an InterruptedException while running a process. Please report it to the Hudson user mailing list.");
          listener.error(e.getMessage());
      }
      return false;
    }

    static class GetUsedFiles implements FileCallable<List<FileUsageDetails>> {

        private static final long serialVersionUID = 1L;
        private final FindFilesInUseCommand command;
        private final Launcher launcher;
        
        public GetUsedFiles(FindFilesInUseCommand command, Launcher launcher) {
            this.command = command;
            this.launcher = launcher;
        }

        public List<FileUsageDetails> invoke(File f, VirtualChannel channel) throws IOException {
            String workspacePath = f.getCanonicalPath();
            ByteArrayOutputStream commandOutput = new ByteArrayOutputStream();
            BufferedReader reader = null;
            
            try {
                int result = launcher.launch().cmds(command.getArguments(workspacePath)).stdout(commandOutput).start().join();
                reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(commandOutput.toByteArray())));            
                return command.parseOutput(result, reader, workspacePath);
            } catch (InterruptedException e) {
                throw new AbortException(e.getMessage());
            } finally {
                IOUtils.closeQuietly(commandOutput);
                if (reader != null) {
                    IOUtils.closeQuietly(reader);
                }
            }
        }        
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String handleExecutable;        
        public DescriptorImpl() {
            super(LockedFilesReporter.class);
            handleExecutable = "handle.exe";
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Locked files report";
        }
        
        public FormValidation doCheckHandleExecutable(@QueryParameter String value) {
            return FormValidation.validateExecutable(value);
        }

        public String getHandleExecutable() {
            return handleExecutable;
        }

        public void setHandleExecutable(String handleExecutable) {
            this.handleExecutable = handleExecutable;
        }
    }
}
