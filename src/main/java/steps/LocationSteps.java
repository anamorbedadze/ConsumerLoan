package steps;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.example.data.Constants;
import pages.LocationPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.regex.Pattern;

public class LocationSteps {
    private final Page page;
    private final LocationPage locationPage;

    public LocationSteps(Page page) {
        this.page = page;
        this.locationPage = new LocationPage(page);
    }

    // ნაბიჯი 1 & 2 & 3: მთავარი გვერდიდან მისამართებზე გადასვლა და ჩატვირთვის შემოწმება
    public void navigateToLocationsAndVerify() {
        page.navigate(Constants.BASE_URL);
        locationPage.acceptCookies();
        locationPage.openAddressesFromMenu();

        // ვალიდაცია
        assertThat(page).hasURL(Pattern.compile(Constants.LOCATION_URL));
        assertThat(locationPage.getPageHeading()).isVisible();

        // რუკის ჩატვირთვის მოლოდინი და ვალიდაცია
        locationPage.getMapContainer().waitFor(new Locator.WaitForOptions().setTimeout(Constants.MAP_LOAD_TIMEOUT));
        assertThat(locationPage.getMapContainer()).isVisible();
    }

    // ნაბიჯი 4 & 5: ბანკომატების ტაბზე გადასვლა და პირველი ქარდის შემოწმება
    public void verifyAtmsTabContent() {
        locationPage.clickAtmsTab();
        locationPage.scrollToFirstCard();

        assertThat(locationPage.getFirstAtmAddress()).isVisible();

        // ვამოწმებთ, რომ კონტეინერი შეიცავს ATM-ის დამახასიათებელ ტექსტს
        assertThat(locationPage.getFirstCardList()).containsText(
                Constants.ATM_CARD_TEXT,
                new LocatorAssertions.ContainsTextOptions().setTimeout(Constants.LOAD_TIMEOUT));
    }

    // ნაბიჯი 6 & 7: ფილიალების ტაბზე გადასვლა და სამუშაო საათების შემოწმება
    public void verifyBranchesTabContent() {
        locationPage.scrollToTop(); // ვბრუნდებით ზემოთ ტაბების დასანახად
        locationPage.clickBranchesTab();
        locationPage.scrollToFirstCard();

        assertThat(locationPage.getFirstAtmAddress()).isVisible();

        // ვამოწმებთ განრიგის არსებობას კონტეინერში
        assertThat(locationPage.getFirstCardList()).containsText(Constants.WORK_HOURS_WEEKLY);
        assertThat(locationPage.getFirstCardList()).containsText(Constants.WORK_HOURS_SAT);
    }
}