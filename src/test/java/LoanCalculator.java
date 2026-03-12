import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoanCalculator {
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
    public void testLoanCalculator() {
        // ნაბიჯი 1: საწყის გვერდზე გადასვლა (აუცილებელია მოქმედებების დასაწყებად)
        page.navigate("https://tbcbank.ge");
        // ნაბიჯი 1.5: Cookies-ზე დათანხმება
        Locator acceptCookies = page.locator(".primary.state-initial.size-s")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(" თანხმობა |Accept", Pattern.CASE_INSENSITIVE)));

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

        //ნაბიჯი 2: ჩამოსქროლვა და "გამოთვალე სესხის" ბარათზე დაჭერა
        System.out.println("გვერდის სათაურია: გამოთვალე სესხი");
        Locator loanCard = page.locator(".tbcx-pw-card__title") // ჯერ ვეძებთ კლასით
                .filter(new Locator.FilterOptions().setHasText("გამოთვალე სესხი")); // შემდეგ ვფილტრავთ ტექსტით
        loanCard.scrollIntoViewIfNeeded();//ავტომატურად ჩამოსქროლავს ელემენტამდე
        loanCard.click();
                // ნაბიჯი 3: "სესხის მოთხოვნა" ღილაკზე დაჭერა
        System.out.println("სესხის მოთხოვნა");
        Locator requestButton = page.locator("button.primary.state-initial.size-m").first();
        requestButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        requestButton.click(); // დაველოდოთ სანამ ღილაკი ხილვადი გახდება

               // ნაბიჯი 4: სესხის კალკულატორის გვერდზე გადასვლა
        page.navigate("https://tbccredit.ge/?source_caller=ui&shortlink=ularj23r&c=Acq_FCL_7_tbccredit.ge_Prompt_7&pid=tbccredit.ge&deep_link_value=offers%2F81621&af_xp=custom");
              // ნაბიჯი 4.5: Cookies-ზე დათანხმება (ID-ით და ტექსტის ფილტრით)
        acceptCookies = page.locator("#acceptAllCookies")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("თანხმობა|Accept|Agree", Pattern.CASE_INSENSITIVE)));

        try {
            // ველოდებით მაქსიმუმ 5 წამს, რადგან ქუქი შეიძლება წამიერი დაგვიანებით ამოხტეს
            acceptCookies.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            if (acceptCookies.isVisible()) {
                acceptCookies.click();
                System.out.println("Cookies-ზე დათანხმება შესრულდა.");
            }
        } catch (Exception e) {
            // თუ ქუქი არ გამოჩნდა, ტესტი არ გაფეილდეს და გაგრძელდეს
            System.out.println("Cookies-ის ფანჯარა არ ამოხტა ან უკვე დათანხმებულია.");
        }
           // ნაბიჯი 5: ვამოწმებთ, რომ გვერდი და კალკულატორის სექცია ჩაიტვირთა
        Assert.assertTrue(
                page.isVisible("text=სესხის კალკულატორი") || page.isVisible(".h3.m-b-3"),
                "შეცდომა: მომხმარებელი არ გადავიდა კალკულატორის გვერდზე!"
        );
         // ნაბიჯი 7: ვპოულობთ "ეფექტური პროცენტის" ტექსტს და ვსქროლავთ მასთან
        Locator effectiveRateLabel = page.locator("#standard-calculator").locator("div.title:has-text('ეფექტური პროცენტი')");
        effectiveRateLabel.scrollIntoViewIfNeeded(); //'ეფექტური პროცენტი' ელემენტი ორ ადგილას იუო გამოყენებული, რის გამოც დაკონკრეტება მომიწია

        // ნაბიჯი 8: თანხის ჩაწერა (3000)
        // fill() მეთოდი ავტომატურად ასუფთავებს გრაფას და წერს ახალს
        Locator amountInput = page.locator("#standard-calculator-amount");
        System.out.println("ჩაიწერა თანხა: 3000");

       //ნაბიჯი 9. ვადის (თვის) გრაფის პოვნა და ჩაწერა (48)
        Locator periodInput = page.locator("#standard-calculator-period");
        periodInput.fill("48");
        System.out.println("ჩაიწერა ვადა: 48");

// პატარა პაუზა, რომ დაინახოთ ცვლილება (სურვილისამებრ)
        page.waitForTimeout(10000);

// ნაბიჯი 10. რიცხვების შესაყვანი გრაფის ცვლილება (3000 -> 777 და 48 -> 33)
        amountInput.fill("777");
        periodInput.fill("33");
        System.out.println("მნიშვნელობები განახლდა: 777 ლარი, 33 თვე");
        // საბოლოო შემოწმება (Assertion)
// თუ მნიშვნელობა არ იქნება 777 ან 33, ტესტი აქ გაჩერდება და დაწერს შეცდომას
        assertThat(amountInput).hasValue("777");
        assertThat(periodInput).hasValue("33");

        System.out.println("✅ მნიშვნელობები წარმატებით შეიცვალა და დადასტურდა: 777 ლარი, 33 თვე.");
    }


    @AfterClass
    public static void tearDown() {
        // რესურსების დახურვა (Cleanup)
        if (browser != null) {
            browser.close();
            playwright.close();
        }
    }
}


