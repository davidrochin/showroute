package xyz.showroute.showroute;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Created by jdrc8 on 26/04/2018.
 */

public class GeocodeAddressIntentService extends IntentService {

    protected ResultReceiver resultReceiver;
    private static final String TAG = "FetchAddyIntentService";

    public GeocodeAddressIntentService(){
        super("GeocodeAddressIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        /*Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        int fetchType = intent.getIntExtra(Constants.FETCH_TYPE_EXTRA, 0);*/
    }
}
