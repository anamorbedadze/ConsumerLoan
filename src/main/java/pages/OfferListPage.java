package pages;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.data.Constants;
import java.util.regex.Pattern;

public class OfferListPage {
    private final Page page;

    // 1. ლოკატორების დეკლარაცია
    private final Locator acceptCookiesBtn;
    private final Locator filterPanel;
    private final Locator offerListContainer;
    private final Locator emptyStateMessage;
    private final Locator offerCards;
    private final Locator clearButtons;

    public OfferListPage(Page page) {
        this.page = page;

        // 2. ლოკატორების ინიციალიზაცია
        this.acceptCookiesBtn = page.locator(".primary.state-initial.size-s")
                .filter(new Locator.FilterOptions().setHasText(Constants.COOKIE_ACCEPT_PATTERN));

        this.filterPanel = page.locator(".filters.ng-star-inserted");

        this.offerListContainer = page.locator(".marketing__cards-list.ng-star-inserted");

        this.emptyStateMessage = page.locator(".offers__empty-state");

        this.offerCards = page.locator(".marketing__cards-list .tbcx-pw-card");

        String clearBtnSelector = String.format("button:has-text('%s'), button:has-text('%s')",
                Constants.CLEAR_BUTTON_GEO, Constants.CLEAR_BUTTON_ENG);
        this.clearButtons = page.locator(clearBtnSelector);
    }

    // 3. Getters (სტატიკური ლოკატორებისთვის)
    public Locator getAcceptCookiesBtn() { return acceptCookiesBtn; }
    public Locator getFilterPanel() { return filterPanel; }
    public Locator getOfferListContainer() { return offerListContainer; }
    public Locator getEmptyStateMessage() { return emptyStateMessage; }
    public Locator getOfferCards() { return offerCards; }
    public Locator getClearButtons() { return clearButtons; }

    // 4. დინამიური ლოკატორი (მეთოდი, რადგან არგუმენტზეა დამოკიდებული)
    public Locator getFilterCheckbox(Pattern pattern) {
        return page.locator(".filter-item__label")
                .filter(new Locator.FilterOptions().setHasText(pattern));
    }

    // 5. Page Actions (დამხმარე მეთოდები)
    public void scrollToTop() {
        page.evaluate("window.scrollTo(0, 0)");
    }

    public void scrollDownSlightly() {
        page.evaluate("window.scrollBy(0, 300)");
    }
}
