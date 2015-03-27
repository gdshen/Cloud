package tk.gdshen.cloud.helpers;

/**
 * Created by gdshen on 15-3-13.
 */
public class TransformHelper {
    static {
        System.loadLibrary("imagetrans");
    }
    public native static int encryptJPEG(String secret, String cover, String final_image, String intput_key);
    public native static int decryptJPEG(String final_image, String recover, String intput_key);

}
