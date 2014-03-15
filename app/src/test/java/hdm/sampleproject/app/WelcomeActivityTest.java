package hdm.sampleproject.app;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by schreon on 3/15/14.
 */

@SuppressWarnings("ConstantConditions")
@Config(emulateSdk = 18)
@RunWith(TestRunner.class)
public class WelcomeActivityTest {
    WelcomeActivity welcomeActivity;

    @Before
    public void setUp() {
        welcomeActivity = Robolectric.buildActivity(WelcomeActivity.class).create().get();
    }

    @Test
    public void sampleTest() {
        assertNotNull(welcomeActivity);
    }
}
