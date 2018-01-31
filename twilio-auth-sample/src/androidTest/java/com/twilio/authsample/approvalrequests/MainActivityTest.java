package com.twilio.authsample.approvalrequests;

import android.content.Intent;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.twilio.authenticator.external.ApprovalRequestStatus;
import com.twilio.authenticator.external.ApprovalRequests;
import com.twilio.authenticator.external.TOTPs;
import com.twilio.authsample.R;
import com.twilio.authsample.main.MainActivity;
import com.twilio.authsample.matchers.RecyclerViewItemCountAssertion;
import com.twilio.authsample.matchers.RecyclerViewItemMatcher;
import com.twilio.authsample.matchers.ToolbarTitleMatcher;
import com.twilio.authsample.mocks.MockApp;
import com.twilio.authsample.mocks.MockApprovalRequest;
import com.twilio.authsample.mocks.MockApprovalRequests;
import com.twilio.authsample.mocks.MockTwilioAuthenticator;
import com.twilio.authsample.mocks.TestApp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by jsuarez on 6/1/16.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    public static final String APPROVAL_REQUEST_PENDING_MESSAGE = "Approval Request Pending";
    public static final String APPROVAL_REQUEST_EXPIRED_MESSAGE = "Approval Request Expired";
    public static final String APPROVAL_REQUEST_APPROVED_MESSAGE = "Approval Request Approved";
    public static final String APPROVAL_REQUEST_DENIED_MESSAGE = "Approval Request Denied";

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, false);

    private MockTwilioAuthenticator mockTwilioAuthenticator;

    @Before
    public void setUp() throws Exception {
        TestApp application = TestApp.TEST_APP;
        mockTwilioAuthenticator = new MockTwilioAuthenticator(true);
        application.setMockTwilioAuthenticator(mockTwilioAuthenticator);

        ApprovalRequests approvalRequests = new MockApprovalRequests();
        MockApprovalRequest.Builder builder = new MockApprovalRequest.Builder();
        builder.setMessage(APPROVAL_REQUEST_PENDING_MESSAGE);
        builder.setTransactionId("uuid1");
        builder.setStatus(ApprovalRequestStatus.pending);
        builder.setCreationDate(new Date());
        MockApprovalRequest mockApprovalRequest = builder.createMockApprovalRequest();
        approvalRequests.getPending().add(mockApprovalRequest);

        builder.setMessage(APPROVAL_REQUEST_EXPIRED_MESSAGE);
        builder.setTransactionId("uuid2");
        builder.setStatus(ApprovalRequestStatus.expired);
        builder.setCreationDate(new Date());
        approvalRequests.getExpired().add(builder.createMockApprovalRequest());

        builder.setMessage(APPROVAL_REQUEST_APPROVED_MESSAGE);
        builder.setTransactionId("uuid3");
        builder.setStatus(ApprovalRequestStatus.approved);
        builder.setCreationDate(new Date());
        approvalRequests.getApproved().add(builder.createMockApprovalRequest());


        builder.setMessage(APPROVAL_REQUEST_DENIED_MESSAGE);
        builder.setTransactionId("uuid4");
        builder.setStatus(ApprovalRequestStatus.denied);
        builder.setCreationDate(new Date());
        approvalRequests.getDenied().add(builder.createMockApprovalRequest());
        mockTwilioAuthenticator.setApprovalRequests(approvalRequests);
        Intent mainActivityIntent = new Intent(getTargetContext(), MainActivity.class);
        mainActivityTestRule.launchActivity(mainActivityIntent);
    }

    @Test
    public void testRequestsViewPendingTab() throws Exception {
        // Check that the correct title is used
        CharSequence activityTitle = getTargetContext().getString(R.string.menu_navigation_requests);
        onView(withId(R.id.toolbar)).check(matches(new ToolbarTitleMatcher(is(activityTitle))));

        // Check initial state with two tabs
        onView(withText(R.string.fragment_approval_requests_pending_title)).check(matches(isDisplayed()));
        onView(withText(R.string.fragment_approval_requests_archive_title)).check(matches(isDisplayed()));

        // Select pending tab
        onView(withText(R.string.fragment_approval_requests_pending_title)).perform(click());

        // Check that there is only one pending transaction
        onView(allOf(withId(R.id.approvalRequests), isDisplayed())).check(new RecyclerViewItemCountAssertion(1));
        onView(allOf(withId(R.id.approvalRequests), isDisplayed())).perform(scrollToPosition(0)).check(matches(new RecyclerViewItemMatcher(0, hasDescendant(withText(APPROVAL_REQUEST_PENDING_MESSAGE)))));

    }

    @Test
    public void testRequestsViewArchiveTab() throws Exception {

        // Check initial state with two tabs
        onView(withText(R.string.fragment_approval_requests_pending_title)).check(matches(isDisplayed()));
        onView(withText(R.string.fragment_approval_requests_archive_title)).check(matches(isDisplayed()));

        // Select archive tab
        onView(withText(R.string.fragment_approval_requests_archive_title)).perform(click());

        // Check that there are 3 approval requests displayed
        onView(allOf(withId(R.id.approvalRequests), isDisplayed())).check(new RecyclerViewItemCountAssertion(3));
        onView(allOf(withId(R.id.approvalRequests), isDisplayed())).perform(scrollToPosition(0)).check(matches(new RecyclerViewItemMatcher(0, hasDescendant(withText(APPROVAL_REQUEST_DENIED_MESSAGE)))));
        onView(allOf(withId(R.id.approvalRequests), isDisplayed())).perform(scrollToPosition(1)).check(matches(new RecyclerViewItemMatcher(1, hasDescendant(withText(APPROVAL_REQUEST_APPROVED_MESSAGE)))));
        onView(allOf(withId(R.id.approvalRequests), isDisplayed())).perform(scrollToPosition(2)).check(matches(new RecyclerViewItemMatcher(2, hasDescendant(withText(APPROVAL_REQUEST_EXPIRED_MESSAGE)))));

    }

    @Test
    public void testRequestsViewSelectApprovalRequest() throws Exception {
        // Check initial state with two tabs
        onView(withText(R.string.fragment_approval_requests_pending_title)).check(matches(isDisplayed()));
        onView(withText(R.string.fragment_approval_requests_archive_title)).check(matches(isDisplayed()));

        // Select pending tab
        onView(withText(R.string.fragment_approval_requests_pending_title)).perform(click());

        // Click on the first approval request
        onView(allOf(withId(R.id.approvalRequests), isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check that we are now on the detail activity
        CharSequence approvalRequestDetailActivityTitle = getTargetContext().getString(R.string.title_activity_approval_request_detail);
        onView(withId(R.id.action_bar)).check(matches(new ToolbarTitleMatcher(is(approvalRequestDetailActivityTitle))));
    }

    @Test
    public void testEmptyTokensView() throws Exception {
        //Empty app list
        mockTwilioAuthenticator.setTotps(new TOTPs());

        // Open navigation menu
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_tokens));

        // Check that the correct title is used
        CharSequence activityTitle = getTargetContext().getString(R.string.menu_navigation_tokens);
        onView(withId(R.id.toolbar)).check(matches(new ToolbarTitleMatcher(is(activityTitle))));

        // Check that an empty token list is displayed
        onView(withId(R.id.tokens_list)).check(matches(isDisplayed()));
        onView(withId(R.id.tokens_list)).check(new RecyclerViewItemCountAssertion(0));
    }

    @Test
    public void testTokensViewWithTwoTokens() throws Exception {

        MockApp app1 = new MockApp("app_1", "First App");
        MockApp app2 = new MockApp("app_2", "Second App");

        //Empty app list
        TOTPs totps = new TOTPs();
        totps.put(app1.getAppId(), app1);
        totps.put(app2.getAppId(), app2);
        mockTwilioAuthenticator.setTotps(totps);

        // Open navigation menu
        onView(withId(R.id.drawer_layout)).perform(open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_tokens));

        // Check that the correct title is used
        CharSequence activityTitle = getTargetContext().getString(R.string.menu_navigation_tokens);
        onView(withId(R.id.toolbar)).check(matches(new ToolbarTitleMatcher(is(activityTitle))));

        // Check that a list with 2 token is displayed
        onView(withId(R.id.tokens_list)).check(matches(isDisplayed()));
        onView(withId(R.id.tokens_list)).check(new RecyclerViewItemCountAssertion(2));
    }
}