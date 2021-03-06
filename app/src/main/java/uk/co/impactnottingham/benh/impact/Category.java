package uk.co.impactnottingham.benh.impact;

import android.content.Context;

/**
 * Created by benh14 on 1/31/18.
 */
public enum Category {
    DEFAULT(0, R.color.colorPrimary, R.color.colorAccent),
    NEWS(2, R.color.colorNews, R.color.colorNewsLight),
    FEATURES(7, R.color.colorFeatures, R.color.colorFeaturesLight),
    LIFESTYLE(19163, R.color.colorLifestyle, R.color.colorLifestyleLight),
    ENTERTAINMENT(19161, R.color.colorEntertainment, R.color.colorEntertainmentLight),
    REVIEWS(19164, R.color.colorReviews, R.color.colorReviewsLight),
    SPORT(3, R.color.colorSport, R.color.colorSportLight),
    PODCAST(29325, R.color.colorPodcasts, R.color.colorPodcastsLight);


    private final int mId;
    private final int mColor;
    private final int mColorLight;

    Category(int id, int color, int colorLight) {
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

    public String getCapitalizedName() {
        if (this == DEFAULT) {
            return "";
        }
        return name().substring(0,1).toUpperCase() + name().substring(1).toLowerCase();
    }
}
