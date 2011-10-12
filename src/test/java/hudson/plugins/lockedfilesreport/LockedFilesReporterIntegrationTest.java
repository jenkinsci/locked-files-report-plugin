package hudson.plugins.lockedfilesreport;

import hudson.matrix.Axis;
import hudson.matrix.AxisList;
import hudson.matrix.MatrixProject;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.HudsonTestCase;

public class LockedFilesReporterIntegrationTest extends HudsonTestCase {
    @Bug(9904)
    public void testThatMatrixProjectCanUsePlugin() throws Exception {
        MatrixProject project = createMatrixProject();
        AxisList axes = new AxisList();
        axes.add(new Axis("direction","north","south"));
        project.setAxes(axes);
        LockedFilesReporter reporter = new LockedFilesReporter();
        project.getPublishersList().add(reporter);
        assertBuildStatusSuccess(project.scheduleBuild2(0).get());
    }

    public void testThatFreeStyleProjectCanUsePlugin() throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        project.getPublishersList().add(new LockedFilesReporter());
        assertBuildStatusSuccess(project.scheduleBuild2(0).get());
    }
}