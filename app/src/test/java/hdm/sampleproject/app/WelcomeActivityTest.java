package hdm.sampleproject.app;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNotNull;

/**
 * @author Leon Schröder
 * @author Friedolin Förder
 * WelcomeActivityTest
 */
@SuppressWarnings("ConstantConditions")
@Config(emulateSdk = 18)
@RunWith(TestRunner.class)
public class WelcomeActivityTest {
    WelcomeActivity welcomeActivity;

    @Before
    public void setUp() {
        // create the activity with the robolectric library
        welcomeActivity = Robolectric.buildActivity(WelcomeActivity.class).create().get();
    }
    
    /**
     * Tests if the activity is available and not null
     */
    @Test
    public void sampleTest() {
        assertNotNull(welcomeActivity);
    }
}
