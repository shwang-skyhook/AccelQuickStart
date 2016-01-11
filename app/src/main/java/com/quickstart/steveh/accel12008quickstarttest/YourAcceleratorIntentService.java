package com.quickstart.steveh.accel12008quickstarttest;

/**
 * Created by steveh on 6/1/15.
 */
import com.skyhookwireless.accelerator.AcceleratorClient;
import com.skyhookwireless.accelerator.CampaignVenue;


import android.content.Intent;
import android.app.IntentService;
import android.widget.Toast;

public class YourAcceleratorIntentService
        extends IntentService {

    static final String ACTION_ACCELERATOR_EVENT = "com.skyhookwireless.accelerator.ACCELERATOR_EVENT";

    public YourAcceleratorIntentService() {
        super("YourAcceleratorIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (AcceleratorClient.hasError(intent)) {
            int errorCode = AcceleratorClient.getErrorCode(intent);
            showToast("error onHandleIntent");
        } else {
            CampaignVenue venue = AcceleratorClient.getTriggeringCampaignVenue(intent);
            if (venue != null) {
                if (AcceleratorClient.getCampaignVenueTransition(intent) == CampaignVenue.CAMPAIGN_VENUE_TRANSITION_ENTER) {
                    showToast("entered venue");
                } else {
                    showToast("exited venue");
                }
            } else {
                showToast("venue == null");
            }
        }
        final Intent broadcastIntent = new Intent(ACTION_ACCELERATOR_EVENT);
        sendBroadcast(broadcastIntent);
    }
    private void showToast(final String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
