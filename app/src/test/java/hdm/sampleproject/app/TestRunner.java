package hdm.sampleproject.app;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

/**
 * @author Leon Schröder
 * RobolectricGradleTestRunner
 */
public class TestRunner extends RobolectricTestRunner {

    /**
     * Constructor
     * @param testClass
     * @throws InitializationError
     */
    public TestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

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