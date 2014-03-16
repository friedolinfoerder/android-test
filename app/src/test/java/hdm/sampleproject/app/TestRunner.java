package hdm.sampleproject.app;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

/**
 * @author Leon Schröder
 * @author Friedolin Förder
 * RobolectricGradleTestRunner
 */
public class TestRunner extends RobolectricTestRunner {

    /**
     * Constructor
     * @param testClass The class which should be tested
     * @throws InitializationError
     */
    public TestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
    
    /**
     * Get the AndroidManifest of the application
     * @param  Config The configuration for the application
     * @return        The AndroidManifest of the application
     */
    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String manifestProperty = System.getProperty("android.manifest");
        if (config.manifest().equals(Config.DEFAULT) && manifestProperty != null) {
            String resProperty = System.getProperty("android.resources");
            String assetsProperty = System.getProperty("android.assets");
            return new AndroidManifest(Fs.fileFromPath(manifestProperty),
                    Fs.fileFromPath(resProperty), Fs.fileFromPath(assetsProperty));
        }
        return super.getAppManifest(config);
    }

}
