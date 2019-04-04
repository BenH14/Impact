package uk.co.impactnottingham.benh.viewholders;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.RelativeLayout;
import uk.co.impactnottingham.benh.impact.R;

/**
 * Created by bth on 05/04/2019.
 */
public class SuperFeaturedHeadlineHolder extends FeaturedHeadlineHolder {

    public SuperFeaturedHeadlineHolder(View itemView, Context context, FragmentManager fragmentManager) {
        super(itemView, context, fragmentManager);

        RelativeLayout.LayoutParams thumbnailParams = (RelativeLayout.LayoutParams) (mThumbnail.getLayoutParams());
        thumbnailParams.height *= 2; // Double the height of the image
    }

    @Override
    protected double getSizeRatio() {
        return 3.5;
    }
}
