package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.data.Constants;


public class OfferDetailsPage {
    private Page page;

    // ლოკატორების დეკლარაცია
    private final Locator acceptCookiesBtn;
    private final Locator offerTitle;
    private final Locator mainBanner;
    private final Locator discountBadge;
    private final Locator descriptionSection;

    public OfferDetailsPage(Page page) {
        this.page = page;

        // ლოკატორების ინიციალიზაცია
        this.acceptCookiesBtn = page.locator(".primary.state-initial.size-s")
                .filter(new Locator.FilterOptions().setHasText(Constants.COOKIE_ACCEPT_PATTERN));
        this.offerTitle = page.locator("h1");

        this.mainBanner = page.locator(".app-textpage-header__media, .offer-image-container img").first();

        this.discountBadge = page.locator(".tbcx-pw-text-badge.tbcx-pw-text-badge--primary").first();

        this.descriptionSection = page.locator("ngx-contentful-rich-text").first();
    }

    // Getters
    public Locator getAcceptCookiesBtn() { return acceptCookiesBtn; }
    public Locator getOfferTitle() { return offerTitle; }
    public Locator getMainBanner() { return mainBanner; }
    public Locator getDiscountBadge() { return discountBadge; }
    public Locator getDescriptionSection() { return descriptionSection; }
}
