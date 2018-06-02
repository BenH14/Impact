package uk.co.impactnottingham.benh.impact;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
public class HeadlineHolder extends RecyclerView.ViewHolder {

    final ImageView mThumbnail;
    final TextView  mTitle;
    final TextView  mExcerpt;
    final CardView  mCard;

    HeadlineHolder(View itemView) {
        super(itemView);
        mThumbnail = itemView.findViewById(R.id.headline_image);
        mTitle = itemView.findViewById(R.id.headline_title);
        mExcerpt = itemView.findViewById(R.id.headline_excerpt);
        mCard = itemView.findViewById(R.id.headline_card_view);
    }

    void setArticle(Article article) {
        mTitle.setText(article.getTitle());
        mExcerpt.setText(article.getSnippet());

        new GetImageLinkTask(WordpressREST.IMAGE_SIZE_THUMBNAIL, (URL url) -> itemView.post(() ->
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
}
