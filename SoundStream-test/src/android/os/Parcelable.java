package android.os;

/**
 * .net.message tests are run as standard JUnit tests, and because this
 * is an Android test project, the tests must be run with the Android
 * test launcher.  This removes Android JAR files from the class path
 * which removes runtime access to this interface, which is required
 * by several of our model objects.  Ergo, this hack is required
 * so we can run our tests within eclipse without running them as
 * Android tests, which requires an emulator or device.
 * 
 * tl;dr THIS ONLY EXISTS BECAUSE ECLIPSE SUCKS.
 * 
 * @author Jesse Rosalia
 *
 */
public interface Parcelable {

    public interface Creator<T> {
        public T createFromParcel(Parcel in);

        public T[] newArray(int size);
    }

    public int describeContents();

    public void writeToParcel(Parcel dest, int flags);
}
