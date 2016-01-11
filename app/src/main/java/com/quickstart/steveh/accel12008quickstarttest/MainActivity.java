package com.quickstart.steveh.accel12008quickstarttest;

import com.skyhookwireless.accelerator.AcceleratorClient;
import com.skyhookwireless.accelerator.NearbyCampaignVenue;
import com.skyhookwireless.accelerator.AcceleratorClient.OnRegisterForCampaignMonitoringResultListener;
import com.skyhookwireless.accelerator.AcceleratorClient.OnStartCampaignMonitoringResultListener;
import com.skyhookwireless.accelerator.AcceleratorClient.OnStopCampaignMonitoringResultListener;
import com.skyhookwireless.accelerator.AcceleratorClient.NearbyMonitoredVenuesListener;
import com.skyhookwireless.accelerator.AcceleratorClient.VenueInfoListener;
import com.skyhookwireless.accelerator.AcceleratorClient.CampaignVisitsListener;
import com.skyhookwireless.accelerator.VenueInfo;
import com.skyhookwireless.accelerator.CampaignVenue;
import com.skyhookwireless.accelerator.CampaignVisit;

import android.app.Service;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MainActivity
        extends ActionBarActivity
        implements
            AcceleratorClient.ConnectionCallbacks,
            AcceleratorClient.OnConnectionFailedListener,
            OnStartCampaignMonitoringResultListener,
            OnStopCampaignMonitoringResultListener,
            OnRegisterForCampaignMonitoringResultListener,
            NearbyMonitoredVenuesListener,
            CampaignVisitsListener,
            VenueInfoListener
{
    private AcceleratorClient accelerator;
    private ArrayList<String> campaigns = new ArrayList<String>();
    private Boolean monitoringAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerReceiver(_serviceBroadcastReceiver, new IntentFilter(YourAcceleratorIntentService.ACTION_ACCELERATOR_EVENT));
        accelerator = new AcceleratorClient(this, "eJwVwccNACAIAMC3w5BQFPEpZSrj7sY7aoSfsFE7hp42lkI4O9ROhl5aEDhdNnUMjfsAE70LQw", this, this);
        accelerator.connect();
    }

    @Override
    public void onConnected() {
        // location accelerator methods. Before calling any of the
        // campaign monitoring methods, a pending intent first needs to
        // be registered for campaign monitoring, and before calling the
        // isMonitoringAllCampaigns() or getMonitoredCampaigns() methods,
        // that registration must be complete.
        //handle successful connection...

        TextView tv = (TextView)findViewById(R.id.onCreate);
        tv.setText("connected");

        Intent intent = new Intent(this, YourAcceleratorIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        accelerator.registerForCampaignMonitoring(pendingIntent, this);

    }

    @Override
    public void onDisconnected() {
        //handle disconnection...
        showToast("Disconnected");
    }

    public void onFetchNearbyMonitoredVenuesClicked(View v) {
        accelerator.fetchNearbyMonitoredVenues(20, this);
    }

    @Override
    public void onNearbyMonitoredVenuesError(int i) {
        EditText et = (EditText) findViewById(R.id.monitoredVenues);
        et.setText(i);
    }

    @Override
    public void onNearbyMonitoredVenuesFetched(List<NearbyCampaignVenue> venues) {
        EditText et = (EditText) findViewById(R.id.monitoredVenues);
        et.setText(venues.toString());
    }

    public void onFetchVenueInfoAtLocation(View V) {
        accelerator.fetchVenueInfoAtLocation(this);
    }


    @Override
    public void onVenueInfoFetched(List<VenueInfo> venues)
    {
        // handle venue information...
        EditText et = (EditText) findViewById(R.id.monitoredVenues);
        et.setText(venues.toString());

    }
    @Override
    public void onVenueInfoError(int errorCode)
    {
        // handle fetch venue info error...
        EditText et = (EditText) findViewById(R.id.monitoredVenues);
        et.setText(Integer.toString(errorCode));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(int errorCode) {
        //handle connection failure...
        TextView tv = (TextView)findViewById(R.id.onRegister);
        tv.setText("failed");
    }

    @Override
    public void onStopCampaignMonitoringResult(int i, String s) {
        Integer n = i;
        String result = s;
    }

    @Override
    public void onRegisterForCampaignMonitoringResult(int statusCode,
                                                      PendingIntent pendingIntent) {
        // The isMonitoringAllCampaigns and getMonitoredCampaigns methods
        // can also be called here if desired.
        accelerator.stopMonitoringForAllCampaigns(this);
        //accelerator.startMonitoringForCampaign("MA - Coffee Shops", this);
        //accelerator.startMonitoringForCampaign("pepsi-locations-us-arby's", this);
        //accelerator.startMonitoringForCampaign("pepsi-locations-us-panera", this);
        //accelerator.startMonitoringForCampaign("pepsi-locations-us-panda-express", this);
        accelerator.startMonitoringForAllCampaigns(this);
        TextView tv = (TextView)findViewById(R.id.onRegister);
        tv.setText("registered for campaign monitoring");
    }

    @Override
    public void onStartCampaignMonitoringResult(int i, String s) {
        campaigns = new ArrayList<String>(accelerator.getMonitoredCampaigns());
        monitoringAll = accelerator.isMonitoringAllCampaigns();
        TextView tv = (TextView)findViewById(R.id.onStartMonitoring);
        tv.setText("started monitoring for campaigns");
        //accelerator.fetchRecentCampaignVisits(10, this);
    }

    private final BroadcastReceiver _serviceBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent)
                {
                    if (AcceleratorClient.hasError(intent))
                    {
                        showToast("Accelerator Error: " + AcceleratorClient.getErrorCode(intent));
                        return;
                    }
                    final CampaignVenue venue = AcceleratorClient.getTriggeringCampaignVenue(intent);
                    if (venue != null)
                    {
                        final boolean isEntered = AcceleratorClient.getCampaignVenueTransition(intent) == CampaignVenue.CAMPAIGN_VENUE_TRANSITION_ENTER;
                        final String label = isEntered ? "Entered" : "Exited";
                        showToast(label + " venue #" + venue.venueId + " in campaign " + venue.campaignName);
                        return;
                    }
                }
    };
    @Override
    public void onCampaignVisitsFetched(final List<CampaignVisit> visits)
    {
        showToast("Recent campaign visits: "
                +(visits.size() <= 0 ? "None" : visits));

    }

    @Override
    public void onCampaignVisitsError(final int errorCode)
    {
        showToast("Recent campaign visits error: "+errorCode);

    }

    private void showToast(final String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

}