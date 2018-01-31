package uk.co.impactnottingham.benh.impact;

import android.graphics.Color;

/**
 * Created by benh14 on 1/31/18.
 */
public enum Categories {
    NEWS(R.color.colorNews, R.color.colorNewsLight),
    FEATURES(R.color.colorFeatures, R.color.colorFeaturesLight),
    LIFESTYLE(R.color.colorLifestyle, R.color.colorLifestyleLight),
    ENTERTAINMENT(R.color.colorEntertainment, R.color.colorEntertainmentLight),
    REVIEWS(R.color.colorReviews, R.color.colorReviewsLight),
    SPORT(R.color.colorSport, R.color.colorSportLight);

    //todo wordpress ids
    private final int mColor;
    private final int mColorLight;

    private Categories(int color, int colorLight) {
        mColor = color;
        mColorLight = colorLight;

    }

    public int getColor() {
        return mColor;
    }

    public int getColorLight() {
        return mColorLight;
    }
}
