package uk.co.impactnottingham.benh.impact;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by bth on 22/03/2019.
 */
public class PrintIssueCountdown {

    public static final int SECS_IN_DAY = 60 * 60 * 24;

    private final TextView mCountdown;
    private final TextView mDaysLabel;

    public PrintIssueCountdown(TextView countdown, TextView daysLabel) {
        mCountdown = countdown;
        mDaysLabel = daysLabel;
    }

    public void start(Context context) {
        long timeToNextIssue = getNextIssueTime(context);
        new CountDownTimer(timeToNextIssue, 60 * 1000) {  // Only update every minute
            @Override
            public void onTick(long millisUntilFinished) {
                String formated_days = String.format(Locale.US, "%02d", (timeToNextIssue / SECS_IN_DAY) + 1);
                mCountdown.setText(formated_days);
            }

            @Override
            public void onFinish() {}

        }.start();
    }

    private long getNextIssueTime(Context context) {
        int[] dates       = context.getResources().getIntArray(R.array.print_issue_dates);  //Dates are unix timetaps
        long  currentTime = System.currentTimeMillis() / 1000L;

        long soonest = Integer.MAX_VALUE;

        for (int date: dates) {
            if (date > currentTime && date < soonest) {
                soonest = date;
            }
        }

        return soonest == Integer.MAX_VALUE ? 0 : soonest - currentTime;
    }
}
