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
// Step -1 -- Verify page and calculator section load successfully
    public void testLoanCalculatorFlow() {
            // Verify page load - პირდაპირ გადავდივართ დავალების URL-ზე
        page.navigate("https://tbcbank.ge/ka/loans/consumer-loan/digital");

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
            // ვამოწმებთ კალკულატორის სექციის არსებობას

        Locator calculatorSection = page.locator("tbcx-pw-calculator");
        calculatorSection.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        calculatorSection.scrollIntoViewIfNeeded();
        System.out.println("ნაბიჯი 1: გვერდი და კალკულატორი წარმატებით ჩაიტვირთა.");

//Step - 2. --Set loan amount to 3000 GEL--
            // fill() მეთოდი ავტომატურად ასუფთავებს გრაფას და წერს ახალს
        // იპოვე ყველა input, რომლის ტიპია number და აიღე პირველივე
        Locator amountInput = page.locator("input[type='number']").first();
        amountInput.fill("3000");
        System.out.println("ჩაიწერა თანხა: 3000");

//Step -3 ----Set loan duration to 48 months-----
        Locator periodInput = page.locator("input[type='number']").nth(1);
        periodInput.fill("48");
        System.out.println("ჩაიწერა ვადა: 48");
// პატარა პაუზა, რომ დაინახოთ ცვლილება (სურვილისამებრ)
        page.waitForTimeout(10000);

//Step 4. ----- Verify calculated values are displayed (მაგალითად, ყოველთვიური გადასახადი)
        Locator monthlyPayment = page.locator(".tbcx-pw-calculated-info__number--new").first();
        monthlyPayment.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        //ვამოწმებთ, რომ ტექსტი არ არის ცარიელი და შეიცავს რიცხვებს/ლარის სიმბოლოს
        assertThat(monthlyPayment).not().isEmpty();
        String firstResult = monthlyPayment.innerText();
        System.out.println("ნაბიჯი 4: გამოთვლილი ყოველთვიური შენატანი გამოჩნდა: " + firstResult);

// Step 5: ---- Change amount or duration and verify results update ----
        amountInput.fill("777");
        periodInput.fill("33");
        page.waitForTimeout(1000);
        System.out.println("მნიშვნელობები განახლდა: 777 ლარი, 33 თვე");
        // საბოლოო შემოწმება (Assertion)
        // თუ მნიშვნელობა არ იქნება 777 ან 33, ტესტი აქ გაჩერდება და დაწერს შეცდომას
        assertThat(amountInput).hasValue("777");
        assertThat(periodInput).hasValue("33");
        System.out.println("მნიშვნელობები წარმატებით შეიცვალა და დადასტურდა: 777 ლარი, 33 თვე.");
        String secondResult = monthlyPayment.innerText();
        if (!firstResult.equals(secondResult)) {
            System.out.println("შედეგი წარმატებით განახლდა: " + secondResult);
        } else {
            throw new AssertionError("შეცდომა: მონაცემების შეცვლის მიუხედავად, გამოთვლილი თანხა არ შეიცვალა!");
        }
        assertThat(monthlyPayment).containsText(Pattern.compile("[0-9]")); //ამით ვამოწმებ რომ ციფრებს შეიცავს ნამდვილად
        }
    }


