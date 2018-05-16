package uk.co.impactnottingham.benh.impact;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

/**
 * Created by benh14 on 1/31/18.
 */
public enum Categories {
    NEWS(2, R.color.colorNews, R.color.colorNewsLight),
    FEATURES(7, R.color.colorFeatures, R.color.colorFeaturesLight),
    LIFESTYLE(19163, R.color.colorLifestyle, R.color.colorLifestyleLight),
    ENTERTAINMENT(19161, R.color.colorEntertainment, R.color.colorEntertainmentLight),
    REVIEWS(19164, R.color.colorReviews, R.color.colorReviewsLight),
    SPORT(3, R.color.colorSport, R.color.colorSportLight);

    //todo wordpress ids
    private final int mId;
    private final int mColor;
    private final int mColorLight;

    private Categories(int id, int color, int colorLight) {
        mColor = color;
        mColorLight = colorLight;
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public int getColor(Context context) {
        return context.getResources().getColor(mColor);
    }

    public int getColorLight(Context context) {
        return context.getResources().getColor(mColorLight);
    }
}
