package uk.co.impactnottingham.benh.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.annotation.UiThread;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import uk.co.impactnottingham.benh.glide.GlideApp;
import uk.co.impactnottingham.benh.impact.ArticleActivity;
import uk.co.impactnottingham.benh.impact.Category;
import uk.co.impactnottingham.benh.impact.R;
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

    protected final LinearLayout mFadeout;

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
        mFadeout = itemView.findViewById(R.id.headline_fadeout_bar);

        mContext = context;
        DISPLAY_DENSITY = mContext.getResources().getDisplayMetrics().density;

        mTitle.getPaint().setShader(mTitleShader);
    }

    public void setArticle(Article article) {
        mThumbnail.setImageDrawable(null);

        mTitle.setText(article.getTitle());
        try {
            if (article.getCategory() == Category.DEFAULT) {
                mCategory.setVisibility(View.INVISIBLE);
            } else {
                mCategory.setVisibility(View.VISIBLE);
            }
            mCategory.setTextColor(article.getCategory().getColor(itemView.getContext()));
            mCategory.setText(article.getCategory().name());
        } catch (NullPointerException ex) {
            Log.w(TAG, "setArticle: No Category? article title is " + article.getTitle(), ex);
            mCategory.setText(" ");
        }
        mDate.setText(article.getTimeFromNow());

        if (article.hasLink()) {
            setImage(article.getImageLink(mImageSize));
        } else {
                article.loadImageLink(() -> {
                        itemView.post(() -> setImage(article.getImageLink(mImageSize)));
                });
//            }
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
        CircularProgressDrawable spinner = new CircularProgressDrawable(mContext);
        spinner.setStrokeWidth(5f);
        spinner.setCenterRadius(30f);
        spinner.start();
        GlideApp.with(itemView.getContext())
                .asDrawable()
                .load(url.toString())
                .placeholder(spinner)
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


}
