package uk.co.impactnottingham.benh.viewholders;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import uk.co.impactnottingham.benh.impact.R;
import uk.co.impactnottingham.benh.wordpress.WordpressREST;

/**
 * Created by bth on 05/04/2019.
 */
public class FeaturedHeadlineHolder extends HeadlineHolder {


    public FeaturedHeadlineHolder(View itemView, Context context, FragmentManager fragmentManager) {
        super(itemView, context, fragmentManager);
        mImageSize = WordpressREST.IMAGE_SIZE_MEDIUM;

        mCard.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        mCard.getLayoutParams().height = (int) (getSizeRatio() * mContext.getResources().getDimension(R.dimen.headline_card_height));

        RelativeLayout.LayoutParams thumbnailParams = (RelativeLayout.LayoutParams) (mThumbnail.getLayoutParams());

        thumbnailParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;

        RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) (mTitle.getLayoutParams());
        titleParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);
        titleParams.addRule(RelativeLayout.BELOW, R.id.headline_image);

        LinearLayout                bottomLayout = itemView.findViewById(R.id.headline_bottom_layout);
        RelativeLayout.LayoutParams bottomParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
        bottomParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);

        RelativeLayout.LayoutParams snippetParams = (RelativeLayout.LayoutParams) (mSnippet.getLayoutParams());
        snippetParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);

        RelativeLayout.LayoutParams fadeoutParams = (RelativeLayout.LayoutParams) (mFadeout.getLayoutParams());
        fadeoutParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);
    }

    protected double getSizeRatio() {
       return 2;
    }

}
