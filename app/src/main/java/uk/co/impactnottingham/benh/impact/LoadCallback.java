package uk.co.impactnottingham.benh.impact;

/**
 * Created by benh14 on 12/22/17.
 */
public interface LoadCallback {
    boolean mTriggered = false;
    void onLoad();

    default boolean getTriggered() {
        return mTriggered;
    }
}
