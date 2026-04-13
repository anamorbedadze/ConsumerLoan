package tbcgeTest;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MoneyTransferTest {
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
            Locator feeTab = page.locator("span.ng-star-inserted");
        }
//ნაბიჯი 1 : --- Open Money Transfers page and verify main content is visible ---
        Locator mainTitle = page.getByText("Quick money transfers").first();
        assertThat(mainTitle).isVisible();
        System.out.println("გვერდი წარმატებით გაიხსნა.");

//ნაბიჯი 2: ---Switch to Remittance Fee Calculation tab - ორივე ტაბს აქვს ერთი და იგივე ინფუთი, რის გამოც პირველ იმფუთს ავსებს ავტომატურად - ეს გასასწორებელი მაქვს და როცა დრო მექნება ჩავასწორებ
        Locator feeCalcTab = page.getByText("Remittance Fee Calculation").first();
        feeCalcTab.scrollIntoViewIfNeeded();
        feeCalcTab.click();

//ნაბიჯი 3: ---------Enter amount: 1000-----------
        // fill() მეთოდი ავტომატურად ასუფთავებს გრაფას და წერს ახალს
        Locator amountInput = page.locator(".input-with-label input").first();//რა კლასის აიდიც ავიღე ცოტა გაფუჭებულია,ცვლილების შემთხვევაში უეჭველად შეიცვლება და ტესტს დამიფეილებს. რამდენად სწორი სოლუშენია ასეთ დროს?
        amountInput.fill("1000");
        System.out.println("ჩაიწერა თანხა: 1000");

//ნაბიჯი 4:------- Select currency: EUR-------
        page.locator("tbcx-dropdown-selector").first().click(); //აქ მინდა რომ ნებისმიერი ვალუტით დაადროფდაუნოს
        page.locator(".tbcx-dropdown-popover-item__title").filter(new Locator.FilterOptions().setHasText("EUR")).click();
        System.out.println("ვალუტა EUR არჩეულია");

//ნაბიჯი 5: --------Select country: Greece-----
        // ქვეყნის დროპდაუნი ჩვეულებრივ მეორეა (nth(1)), რადგან პირველი ვალუტისაა
        Locator countryDropdown = page.locator("tbcx-dropdown-selector").nth(1);
        countryDropdown.click();

        // ვეძებთ საბერძნეთს პოპაპში
        Locator greeceOption = page.locator("div.tbcx-dropdown-popover-item__title:has-text('Greece')");
        greeceOption.evaluate("el => el.scrollIntoView({ behavior: 'smooth', block: 'center' })");
        greeceOption.click();
        System.out.println(" ქვეყანა Greece არჩეულია.");
        page.waitForTimeout(20000); //ხან იტვირთება და ხან ბევრი დრო სჭირდება, დამაწყდა ნერვები :))

// ნაბიჯი 6: -------------Verify calculation result section appears---
        // ვპოულობთ მთლიან სექციას, სადაც შედეგები უნდა გამოჩნდეს
        Locator resultSection = page.locator(".tbcx-pw-money-transfer-fee-calculator__cards").first();
        resultSection.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(15000));
        page.waitForTimeout(1000);
        assertThat(resultSection).isVisible();
        System.out.println("ვალიდაცია წარმატებულია: შედეგები ჩაიტვირთა და გამოჩნდა.");
// ნაბიჯი  7: ----- Verify result values are displayed and not empty ---
        //მინდოდა ლოგოს, საკომისიოს და ტრანსფერის დასახელების მიხედვით გამეჩექა მარა ამ კლასის პოვნას ვერ ასწრებს - .tbcx-pw-card__info // სხვა რაღაც ეფარება და შეგვიძლია ისეთი კოდის დაწერა რომ რამე თუ გეფარება აღარ მოგეფაროს

        Locator resultValue = page.locator(".tbcx-pw-money-transfer-fee-calculator__cards").first();
        resultValue.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(10000));
        assertThat(resultValue).not().isEmpty(); // უბრალოდ ვამოწმებთ, რომ კონტეინერი ცარიელი არაა
        assertThat(resultValue).containsText(Pattern.compile(" Ria | MoneyGram | IntelExpress | WesternUnion ", Pattern.CASE_INSENSITIVE));
        assertThat(resultValue).containsText("€"); //საკომისიოს ვალუტის სიმბოლოც მოყვება და კარგი იქნება ამასაც თუ შევამოწმებ
        assertThat(resultValue).containsText(Pattern.compile("\\d")); //ვამოწმებ რომ მინ ერთი რიცხვი მაინც წერია
        System.out.println("ვალიდაცია წარმატებულია: მთავარ კონტეინერში შედეგები ნაპოვნია!");
        //აქ შემეძლო

//ნაბიჯი 8: ----Verify no error message is shown ---
        //კონკრეტული კლასი ვერ ვიპოვე ერორების, ამიტომ ვტესავ ნებისმიერ ელემენტს, რომელსაც კლასში ეწერება ერორი
        Locator anyError = page.locator("[class*='error'], [class*='Invalid']").first();
        assertThat(anyError).isHidden(); // ვამოწმებთ, რომ ასეთი ელემენტი არ არის ხილვადი (Hidden)
        System.out.println("ტესტი დასრულებულია წარმატებით: შეცდომები არ დაფიქსირებულა.");
        }
    }