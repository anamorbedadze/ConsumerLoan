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

public class Locations {
    static Playwright playwright;
    static Browser browser;
    static Page page;


    @BeforeClass
    public static void setUp() {
        playwright = Playwright.create();
        // აქ მხოლოდ ბრაუზერს ვუშვებთ, ფანჯარას - არა!
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(500));
    }
    @Test
    public void openMobVersion() {
        // step - 1 : Open homepage in mobile viewport
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(390, 844)
                .setIsMobile(true)
                .setHasTouch(true)
                .setPermissions(java.util.List.of()));

        Page page = context.newPage();
        page.navigate("https://tbcbank.ge/ka");

        try {
            // ვეძებთ ღილაკს ტექსტით "თანხმობა"
            Locator cookieAcceptBtn = page.locator("button:has-text('თანხმობა')");

            // ველოდებით მაქსიმუმ 5 წამს, რომ გამოჩნდეს
            cookieAcceptBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));

            if (cookieAcceptBtn.isVisible()) {
                cookieAcceptBtn.click();
                System.out.println("ქუქიებზე თანხმობა გაიცა.");
                // მცირე პაუზა, რომ ფანჯარა გაქრეს და ეკრანი გათავისუფლდეს
                page.waitForTimeout(1000);
            }
        } catch (Exception e) {
            System.out.println("ქუქიების ფანჯარა არ გამოჩნდა.");
        }

// step -2 --- ბურგერ მენიუს პოვნა და დაჭერა ---
        Locator burgerMenu = page.locator(".tbcx-pw-hamburger-menu__button.icon-flat.size-s.state-initial, button:has(.icon-burger)").first();

        burgerMenu.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        burgerMenu.click();
        System.out.println("ბურგერ მენიუ გაიხსნა.");

        // --- სქროლვა ბოლოში და "მისამართებზე" დაჭერა ---
        Locator addressesBtn = page.locator("a:has-text('მისამართები')").last();
        addressesBtn.evaluate("el => el.scrollIntoView({ behavior: 'smooth', block: 'center' })");//ამით ვანელებ სქროლვის პროცესს
        page.waitForTimeout(2000);
        addressesBtn.click();
        System.out.println("გადავედით მისამართების გვერდზე.");

//step - 3 -- Verify redirection to 'ATMs, Branches and CDMs' page
        // ვამოწმებთ, რომ მისამართში გაჩნდა სიტყვა "ყველა"
        assertThat(page).hasURL(Pattern.compile(".*atms&branches.*"));
        System.out.println("URL ვალიდაცია: წარმატებულია.");
        // ვიყენებთ getByText-ს, რომელიც ეძებს ამ ზუსტ ტექსტს ნებისმიერ თეგში
        Locator pageHeading = page.getByText("ფილიალი, ბანკომატი და თანხის მიმღები").first();
        assertThat(pageHeading).isVisible();
        assertThat(pageHeading).hasText("ფილიალი, ბანკომატი და თანხის მიმღები"); //ტექსტი რომ ემთხვევა იმას ვამოწმებთ
        System.out.println("სათაურის ვალიდაცია ტექსტით: წარმატებულია.");
        //რუკის (Map) გამოჩენის შემოწმება
        Locator mapContainer = page.locator(".tbcx-pw-atm-branches-section-map").first();
        // ველოდებით 10 წამამდე, რადგან რუკას ჩატვირთვა სჭირდება
        mapContainer.waitFor(new Locator.WaitForOptions().setTimeout(10000));
        assertThat(mapContainer).isVisible();
        System.out.println("რუკის ვალიდაცია: რუკა ჩანს ეკრანზე.");

//Step - 4:-- click on ATMs tab
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("ბანკომატები")).click();
        page.waitForTimeout(1000); // მცირე პაუზა, რომ სია განახლდეს
        System.out.println("ბანკომატების ტაბი არჩეულია.");
// Step - 5. --Verify first card contains text 'ATM' and a visible address
        // ვიღებთ პირველ ქარდს სიიდან
        Locator firstAtmCard = page.locator(".tbcx-pw-atm-branches-section__list").first();
        firstAtmCard.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        firstAtmCard.evaluate("el => el.scrollIntoView({ behavior: 'smooth', block: 'center' })");
        page.waitForTimeout(1500);
        System.out.println("ჩამოვსქროლეთ ქარდების სექციამდე.");
        // ვამოწმებთ, რომ შეიცავს ტექსტს (ქართულად "ბანკომატი" ან "ATM")
        assertThat(firstAtmCard).containsText(Pattern.compile(".* ATM  - | 24/7 .*", Pattern.CASE_INSENSITIVE));

        // ვამოწმებთ, რომ მისამართი ხილვადია (ჩვეულებრივ ეს არის ქარდის შიგნით არსებული ტექსტი)
        Locator atmAddress = firstAtmCard.locator(".tbcx-pw-atm-branches-section__list-item-title.tbcx-pw-title").first();
        assertThat(atmAddress).isVisible();
        System.out.println("პირველი ბანკომატის ქარდი ვალიდურია.");
        page.evaluate("window.scrollTo({ top: 0, behavior: 'smooth' })");
        page.waitForTimeout(1500);
//Step - 6. ----Switch to Branches tab
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("ფილიალები")).click();
        page.waitForTimeout(1000); // მცირე პაუზა, რომ სია განახლდეს
        System.out.println("ფილიალების ტაბი არჩეულია.");
//Step - 7 ---- Verify first card contains address and schedule information (Week/Saturday/Sunday hours visible)
        Locator firstBranchCard = page.locator(".tbcx-pw-atm-branches-section__list").first();
        // მისამართის შემოწმება
        assertThat(firstBranchCard.locator(".tbcx-pw-atm-branches-section__list-item-title.tbcx-pw-title").first()).isVisible();
        // სამუშაო საათების შემოწმება (ორშ-პარ, შაბათი)
        // ვიყენებთ containsText-ს, რომ დავრწმუნდეთ, ეს სიტყვები ქარდში წერია
        assertThat(firstBranchCard).containsText(" ორშაბათი-პარასკევი: 10:00-18:00 "); // Week
        assertThat(firstBranchCard).containsText(" შაბათი: 10:00-14:00 ");  // Saturday
        firstAtmCard.evaluate("el => el.scrollIntoView({ behavior: 'smooth', block: 'center' })");
        page.waitForTimeout(1500);
        System.out.println("ჩამოვსქროლეთ ქარდების სექციამდე.");
        System.out.println("ფილიალის სამუშაო საათების ვალიდაცია წარმატებულია.");

        }
    }


