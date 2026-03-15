import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static java.awt.SystemColor.text;

public class ConsumerLoan {
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
    public void clickConsumerLoanTerm() {
        page.navigate("https://tbcbank.ge/ka");
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
        }
//step -1 : Page: Home Loans / Consumer Loan Terms (Apply flow)
        //ჰედერზე "ჩემთვის" მაუსის მიტანა (Hover)
// ვეძებთ div-ს, რომელსაც აქვს ეს ატრიბუტი და შეიცავს ტექსტს "ჩემთვის"
        Locator personalMenu = page.locator(".tbcx-pw-navigation-item__link.ng-star-inserted").filter(new Locator.FilterOptions().setHasText(" ჩემთვის ")).first();
        personalMenu.hover();
        System.out.println("მაუსი მიტანილია 'ჩემთვის' მენიუზე.");
        //  სესხების განყოფილებიდან "სამომხმარებლო"-ს არჩევა
        // ვიყენებთ ლინკს, რომელსაც აქვს შესაბამისი ტექსტი
        Locator consumerLoanLink = page.locator("a:has-text('სამომხმარებლო')").first();
        consumerLoanLink.waitFor(); // დაველოდოთ გამოჩენას
        consumerLoanLink.click();
        System.out.println("არჩეულია სამომხმარებლო სესხი.");

        // ვამოწმებთ, რომ სწორ URL-ზე გადავედით
        assertThat(page).hasURL("https://tbcbank.ge/ka/loans/consumer-loan");

        //  "პირობების" ღილაკზე დაჭერა
        // ვიყენებთ getByRole-ს, რადგან ეს არის ტიპიური Button ელემენტი
        Locator conditionsBtn = page.locator("button.primary.state-initial")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("პირობები", Pattern.CASE_INSENSITIVE)))
                .first();
        conditionsBtn.scrollIntoViewIfNeeded();
        conditionsBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        conditionsBtn.evaluate("el => el.click()");
        System.out.println("პირობების ღილაკზე დაჭერა წარმატებით შესრულდა.");
        page.waitForTimeout(1000);
//step 2:Verify the page main content loads successfully and the Apply button is visible and enabled
        assertThat(page.locator("body")).containsText(Pattern.compile("სესხის მოთხოვნა|საპროცენტო", Pattern.CASE_INSENSITIVE));
//step 3: Click Apply
        // TBC-ს საიტზე ეს ღილაკი ხშირად არის .state-initial კლასით
        Locator applyBtn = page.locator("a, button") //აქ ვეუბნები ან ლინკი მოძებნოს ან ბათონი
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("სესხის მოთხოვნა|Apply", Pattern.CASE_INSENSITIVE)))
                .first();

        Page newPage = page.waitForPopup(() -> { //waitForPopup ნიშნავს, რომ Playwright დაელოდება ახალი ტაბის გახსნას კლიკის მომენტში
            applyBtn.scrollIntoViewIfNeeded();
            applyBtn.click();
        });
        System.out.println("ახალი ფანჯარა გაიხსნა: " + newPage.url());
        newPage.waitForLoadState(LoadState.NETWORKIDLE);
        Locator applyCreditsCookie = newPage.locator("#acceptAllCookies");
        try {
            // ველოდებით მაქსიმუმ 5 წამს, რომ ღილაკი გამოჩნდეს
            applyCreditsCookie.waitFor(new Locator.WaitForOptions().setTimeout(5000));

            if (applyCreditsCookie.isVisible()) {
                applyCreditsCookie.click();
                assertThat(applyCreditsCookie).isHidden();
                System.out.println("ქუქიების პოპ-აპი გაქრა.");
                newPage.waitForTimeout(5000);
                newPage.waitForTimeout(1000);
                newPage.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get("after_click.png")));
            }
        } catch (Exception e) {
            System.out.println("ქუქიების ფანჯარა არ იყო ან უკვე დახურულია.");
        }
//step -4: Validate redirection happens to TBC Credit (assert URL host contains 'tbccredit' / domain is 'tbccredit.ge')
        assertThat(newPage).hasURL(Pattern.compile("tbccredit.ge"));
//step -5: Validate that the destination page loads (e.g., page title or main form/hero section visible)
        Locator heroTitle = newPage.locator("h1");
        assertThat(heroTitle).isVisible();
        System.out.println("გვერდის სათაური ხილვადია: " + heroTitle.innerText());
    }
}