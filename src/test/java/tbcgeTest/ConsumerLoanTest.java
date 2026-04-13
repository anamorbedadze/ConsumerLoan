package tbcgeTest;

import basetest.BaseTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PageAssertions;
import org.example.data.Constants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ConsumerLoanPage;
import pages.LoanCalculatorPage;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


    public class ConsumerLoanTest extends BaseTest {
        private ConsumerLoanPage loanPage;

        @BeforeMethod
        public void setupTest() {
            loanPage = new ConsumerLoanPage(page);
        }

        @Test
        public void testConsumerLoanFlow() {
            ConsumerLoanPage loanPage = new ConsumerLoanPage(page);

            // 1. ნავიგაცია
            page.navigate(Constants.BASE_URL);

            // 2. მოქმედებები
            loanPage.acceptCookiesIfVisible();
            loanPage.hoverPersonalMenu();
            loanPage.getConsumerLoanLink().click();
            assertThat(page).hasURL(Constants.CONS_LOAN_URL);

            // სცენარი ნაბიჯი 2: გახსენი Terms გვერდი
            loanPage.openConditions();

            // სცენარი ნაბიჯი 3: შეამოწმე რომ კონტენტი ჩაიტვირთა და Apply ხილვადია
            assertThat(page.locator("body")).containsText(Pattern.compile(Constants.CONSUMER_LOAN_APPLY + "|" + Constants.CONSUMER_INTEREST_RATE ));
            assertThat(loanPage.getApplyBtn()).isVisible();
            assertThat(loanPage.getApplyBtn()).isEnabled();

            // სცენარი ნაბიჯი 4 და 5: ახალი ფანჯრის დაჭერა და კლიკი
            Page popup = page.waitForPopup(loanPage::startApplyProcess);
            assertThat(popup).hasURL(Pattern.compile(".*tbccredit.ge.*"),
                    new PageAssertions.HasURLOptions().setTimeout(Constants.LOAD_TIMEOUT));
            loanPage.acceptTbcCreditCookies(popup);
            // სცენარი ნაბიჯი 5 და 6: Assertions ახალ ფანჯარაზე
            loanPage.closeOfferPopupIfVisible(popup);
            assertThat(loanPage.getHeroTitle(popup)).isVisible();
            //----ვიზუალურად არ მიჩვენებს რომ კრედიტის ფეიჯზე ქუქის დადასტურება მოხდა, კოდის დონეზე მიდასტურებს----
        }
    }