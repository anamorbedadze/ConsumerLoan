import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MoneyTransfer {
    static Playwright playwright;
    static Browser browser;
    static Page page;

    @BeforeClass
    public static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000) // 1000 მილიწამი = 1 წამი.ვერ ვასწრებდი ბათონზე დაჭერის პროცესს და ცოტა შევანელე
                .setArgs(List.of("--start-maximized")));//--start-maximized გამოვიყენე იმიტომ რომ საიტი მაქსიმალურ ზომაზე გაიხსნას (step 1)

        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(null));
        page = context.newPage();
    }

    @Test
    public void clickTransferFeeTab() {
        // ნაბიჯი 1: money transfer-ის გვერდზე გადასვლა
        page.navigate("https://tbcbank.ge/en/other-products/money-transfers");
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
            Locator feeTab = page.locator("span.ng-star-inserted",
                    new Page.LocatorOptions().setHasText("Remittance Fee Calculation"));
            feeTab.click();
        }

        //ნაბიჯი 2: Switch to Remittance Fee Calculation tab - ორივე ტაბს აქვს ერთი და იგივე ინფუთი, რის გამოც პირველ იმფუთს ავსებს ავტომატურად - ეს გასასწორებელი მაქვს და როცა დრო მექნება ჩავასწორებ
        Locator feeTab = page.locator("span:has-text('Remittance Fee Calculation')");
        feeTab.scrollIntoViewIfNeeded();
        feeTab.click();

        //ნაბიჯი 3: Enter amount: 1000
        // fill() მეთოდი ავტომატურად ასუფთავებს გრაფას და წერს ახალს
        Locator amountInput = page.locator(".input-with-label input").first();//რა კლასის აიდიც ავიღე ცოტა გაფუჭებულია,ცვლილების შემთხვევაში უეჭველად შეიცვლება და ტესტს დამიფეილებს. რამდენად სწორი სოლუშენია ასეთ დროს?
        amountInput.fill("1000");
        System.out.println("ჩაიწერა თანხა: 1000");

        //ნაბიჯი 4: Select currency: EUR
        page.locator(".tbcx-label-selected-container").filter(new Locator.FilterOptions().setHasText("GEL")).click();//ჯერ ვაჭერთ ვალუტის დროპდაუნს
        page.locator(".tbcx-dropdown-popover-item__title").filter(new Locator.FilterOptions().setHasText("EUR")).click();
        System.out.println("აირჩია ვალუტა: ვალუტა EUR არჩეულია");

        //ნაბიჯი 5: Select country: Greece
        // ქვეყნის დროპდაუნი ჩვეულებრივ მეორეა (nth(1)), რადგან პირველი ვალუტისაა
        Locator countryDropdown = page.locator("tbcx-dropdown-selector").nth(1);
        countryDropdown.click();

        // ვეძებთ საბერძნეთს პოპაპში.
        // რადგან სია გრძელია, scrollIntoViewIfNeeded() უნდა გამოვიყენო
        Locator greeceOption = page.locator("div.tbcx-dropdown-popover-item__title:has-text('Greece')");
        greeceOption.scrollIntoViewIfNeeded();
        greeceOption.click();
        System.out.println("ნაბიჯი 4: ქვეყანა Greece არჩეულია.");

        // შემოწმება (Assertion)
        assertThat(countryDropdown).containsText("Greece");
        Locator resultSection = page.locator(".tbcx-pw-money-transfer-fee-calculator__cards");//ეს კლასი არასწორად მაქვს მოძებნილი და დრო რომ მექნება ჩავხედავ
        resultSection.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        assertThat(resultSection).isVisible();
        Locator feeValue = resultSection.locator(".tbcx-pw-money-transfer-fee-calculator__cards");//პრობლემა მაქვს იმ კლასის საპოვნელად სადაც საკომისიოს დაანგარიშება ხდება. გვაქვს მაქს 4 მანი ტრანსფერის საკომისიო და ცალ-ცალკე ყველას საკომისიოს რომ შევხედო უფრო ჩაშლილად დამჭირდება კოდის დაწერა
        assertThat(feeValue).not().isEmpty();
        assertThat(feeValue).containsText(Pattern.compile("\\d"));

        System.out.println("✅ შედეგები წარმატებით დადასტურდა!");
    }

        @AfterClass
        public static void tearDown () {
            // რესურსების დახურვა (Cleanup)
            if (browser != null) {
                browser.close();
                playwright.close();
            }
        }
    }