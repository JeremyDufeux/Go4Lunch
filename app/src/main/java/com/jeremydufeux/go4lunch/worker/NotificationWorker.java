package com.jeremydufeux.go4lunch.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jeremydufeux.go4lunch.R;
import com.jeremydufeux.go4lunch.models.Workmate;
import com.jeremydufeux.go4lunch.repositories.WorkmatesRepository;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationWorker extends Worker {
    private static final String TAG = "NotificationWorker";

    private static final String CHANNEL_ID = "REMINDERS";
    public static final int NOTIFICATION_REMINDER_ID = 10;
    public static final String INPUT_CURRENT_USER_ID = "INPUT_CURRENT_USER_ID";
    public static final String INPUT_RESTAURANT_ID = "INPUT_RESTAURANT_ID";
    public static final String INPUT_RESTAURANT_ADDRESS = "INPUT_RESTAURANT_ADDRESS";
    public static final String INPUT_RESTAURANT_NAME = "INPUT_RESTAURANT_NAME";
    public static final String INPUT_RESTAURANT_PHOTO_URL = "INPUT_RESTAURANT_PHOTO_URL";

    private final Context mContext;
    private Disposable mDisposable;
    private final WorkmatesRepository mWorkmatesRepository = new WorkmatesRepository();

    public NotificationWorker(@NonNull Context context,
                              @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        getInterestedWorkmates();
        return Result.success();
    }

    private void getInterestedWorkmates() {
        mDisposable = mWorkmatesRepository.getInterestedWorkmatesForRestaurants(getInputData().getString(INPUT_RESTAURANT_ID))
                .subscribeOn(Schedulers.computation())
                .subscribe(this::loadRestaurantPicture,
                        throwable -> Log.e(TAG, "getInterestedWorkmates " + throwable));
    }

    private void loadRestaurantPicture(List<Workmate> workmateList){
        Glide.with(mContext)
                .asBitmap()
                .load(getInputData().getString(INPUT_RESTAURANT_PHOTO_URL))
                .circleCrop()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        createNotification(workmateList, resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    private void createNotification(List<Workmate> workmateList, Bitmap photo) {
        Bundle bundle = new Bundle();
        bundle.putString(mContext.getString(R.string.arg_restaurant_id), getInputData().getString(INPUT_RESTAURANT_ID));

        PendingIntent pendingIntent = new NavDeepLinkBuilder(mContext)
                .setGraph(R.navigation.main_nav_graph)
                .setDestination(R.id.restaurant_details_fragment)
                .setArguments(bundle)
                .createPendingIntent();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setLargeIcon(photo)
                .setContentTitle(getTitle())
                .setContentText(geContent())
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(geContent())
                        .addLine(mContext.getString(R.string.the_address_is, getInputData().getString(INPUT_RESTAURANT_ADDRESS)))
                        .addLine(getThirdLine(workmateList)))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_name);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        notificationManager.notify(NOTIFICATION_REMINDER_ID, builder.build());

        mDisposable.dispose();
    }

    private Spannable getTitle(){
        String title = mContext.getString(R.string.notification_title);
        Spannable spTitle = new SpannableString(title);
        spTitle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spTitle;
    }

    private Spannable geContent(){
        String restaurantName = getInputData().getString(INPUT_RESTAURANT_NAME);
        int restaurantNameIndex = mContext.getString(R.string.notification_content).indexOf("%1$s");
        String firstLine = mContext.getString(R.string.notification_content, restaurantName);

        Spannable spFirstLine = new SpannableString(firstLine);
        assert restaurantName != null;
        spFirstLine.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                restaurantNameIndex,
                restaurantNameIndex + restaurantName.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spFirstLine;
    }

    private String getThirdLine(List<Workmate> workmateList){

        for(Workmate workmate : workmateList){
            if(workmate.getUId().equals(getInputData().getString(INPUT_CURRENT_USER_ID))){
                workmateList.remove(workmate);
                break;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        if(workmateList.size()>1){
            for(int i = 0; i < workmateList.size(); i++){
                if(i == workmateList.size()-1){
                    stringBuilder.append(mContext.getString(R.string.name_last_separation));
                } else if (i != workmateList.size()-1 && i != 0 ){
                    stringBuilder.append(mContext.getString(R.string.name_separation));
                }
                stringBuilder.append(workmateList.get(i).getNickname());
            }
            stringBuilder.append(mContext.getString(R.string.are_joining_too));
        } else if(workmateList.size()==1){
            stringBuilder.append("\n");
            stringBuilder.append(workmateList.get(0).getNickname());
            stringBuilder.append(mContext.getString(R.string.is_joining_too));
        }

        return stringBuilder.toString();
    }
}
