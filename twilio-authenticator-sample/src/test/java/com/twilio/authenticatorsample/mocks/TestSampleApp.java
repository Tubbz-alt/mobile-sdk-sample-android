package com.twilio.authenticatorsample.mocks;

import android.provider.Settings;

import com.twilio.authenticator.TwilioAuthenticator;
import com.twilio.authenticatorsample.SampleApp;

/**
 * Created by jsuarez on 8/14/17.
 */

public class TestSampleApp extends SampleApp {

    MockTwilioAuthenticator mockTwilioAuthenticator;

    @Override
    public void onCreate() {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ANDROID_ID, "test_android_id");
        super.onCreate();
    }

    @Override
    protected TwilioAuthenticator buildTwilioAuthenticator() {
        return new MockTwilioAuthenticator(true);
    }

    @Override
    public TwilioAuthenticator getTwilioAuthenticator() {
        return mockTwilioAuthenticator;
    }

    public void setTwilioAuth(MockTwilioAuthenticator mockTwilioAuthenticator) {
        this.mockTwilioAuthenticator = mockTwilioAuthenticator;
    }
}
