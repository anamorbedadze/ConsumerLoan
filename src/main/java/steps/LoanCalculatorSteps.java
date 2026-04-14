package steps;
import com.microsoft.playwright.Page;
import org.example.data.Constants;
import pages.LoanCalculatorPage;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoanCalculatorSteps {
    private final Page page;
    private final LoanCalculatorPage calcPage;

    public LoanCalculatorSteps(Page page) {
        this.page = page;
        this.calcPage = new LoanCalculatorPage(page);
    }

    // ნაბიჯი 1: გვერდის გახსნა და კალკულატორამდე მისვლა
    public void navigateToCalculator() {
        calcPage.navigate();
        calcPage.handleCookies();
        calcPage.waitForCalculator();
        assertThat(calcPage.getCalculatorSection()).isVisible(); // ვამოწმებთ რომ კალკულატორი ჩაიტვირთა
    }

    // ნაბიჯი 2: კალკულატორში მონაცემების შეყვანა და შედეგის მიღება
    public String calculateLoan(String amount, String period) {
        calcPage.fillLoanDetails(amount, period);
        calcPage.stabilityWait(); // თუ საჭიროა სტაბილურობისთვის

        // ვამოწმებთ, რომ შედეგი გამოჩნდა (ცარიელი არ არის)
        assertThat(calcPage.getMonthlyPaymentLocator()).not().isEmpty();

        return calcPage.getMonthlyPayment(); // ვაბრუნებთ შედეგს, რომ ტესტში შევადაროთ
    }

    // ნაბიჯი 3: შეცვლილი მონაცემების ვალიდაცია
    public void verifyCalculatorUpdate(String expectedAmount, String expectedPeriod, String previousResult) {
        // ვამოწმებთ, რომ ველებში ნამდვილად ახალი მნიშვნელობები წერია
        assertThat(calcPage.getAmountInput()).hasValue(expectedAmount);
        assertThat(calcPage.getPeriodInput()).hasValue(expectedPeriod);

        // ვამოწმებთ, რომ ახალი შედეგი არ უდრის ძველ შედეგს (ანუ დააფდეითდა)
        assertThat(calcPage.getMonthlyPaymentLocator()).not().hasText(previousResult);

        // ვამოწმებთ, რომ შედეგი შეიცავს ციფრებს (და არა მაგალითად "Loading...")
        assertThat(calcPage.getMonthlyPaymentLocator()).containsText(Constants.CALC_DIGIT_PATTERN);
    }
}