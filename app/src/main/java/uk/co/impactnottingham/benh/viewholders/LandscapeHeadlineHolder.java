package uk.co.impactnottingham.benh.viewholders;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import uk.co.impactnottingham.benh.wordpress.WordpressREST;

/**
 * Created by bth on 05/04/2019.
 */
public class LandscapeHeadlineHolder extends HeadlineHolder {

    public LandscapeHeadlineHolder(View itemView, Context context, FragmentManager fragmentManager) {
        super(itemView, context, fragmentManager);
        mImageSize = WordpressREST.IMAGE_SIZE_THUMBNAIL;
    }

}
