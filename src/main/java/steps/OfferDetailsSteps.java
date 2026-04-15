package steps;

import com.microsoft.playwright.Page;
import pages.OfferDetailsPage;

import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OfferDetailsSteps {
    private final Page page;
    private final OfferDetailsPage offerPage;

    public OfferDetailsSteps(Page page) {
        this.page = page;
        this.offerPage = new OfferDetailsPage(page);
    }

    public void navigateToOfferPage(String url) {
        page.navigate(url);
    }

    public void handleCookies() {
        try {
            // ველოდებით მაქსიმუმ 5 წამს
            offerPage.getAcceptCookiesBtn().waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(5000));
            if (offerPage.getAcceptCookiesBtn().isVisible()) {
                offerPage.getAcceptCookiesBtn().click();
                System.out.println("Cookies-ზე დათანხმება წარმატებით შესრულდა.");
            }
        } catch (Exception e) {
            // თუ არ ამოხტა, უბრალოდ ვაგრძელებთ ტესტს, რადგან გვერდი უკვე ჩატვირთულია.
            System.out.println("Cookies-ის ფანჯარა არ ამოხტა (ან უკვე დათანხმებულია). ვაგრძელებთ ტესტს...");
        }
    }

    public void verifyOfferTitleIsVisible() {
        assertThat(offerPage.getOfferTitle()).isVisible();
        System.out.println("სათაური ხილვადია: " + offerPage.getOfferTitle().innerText());
    }

    public void verifyMainBannerIsVisible() {
        assertThat(offerPage.getMainBanner()).isVisible();
        System.out.println("მთავარი ბანერი ხილვადია.");
    }

    public void verifyDiscountBadgeIsVisible() {
        assertThat(offerPage.getDiscountBadge()).isVisible();
        assertThat(offerPage.getDiscountBadge()).containsText(Pattern.compile(".*დაიბრუნე 30%.*"));
        System.out.println("ფასდაკლების ლეიბლი ნაპოვნი და ხილვადია.");
    }

    public void verifyDescriptionSectionIsDisplayed() {
        offerPage.getDescriptionSection().scrollIntoViewIfNeeded();
        assertThat(offerPage.getDescriptionSection()).isVisible();

        String descriptionText = offerPage.getDescriptionSection().innerText();
        // შემოწმება რომ ტექსტი ცარიელი არ არის
        if (!descriptionText.trim().isEmpty()) {
            System.out.println("აღწერის სექცია ნაპოვნია და შეიცავს ტექსტს.");
        } else {
            throw new AssertionError("აღწერის სექცია ნაპოვნია, მაგრამ ცარიელია!");
        }
    }
}