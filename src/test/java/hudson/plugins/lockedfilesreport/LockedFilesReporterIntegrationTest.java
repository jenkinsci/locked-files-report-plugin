package hudson.plugins.lockedfilesreport;

import org.junit.Test;
import org.jvnet.hudson.test.Bug;
import org.jvnet.hudson.test.HudsonTestCase;

public class LockedFilesReporterIntegrationTest extends HudsonTestCase {
    @Bug(9904)
    @Test public void testThatMatrixProjectCanUsePlugin() throws Exception {
//        MatrixProject project = createMatrixProject();
//        AxisList axes = new AxisList();
//        axes.add(new Axis("direction","north","south"));
//        project.setAxes(axes);
//        LockedFilesReporter reporter = new LockedFilesReporter();
//        project.getPublishersList().add(reporter);
//        assertBuildStatusSuccess(project.scheduleBuild2(0).get());
    }

    @Test public void testThatFreeStyleProjectCanUsePlugin() throws Exception {
//        FreeStyleProject project = createFreeStyleProject();
//        project.getPublishersList().add(new LockedFilesReporter());
//        assertBuildStatusSuccess(project.scheduleBuild2(0).get());
    }
}