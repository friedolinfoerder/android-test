package hdm.sampleproject.app;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

/**
 * Created by schreon on 3/15/14.
 */
public class WelcomeIntegrationTest extends ActivityInstrumentationTestCase2<WelcomeActivity> {

    public WelcomeIntegrationTest() {
        super(WelcomeActivity.class);
    }

    private Solo solo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Log.d("robotium", "creating solo");
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testWelcome() throws Exception {
        solo.waitForActivity(WelcomeActivity.class);
        assertTrue(solo.searchText("welcome!"));
    }

    @Override
    public void tearDown() throws Exception {
        // finish activity
        solo.finishOpenedActivities();

        super.tearDown();
    }

}
