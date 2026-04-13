package tbcgeTest;

import basetest.BaseTest;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.example.data.Constants;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.Locator;
import pages.LocationPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LocationTest extends BaseTest { // ვთქვათ BaseTest-ში ხდება setupMobile()

    @Test
    public void openMobVersion() {
        // გვერდის ინიციალიზაცია
        page.navigate(Constants.BASE_URL);
        LocationPage locationPage = new LocationPage(page);

        // Step 1: ქუქიების მიღება
        locationPage.acceptCookies();

        // Step 2: ნავიგაცია მისამართებზე
        locationPage.openAddressesFromMenu();

        // Step 3: ვალიდაცია - URL, სათაური და რუკა
        assertThat(page).hasURL(Pattern.compile(Constants.LOCATION_URL));
        assertThat(locationPage.getPageHeading()).isVisible();

        locationPage.getMapContainer().waitFor(new Locator.WaitForOptions().setTimeout(Constants.MAP_LOAD_TIMEOUT));
        assertThat(locationPage.getMapContainer()).isVisible();

        // Step 4 & 5: ბანკომატების ტაბის შემოწმება / ქარდის შემოწმება
        locationPage.clickAtmsTab();
        locationPage.scrollToFirstCard();
        assertThat(locationPage.getFirstAtmAddress()).isVisible();
        assertThat(locationPage.getFirstCardList()).containsText(
                Constants.ATM_CARD_TEXT,
                new LocatorAssertions.ContainsTextOptions().setTimeout(Constants.LOAD_TIMEOUT));



        // ვბრუნდებით ზემოთ, რომ შემდეგ ტაბს დავაკლიკოთ
        locationPage.scrollToTop();

        // Step 6 & 7: ფილიალების ტაბის შემოწმება
        locationPage.clickBranchesTab();
        locationPage.scrollToFirstCard();

        assertThat(locationPage.getFirstAtmAddress()).isVisible();
        assertThat(locationPage.getFirstCardList()).containsText(Constants.WORK_HOURS_WEEKLY);
        assertThat(locationPage.getFirstCardList()).containsText(Constants.WORK_HOURS_SAT);
    }
}
//ტესტ აფეილებს და უნდა ჩაიხედოს