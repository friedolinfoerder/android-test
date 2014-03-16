package hdm.sampleproject.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author Leon Schröder
 * @author Friedolin Förder
 * WelcomeActivity
 */
public class WelcomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}
