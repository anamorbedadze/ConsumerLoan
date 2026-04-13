package tbcgeTest;

import basetest.BaseTest;
import com.microsoft.playwright.*;
import org.example.data.Constants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoanCalculatorPage;

import java.util.regex.Pattern;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoanCalculatorTest extends BaseTest {
    private LoanCalculatorPage loanPage;

    @BeforeMethod
    public void setupTest() {
        // 2. BaseTest-დან მომავალი page-ის გამოყენებით შექმენით Page ობიექტი
        // (დარწმუნდით, რომ BaseTest-ში 'page' ცვლადი ხელმისაწვდომია)
        loanPage = new LoanCalculatorPage(page);
    }

    @Test
    public void testLoanCalculatorFlow() {
        loanPage.navigate();
        loanPage.handleCookies();
        loanPage.waitForCalculator();

        // ნაბიჯი 2 & 3: საწყისი მონაცემები (Constants-დან)
        loanPage.fillLoanDetails(Constants.INITIAL_LOAN_AMOUNT, Constants.INITIAL_LOAN_PERIOD);

        // ნაბიჯი 4: პირველი შედეგის შენახვა
        String firstResult = loanPage.getMonthlyPayment();
        assertThat(loanPage.getMonthlyPaymentLocator()).not().isEmpty();

        // ნაბიჯი 5: მონაცემების შეცვლა
        loanPage.fillLoanDetails(Constants.UPDATED_LOAN_AMOUNT, Constants.UPDATED_LOAN_PERIOD);

        // ვალიდაცია Playwright Assertions-ით
        assertThat(loanPage.getAmountInput()).hasValue(Constants.UPDATED_LOAN_AMOUNT);
        assertThat(loanPage.getPeriodInput()).hasValue(Constants.UPDATED_LOAN_PERIOD);

        // ვამოწმებთ, რომ ტექსტი შეიცვალა და აღარ არის პირველი შედეგის ტოლი
        assertThat(loanPage.getMonthlyPaymentLocator()).not().hasText(firstResult);

        // დამატებითი შემოწმება ციფრებზე
        assertThat(loanPage.getMonthlyPaymentLocator()).containsText(Constants.CALC_DIGIT_PATTERN);
        System.out.println("ტესტი წარმატებით დასრულდა. შენატანი განახლდა: " + loanPage.getMonthlyPayment());
    }
}