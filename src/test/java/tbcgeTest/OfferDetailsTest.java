package tbcgeTest;

import com.microsoft.playwright.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class OfferDetailsTest {
    static Playwright playwright;
    static Browser browser;
    static Page page;

    @BeforeClass
    public static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(4000) // 1000 მილიწამი = 1 წამი.ვერ ვასწრებდი ბათონზე დაჭერის პროცესს და ცოტა შევანელე
                .setArgs(List.of("--start-maximized")));//--start-maximized გამოვიყენე იმიტომ რომ საიტი მაქსიმალურ ზომაზე გაიხსნას (step 1)

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(null));
        page = context.newPage();
    }

    @Test
    public void ClickOfferDetails() {
        // ნაბიჯი 1: offer pager-ის გვერდზე გადასვლა
        page.navigate("https://tbcbank.ge/ka/offers/all-offers/1o8WOB94RBAZVUFk2uqi5l/extra-offer");
        // ნაბიჯი 1.5: Cookies-ზე დათანხმება
        Locator acceptCookies = page.locator(".primary.state-initial.size-s")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(" თანხმობა | Accept All ", Pattern.CASE_INSENSITIVE)));

        try {
            // ველოდებით მაქსიმუმ 5 წამს, რომ ღილაკი გამოჩნდეს
            acceptCookies.waitFor(new Locator.WaitForOptions().setTimeout(5000));

            if (acceptCookies.isVisible()) {
                acceptCookies.click();
                System.out.println("Cookies-ზე დათანხმება წარმატებით შესრულდა.");
            }
        } catch (Exception e) {
            // თუ 5 წამში არ გამოჩნდა (მაგალითად, უკვე დათანხმებულია), ტესტი არ გაფეილდეს
            System.out.println("Cookies-ის ფანჯარა არ ამოხტა.");
            Locator offerdet = page.locator("span.ng-star-inserted",
                    new Page.LocatorOptions().setHasText("Remittance Fee Calculation")); //არასწორი კოდია - აქ დასაწერი მაქვს თუ ქუქი არ გახსნა, მაშინ სად უნდა გადამისამართდეს იუზერი? ;დდ
            offerdet.click();
    //ნაბიჯი 1: ------Verify offer title (H1) is visible // გვერდის მთავარი სათაური ჩვეულებრივ h1 თეგშია -----
            Locator offerTitle = page.locator("h1");
            assertThat(offerTitle).isVisible();
            System.out.println("სათაური ხილვადია: " + offerTitle.innerText());

    // ნაბიჯი 2: ------ Verify main banner/hero section is visible// ვიყენებ .offer-details-image კლასს ------
            Locator mainBanner = page.locator(".app-textpage-header__media, .offer-image-container img").first();
            assertThat(mainBanner).isVisible();
            System.out.println("მთავარი ბანერი ხილვადია.");

    // ნაბიჯი 3: ----- Verify discount badge or label is visible ----
            Locator discountBadge = page.locator(".tbcx-pw-text-badge.tbcx-pw-text-badge--primary").first();
            assertThat(discountBadge).isVisible();
            assertThat(discountBadge).containsText(Pattern.compile(".*დაიბრუნე 30%.*"));
            System.out.println("ფასდაკლების ლეიბლი ნაპოვნი და ხილვადია.");

    //ნაბიჯი 4: ------Verify offer description/content section is displayed---- - ვეძებთ კონტეინერს, რომელიც შეიცავს შეთავაზების დეტალურ ტექსტს
            Locator descriptionSection = page.locator(".offer-details-content, .rich-text").first();
            descriptionSection.scrollIntoViewIfNeeded();
            assertThat(descriptionSection).isVisible();// ვამოწმებთ, რომ ეს სექცია ხილვადია
            String descriptionText = descriptionSection.innerText(); // დამატებითი შემოწმება: დარწმუნდი, რომ ტექსტი ცარიელი არ არის

            if (!descriptionText.isEmpty()) {
                System.out.println("აღწერის სექცია ნაპოვნია და შეიცავს ტექსტს.");
            } else {
                System.out.println("აღწერის სექცია ნაპოვნია, მაგრამ ცარიელია!");
            }

        }
    }
}