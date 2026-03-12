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

public class OfferList {
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
    public void ClickAllOfferPage() {
        page.navigate("https://tbcbank.ge/ka/offers/all-offers");
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
//step -1 :Open Offers list page and verify filter panel and offers list container are visible
        Locator FilterPanel = page.locator(".filters.ng-star-inserted");
        assertThat(FilterPanel).isVisible();
    //assertThat(Page page) - ამოწმებს გვერდს, URL, გვერდზე ტექსტის ხილვადობა
    //assertThat(Locator locator) - ამოწმებს ელემენტს, მაგ. .isVisible() .isHidden() .hasText("...") .isChecked()
    //assertThat(APIResponse response) — API-ს შემოწმება, მაგ. .isOK() ან .hasStatus(404)
        System.out.println("ფილტრების პანელო ხილვადია");
        Locator OfferList = page.locator(".marketing__cards-list.ng-star-inserted");
        assertThat(OfferList).isVisible();
        System.out.println("ყველა შეთავაზება ხილვადია");

//step - 2: Select filter: Payment system → Mastercard
        Locator MastercardCheckbox= page.locator(".filter-item__label")
             .filter(new Locator.FilterOptions().setHasText(Pattern.compile(" მასტერქარდი | MasterCard", Pattern.CASE_INSENSITIVE)));
        //Pattern.compile იმიტომ ვიყენებ რომ ტექსტი სფეისებით წერია, ამ ფუნქციით სფეისებსაც უგულვეყოფს კოდი და დიდ/პატარა ასოებსაც, იმ შემთხვევაში თუ შეიცვალა მასტერქარდის ტექსტი
        MastercardCheckbox.scrollIntoViewIfNeeded();
        MastercardCheckbox.click();
        System.out.println("MasterCard ფილტრი წარმატებით მოინიშნა.");
        assertThat(MastercardCheckbox.locator("input")).isChecked();
        page.evaluate("window.scrollTo(0, 0)"); //ამის საშუალებით გაფილტრული ინფორმაციის ფეიჯი თავში აისქროლება

//Step-3 :Validate that an empty state is shown (e.g., 'No offers found') OR results count is 0
     //იმისთვის რომ შედეგი ვერ ვიპოვოთ, საჭიროა მოვნიშნოთ მასტერქარდი და სხვა სცეპიფიკური გასაფილტრი(მაგ პარტნიორების შეთავაზება) რაც მოგვცემს შედეგს-0
        Locator ParentOffer = page.locator(".filter-item__label")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile(" პარტნიორების შეთავაზება | Partner Offers", Pattern.CASE_INSENSITIVE)));
        ParentOffer.scrollIntoViewIfNeeded();
        ParentOffer.click();
        System.out.println("Parent Offers ფილტრი წარმატებით მოინიშნა.");
        page.evaluate("window.scrollTo(0, 0)"); //ნიშნავს საიტის თავში ასქროლვას, იწერება მაშინ როცა ჩექის მოქმედება დასრულდა და შემოწმებას სანამ გაივლის მანამდე უნდა ჩაისვას
        assertThat(ParentOffer.locator("input")).isChecked();
        Locator emptyStateMessage = page.locator(".offers__empty-state");
        assertThat(emptyStateMessage).isVisible();
        assertThat(emptyStateMessage).hasText(Pattern.compile(" შეთავაზებები არ მოიძებნა | Offer wasn't found ", Pattern.CASE_INSENSITIVE)); //ვამოწმებ რომ ხილვადობოს მხრივ, ნამდვილად ხილვადია

//Step-4: Validate that no offer cards are displayed in the list/grid- ეკრანზე არცერთი შეთავაზების ქარდი აღარ არსებობს
        Locator OfferCards = page.locator(".marketing__cards-list.offer-card");
        //marketing__cards-list.offer-card"ნიშნავს: იპოვე .offer-card ელემენტები ამ სიის შიგნით - იმიტომ დავადე რომ კონტეინერი სულ იარსებებს და სისტემა მას 1-ად ჩათვლის, რომ აზრი ჰქონდეს ამ ჩეკს მე ვეუბნები რომ კონტეინერში იპოვოს ქარდები და არა ქარდების კონტეინერი იპოვოს
        assertThat(OfferCards).hasCount(0);
        int actualCount= OfferCards.count();
        if (actualCount==0) {
                System.out.println("ვალიდაცია წარმატებულია: სიაში 0 შეთავაზებაა.");
        }
//Step -5: Click Clear / Reset filters and verify offers list is populated again (at least 1 offer card visible)
        Locator clearBtn = page.locator("button:has-text('გასუფთავება'), button:has-text('Clear')").first();;
        clearBtn.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        clearBtn.scrollIntoViewIfNeeded();
        clearBtn.focus();
        clearBtn.click(new Locator.ClickOptions().setForce(true));
        System.out.println("ფილტრები გასუფთავდა");
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(1000);
        Locator offerCards = page.locator(".offer-card"); //- აქ გავიჭედე და გავასწორო...ვამოწმებთ რომ ქარდების სია კვლავ შეივსო და 1 ბარათი მაინც ჩანს
        offerCards.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(15000));
        int currentCount = offerCards.count();
        if (currentCount > 0) {
            System.out.println("ვალიდაცია წარმატებულია: სია კვლავ შეივსო. ნაპოვნია " + currentCount + " ბარათი.");
        } else {
            System.out.println("შეცდომა: ფილტრების გასუფთავების შემდეგ სია კვლავ ცარიელია.");
        }
}
}





