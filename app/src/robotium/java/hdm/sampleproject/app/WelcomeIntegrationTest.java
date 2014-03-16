package hdm.sampleproject.app;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

/**
 * @author Leon Schröder
 * @author Friedolin Förder
 * WelcomeIntegrationTest
 */
public class WelcomeIntegrationTest extends ActivityInstrumentationTestCase2<WelcomeActivity> {
    private Solo solo;
    
    /**
     * Constructor
     */
    public WelcomeIntegrationTest() {
        super(WelcomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Log.d("robotium", "creating solo");
        solo = new Solo(getInstrumentation(), getActivity());
    }
    
    /**
     * Tests if there is a text welcome! available at the WelcomeActivity
     */ 
    public void testWelcome() throws Exception {
        Log.d("robotium", "wait for the activity to appear");
        solo.waitForActivity(WelcomeActivity.class);
        
        Log.d("robotium", "check if there is the text welcome!");
        assertTrue(solo.searchText("welcome!"));
    }
    
    @Override
    public void tearDown() throws Exception {
        Log.d("robotium", "finish the activity");
        solo.finishOpenedActivities();

        super.tearDown();
    }

}
