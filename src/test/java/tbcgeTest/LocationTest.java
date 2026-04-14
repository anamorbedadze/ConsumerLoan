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

// package tbcgeTest; (ან სადაც გაქვს ტესტები)

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import steps.LocationSteps;

public class LocationTest extends BaseTest {
    private LocationSteps locationSteps;

    @BeforeMethod
    public void setupTest() {
        // Steps ობიექტის ინიციალიზაცია BaseTest-ის page-ით
        locationSteps = new LocationSteps(page);
    }

    @Test
    public void openMobVersion() {
        // Step 1: ნავიგაცია მისამართებზე და გვერდის ჩატვირთვის ვალიდაცია
        locationSteps.navigateToLocationsAndVerify();

        // Step 2: ბანკომატების (ATMs) ინფორმაციის შემოწმება
        locationSteps.verifyAtmsTabContent();

        // Step 3: ფილიალების (Branches) სამუშაო საათების შემოწმება
        locationSteps.verifyBranchesTabContent();

        System.out.println("მობაილ ვერსიაში ლოკაციების ფლოუ წარმატებით დასრულდა.");
    }
}