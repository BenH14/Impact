package uk.co.impactnottingham.benh.impact;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.wordpress.Article;
import uk.co.impactnottingham.benh.wordpress.WordpressREST;

import java.net.URL;

/**
 * Created by bth on 02/06/2018.
 */
public abstract class HeadlineHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "HeadlineHolder";

    public static final int HEADLINE_FADE_OUT_OFFSET = 40;

    protected final float   DISPLAY_DENSITY;
    protected final Context mContext;

    protected final ImageView mThumbnail;
    protected final TextView  mTitle;
    protected final TextView  mCategory;
    protected final TextView  mDate;
    protected final CardView  mCard;
    protected final TextView  mBreakingLabel;
    protected final ImageView mPodcastIcon;
    protected final TextView  mSnippet;

    protected int mImageSize;

    private FragmentManager fm;

    private final Shader mTitleShader;

    HeadlineHolder(View itemView, Context context, FragmentManager fragmentManager) {
        super(itemView);
        int maxHeight = context.getResources().getDimensionPixelSize(R.dimen.max_headline_title_size);
        mTitleShader = new LinearGradient(0, maxHeight - HEADLINE_FADE_OUT_OFFSET,
                0, maxHeight,
                Color.BLACK, 0x66FFFFFF,
                Shader.TileMode.CLAMP);

        fm = fragmentManager;

        mThumbnail = itemView.findViewById(R.id.headline_image);
        mTitle = itemView.findViewById(R.id.headline_title);
        mCategory = itemView.findViewById(R.id.headline_category);
        mDate = itemView.findViewById(R.id.headline_date);
        mCard = itemView.findViewById(R.id.headline_card_view);
        mBreakingLabel = itemView.findViewById(R.id.breaking_label);
        mPodcastIcon = itemView.findViewById(R.id.headline_podcast_icon);
        mSnippet = itemView.findViewById(R.id.headline_snippet);

        mContext = context;
        DISPLAY_DENSITY = mContext.getResources().getDisplayMetrics().density;

        mTitle.getPaint().setShader(mTitleShader);
    }

    void setArticle(Article article) {
        GlideApp.with(itemView.getContext()).clear(mThumbnail);

        mTitle.setText(article.getTitle());
        try {
            mCategory.setTextColor(article.getCategory().getColor(itemView.getContext()));
            mCategory.setText(article.getCategory().name());
        } catch (NullPointerException ex) {
            Log.w(TAG, "setArticle: No Category? article title is " + article.getTitle(), ex);
            mCategory.setText(" ");
        }
        mDate.setText(article.getTimeFromNow());

        if (article.hasLink()) {
            setImage(article.getImageLink());
        } else {
            article.loadImageLink(mImageSize, () -> {
                if (article.getImageLink() != null) {
                    itemView.post(() -> setImage(article.getImageLink()));
                } else {
                    itemView.post(() -> GlideApp.with(itemView.getContext()).clear(mThumbnail));
                }
            });
        }

        mSnippet.setText(article.getSnippet());

        setBreaking(article.isBreaking());
        setPodcast(article.isPodcast());

        mCard.setOnClickListener((View v) -> {
            gotoArticle(article);
        });
    }

    @UiThread
    void setImage(URL url) {
        GlideApp.with(itemView.getContext())
                .asDrawable()
                .load(url.toString())
                .thumbnail(0.1f)
                .transition(DrawableTransitionOptions.withCrossFade(100))
                .into((ImageView) itemView.findViewById(R.id.headline_image));
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

    void setPodcast(boolean podcast) {
        if (podcast) {
            mPodcastIcon.setVisibility(View.VISIBLE);
        } else {
            mPodcastIcon.setVisibility(View.GONE);
        }
    }

    void gotoArticle(Article article) {
        Intent intent = new Intent(mContext, ArticleActivity.class);
        intent.putExtra("ARTICLE", article);
        mContext.startActivity(intent);
    }

    static class LandscapeHeadlineHolder extends HeadlineHolder {

        LandscapeHeadlineHolder(View itemView, Context context, FragmentManager fragmentManager) {
            super(itemView, context, fragmentManager);
            mImageSize = WordpressREST.IMAGE_SIZE_THUMBNAIL;
        }
    }

    static class FeaturedHeadlineHolder extends HeadlineHolder {


        public static final int SNIPPET_FADEOUT_OFFSET = 100;

        @Override
        void setArticle(Article article) {
            super.setArticle(article);

            Rect bounds = new Rect();
            mTitle.getPaint().getTextBounds(article.getTitle(), 0, article.getTitle().length(), bounds);

            // Good enough as an approximation of card width
            int actualHeight = (int) ((bounds.width() / (float) (Resources.getSystem().getDisplayMetrics().widthPixels - 40)) * bounds.height());

            int oneDp       = mContext.getResources().getDimensionPixelSize(R.dimen.onedp);
            int totalHeight = oneDp * 72;
            int freeHeight  = totalHeight - actualHeight;

            Shader snippetShader = new LinearGradient(
                    0, freeHeight - SNIPPET_FADEOUT_OFFSET,
                    0, freeHeight,
                    Color.BLACK, 0xAA000000 + ContextCompat.getColor(mContext, R.color.colorPrimary),
                    Shader.TileMode.CLAMP);
            mSnippet.getPaint().setShader(snippetShader);
            mSnippet.setVisibility(View.VISIBLE);
        }

        FeaturedHeadlineHolder(View itemView, Context context, FragmentManager fragmentManager) {
            super(itemView, context, fragmentManager);
            mImageSize = WordpressREST.IMAGE_SIZE_MEDIUM;

            mCard.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            mCard.getLayoutParams().height = (int) (2 * mContext.getResources().getDimension(R.dimen.headline_card_height));

            RelativeLayout.LayoutParams thumbnailParams = (RelativeLayout.LayoutParams) (mThumbnail.getLayoutParams());

            thumbnailParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;

            RelativeLayout.LayoutParams titleParams = (RelativeLayout.LayoutParams) (mTitle.getLayoutParams());
            titleParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);
            titleParams.addRule(RelativeLayout.BELOW, R.id.headline_image);

            LinearLayout                bottomLayout = itemView.findViewById(R.id.headline_bottom_layout);
            RelativeLayout.LayoutParams bottomParams = (RelativeLayout.LayoutParams) bottomLayout.getLayoutParams();
            bottomParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);




//            RelativeLayout.LayoutParams excerptParams = (RelativeLayout.LayoutParams) (mExcerpt.getLayoutParams());

//            excerptParams.addRule(RelativeLayout.END_OF, RelativeLayout.NO_ID);
//            excerptParams.addRule(RelativeLayout.BELOW, R.id.headline_title);


        }
    }
}
