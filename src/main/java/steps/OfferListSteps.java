package steps;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.example.data.Constants;
import pages.OfferListPage;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OfferListSteps {
    private final Page page;
    private final OfferListPage offerListPage;

    public OfferListSteps(Page page) {
        this.page = page;
        // აქ ხდება Page კლასის ინიციალიზაცია, რომ მისი გეთერები გამოვიყენოთ
        this.offerListPage = new OfferListPage(page);
    }

    // 1. ნავიგაცია მთავარ გვერდზე
    public OfferListSteps navigateToOffersPage() {
        page.navigate(Constants.OFFER_LIST_URL);
        return this;
    }

    // 2. Cookies-ზე დათანხმება
    public OfferListSteps handleCookies() {
        Locator acceptCookies = offerListPage.getAcceptCookiesBtn();
        try {
            acceptCookies.waitFor(new Locator.WaitForOptions().setTimeout(Constants.LOAD_TIMEOUT));
            if (acceptCookies.isVisible()) {
                acceptCookies.click();
                System.out.println("Cookies-ზე დათანხმება წარმატებით შესრულდა.");
            }
        } catch (Exception e) {
            System.out.println("Cookies-ის ფანჯარა არ ამოხტა.");
        }
        return this;
    }

    // 3. მთავარი ელემენტების ხილვადობის შემოწმება
    public OfferListSteps verifyPageElementsVisible() {
        assertThat(offerListPage.getFilterPanel()).isVisible();
        System.out.println("ფილტრების პანელი ხილვადია");

        assertThat(offerListPage.getOfferListContainer()).isVisible();
        System.out.println("ყველა შეთავაზების კონტეინერი ხილვადია");
        return this;
    }

    // 4. ფილტრების მონიშვნა (0 შედეგის მისაღებად)
    public OfferListSteps selectFiltersForEmptyResult() {
        // Mastercard ფილტრის მონიშვნა
        Locator mastercardCb = offerListPage.getFilterCheckbox(Constants.MASTERCARD_FILTER_PATTERN);
        mastercardCb.scrollIntoViewIfNeeded();
        mastercardCb.click();
        assertThat(mastercardCb.locator("input")).isChecked();
        System.out.println("MasterCard ფილტრი წარმატებით მოინიშნა.");
        offerListPage.scrollToTop();

        // Partner Offers ფილტრის მონიშვნა
        Locator partnerCb = offerListPage.getFilterCheckbox(Constants.PARTNER_OFFERS_PATTERN);
        partnerCb.scrollIntoViewIfNeeded();
        page.waitForTimeout(2000); // მცირე დაყოვნება UI ანიმაციისთვის
        partnerCb.click();
        offerListPage.scrollToTop();
        page.waitForTimeout(2000);
        assertThat(partnerCb.locator("input")).isChecked();
        System.out.println("Partner Offers ფილტრი წარმატებით მოინიშნა.");

        return this;
    }

    // 5. ცარიელი მდგომარეობის (Empty State) ვალიდაცია
    public OfferListSteps validateEmptyState() {
        Locator emptyStateMessage = offerListPage.getEmptyStateMessage();
        assertThat(emptyStateMessage).isVisible();
        assertThat(emptyStateMessage).hasText(Constants.NOT_FOUND_PATTERN);

        Locator offerCards = offerListPage.getOfferCards();
        assertThat(offerCards).hasCount(0);
        System.out.println("ვალიდაცია წარმატებულია: სიაში 0 შეთავაზებაა.");

        return this;
    }

    // 6. მხოლოდ ფილტრის გასუდთავება
    public OfferListSteps clearFilters() {
        Locator clearButtons = offerListPage.getClearButtons();
        int buttonsCount = clearButtons.count();
        System.out.println("ნაპოვნია " + buttonsCount + " გასუფთავების ღილაკი.");

        for (int i = 0; i < buttonsCount; i++) {
            Locator currentBtn = offerListPage.getClearButtons().first();
            currentBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            currentBtn.scrollIntoViewIfNeeded();
            currentBtn.click(new Locator.ClickOptions().setForce(true));
            System.out.println((i + 1) + "-ე ფილტრი გასუფთავდა.");
            //page.waitForTimeout(Constants.LOAD_TIMEOUT);
        }

        offerListPage.scrollToTop();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        offerListPage.scrollDownSlightly();
       // page.waitForTimeout(Constants.LOAD_TIMEOUT);

        return this;
    }
    // 7. ვაბრუნებთ ქარდების რაოდენობას (რათა Test-ში შევამოწმოთ)
    public int getVisibleOfferCardsCount() {
        Locator cardsAfterReset = offerListPage.getOfferCards();

        // ველოდებით პირველი ქარდის გამოჩენას, რომ დავრწმუნდეთ DOM განახლდა
        cardsAfterReset.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        return cardsAfterReset.count();
    }
}