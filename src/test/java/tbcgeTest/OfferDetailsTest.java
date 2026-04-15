package tbcgeTest;

import basetest.BaseTest;
import com.microsoft.playwright.*;
import org.example.data.Constants;
import org.testng.annotations.Test;

import steps.OfferDetailsSteps;

public class OfferDetailsTest extends BaseTest {

    @Test
    public void verifyOfferDetailsContent() {
        // Steps კლასის ინიციალიზაცია
        OfferDetailsSteps offerSteps = new OfferDetailsSteps(page);

        offerSteps.navigateToOfferPage(Constants.OFFER_DET_PAGE_URL);

        // ტესტის ნაბიჯები (Scenario 3)
        offerSteps.handleCookies();

        offerSteps.verifyOfferTitleIsVisible();
        offerSteps.verifyMainBannerIsVisible();
        offerSteps.verifyDiscountBadgeIsVisible();
        offerSteps.verifyDescriptionSectionIsDisplayed();
    }
}