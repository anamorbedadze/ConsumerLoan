package tbcgeTest; // შენი ტესტების პაკეტის სახელი

import basetest.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.example.data.Constants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import steps.MoneyTransferSteps;

// import org.example.data.Constants; // თუ URL-იც კონსტანტებში გაიტანე, ეს დაგჭირდება

import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

// ვაკეთებთ extends BaseTest-ს, რათა ბრაუზერი და page ავტომატურად გაიხსნას/დაიხუროს
public class MoneyTransferTest extends BaseTest {

    private MoneyTransferSteps steps;

    @BeforeMethod
    public void initSteps() {
        // BaseTest-დან მემკვიდრეობით მიღებულ 'page' ცვლადს ვაწოდებთ Steps კლასს
        steps = new MoneyTransferSteps(page);
    }

    @Test
    public void calculateTransferFeeToGreece() {
        // ნაბიჯი 1: გვერდის გახსნა და Cookies-ზე დათანხმება
        // შეგიძლია პირდაპირ ლინკი ჩაწერო, ან თუ Constants-ში გაიტანე - Constants.MONEY_TRANSFER_URL
        steps.openPageAndHandleCookies(Constants.MONEY_TRANSFER_URL);

        // ვალიდაცია 1: ვამოწმებთ, რომ მთავარი სათაური ხილვადია
        Locator mainTitle = steps.getPage().getMainTitle();
        assertThat(mainTitle).isVisible();
        System.out.println("გვერდი წარმატებით გაიხსნა და მთავარი სათაური გამოჩნდა.");

        // ნაბიჯი 2: ტაბის შეცვლა
        steps.switchToFeeCalculationTab();

        // ნაბიჯი 3, 4, 5: მონაცემების შევსება (1000, EUR, Greece)
        steps.fillTransferDetails("1000", "EUR", "Greece");

        // ნაბიჯი 6 და 7: შედეგების სექციის მოლოდინი და ვალიდაცია
        Locator resultSection = steps.getPage().getResultSection();


        resultSection.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.ATTACHED)
                .setTimeout(Constants.LOAD_TIMEOUT));

        // შედეგების ვალიდაციები
        assertThat(resultSection).isVisible(); // სექცია გამოჩნდა
        assertThat(resultSection).not().isEmpty(); // სექცია ცარიელი არ არის
        assertThat(resultSection).containsText(Pattern.compile(Constants.MONEY_TRANSFER_NAME, Pattern.CASE_INSENSITIVE));
        assertThat(resultSection).containsText("€"); // ვალუტის სიმბოლო წერია
        assertThat(resultSection).containsText(Pattern.compile("\\d")); // მინიმუმ ერთი რიცხვი (თანხა/საკომისიო) წერია
        System.out.println("ვალიდაცია წარმატებულია: მთავარ კონტეინერში შედეგები ნაპოვნია!");

        // ნაბიჯი 8: ერორების არარსებობის შემოწმება
        Locator anyError = steps.getPage().getAnyError();
        assertThat(anyError).isHidden(); // ვამოწმებთ, რომ ერორი დამალულია (არ ჩანს)
        System.out.println("ტესტი დასრულებულია წარმატებით: შეცდომები არ დაფიქსირებულა.");
    }
}
//ტესტის უფრო დახვეწა შესაძლებელია, რადგან ბოლოში ისე სწრაფად იხურება, ვიზუალურად ვერაფერს ვერ ვიჭერ