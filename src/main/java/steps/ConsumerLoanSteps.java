package steps;

import com.microsoft.playwright.Page;
import org.example.data.Constants;import pages.ConsumerLoanPage;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.regex.Pattern;

public class ConsumerLoanSteps {
    private final Page page;
    private final ConsumerLoanPage loanPage;

    public ConsumerLoanSteps(Page page) {
        this.page = page;
        this.loanPage = new ConsumerLoanPage(page);
    }

    // ნაბიჯი 1: მთავარი გვერდიდან სამომხმარებლო სესხის გვერდზე გადასვლა
    public void navigateToConsumerLoanPage() {
        page.navigate(Constants.BASE_URL);
        loanPage.acceptCookiesIfVisible();
        loanPage.hoverPersonalMenu();
        loanPage.getConsumerLoanLink().click();
        assertThat(page).hasURL(Constants.CONS_LOAN_URL);
    }

    // ნაბიჯი 2: პირობების გვერდის გახსნა და ვალიდაცია
    public void openAndVerifyLoanConditions() {
        loanPage.openConditions();
        // ვამოწმებთ, რომ გვერდი ჩაიტვირთა (ტექსტის ან ღილაკის მიხედვით)
        assertThat(page.locator("body")).containsText(Pattern.compile(Constants.CONSUMER_LOAN_APPLY + "|" + Constants.CONSUMER_INTEREST_RATE));
        assertThat(loanPage.getApplyBtn()).isVisible();
        assertThat(loanPage.getApplyBtn()).isEnabled();
    }

    // ნაბიჯი 3: სესხზე განაცხადის დაწყება (Redirect-ის შემოწმებით)
    public Page initiateLoanApplication() {
        // Playwright-ში popup-ის დაჭერა
        Page popup = page.waitForPopup(loanPage::startApplyProcess);

        // URL-ის ვალიდაცია
        assertThat(popup).hasURL(Pattern.compile(".*tbccredit.ge.*"),
                new com.microsoft.playwright.assertions.PageAssertions.HasURLOptions().setTimeout(Constants.LOAD_TIMEOUT));

        return popup; // ვაბრუნებთ ახალ ფანჯარას შემდეგი მოქმედებებისთვის
    }

    // ნაბიჯი 4: TBC Credit-ის გვერდის გასუფთავება (ქუქიები, პოპაპები) და ფინალური შემოწმება
    public void verifyTbcCreditPageContent(Page popup) {
        loanPage.acceptTbcCreditCookies(popup);
        loanPage.closeOfferPopupIfVisible(popup);
        assertThat(loanPage.getHeroTitle(popup)).isVisible();
    }
}