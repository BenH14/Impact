package uk.co.impactnottingham.benh.impact;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.wordpress.Article;
import uk.co.impactnottingham.benh.wordpress.GetImageLinkTask;
import uk.co.impactnottingham.benh.wordpress.WordpressREST;

import java.net.URL;

/**
 * Created by bth on 02/06/2018.
 */
public abstract class HeadlineHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "HeadlineHolder";

    protected final float   DISPLAY_DENSITY;
    protected final Context mContext;

    protected final ImageView mThumbnail;
    protected final TextView  mTitle;
    protected final TextView  mExcerpt;
    protected final CardView  mCard;
    protected final TextView  mBreakingLabel;

    protected int mImageSize;

    HeadlineHolder(View itemView, Context context) {
        super(itemView);

        mContext = context;
        DISPLAY_DENSITY = mContext.getResources().getDisplayMetrics().density;


        mThumbnail = itemView.findViewById(R.id.headline_image);
        mTitle = itemView.findViewById(R.id.headline_title);
        mExcerpt = itemView.findViewById(R.id.headline_excerpt);
        mCard = itemView.findViewById(R.id.headline_card_view);
        mBreakingLabel = itemView.findViewById(R.id.breaking_label);
    }

    void setArticle(Article article) {
        mTitle.setText(article.getTitle());
        mExcerpt.setText(article.getSnippet());

        new GetImageLinkTask(mImageSize, (URL url) -> itemView.post(() ->
                GlideApp.with(itemView.getContext())
                        .asDrawable()
                        .load(url.toString())
                        .thumbnail(0.1f)
                        .into((ImageView) itemView.findViewById(R.id.headline_image))
        )).execute(article.getImageId());

        mCard.setOnClickListener((View v) -> {
            Toast.makeText(itemView.getContext(), "Card Clicked", Toast.LENGTH_LONG).show();
            //todo
        });
    }

    void setBreaking(boolean breaking) {
        if (breaking) {
            mBreakingLabel.setVisibility(View.VISIBLE);
            mCard.setForeground(mContext.getDrawable(R.drawable.breaking_frame));
        } else {
            mBreakingLabel.setVisibility(View.GONE);
            mCard.setForeground(null);
        }
    }

    static class LandscapeHeadlineHolder extends HeadlineHolder {

        LandscapeHeadlineHolder(View itemView, Context context) {
            super(itemView, context);
            mImageSize = WordpressREST.IMAGE_SIZE_THUMBNAIL;
        }
    }

    static class FeaturedHeadlineHolder extends HeadlineHolder {

        FeaturedHeadlineHolder(View itemView, Context context) {
            super(itemView, context);
            mImageSize = WordpressREST.IMAGE_SIZE_MEDIUM;

            mCard.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            mCard.getLayoutParams().height = (int) (2 * mContext.getResources().getDimension(R.dimen.headline_card_height));

            RelativeLayout.LayoutParams thumbnailParams = (RelativeLayout.LayoutParams) (mThumbnail.getLayoutParams());

            thumbnailParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;

            RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) (mTitle.getLayoutParams());

            titleParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);
            titleParams.addRule(RelativeLayout.BELOW, R.id.headline_image);


            RelativeLayout.LayoutParams excerptParams = (RelativeLayout.LayoutParams) (mExcerpt.getLayoutParams());

            excerptParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);
            excerptParams.addRule(RelativeLayout.BELOW, R.id.headline_title);


        }
    }
}
