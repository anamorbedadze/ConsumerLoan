package tbcgeTest;
import basetest.BaseTest;
import com.microsoft.playwright.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import steps.ConsumerLoanSteps;



public class ConsumerLoanTest extends BaseTest {
    private ConsumerLoanSteps loanSteps;

    @BeforeMethod
    public void setupTest() {
        loanSteps = new ConsumerLoanSteps(page);
    }

    @Test
    public void testConsumerLoanFlow() {
        // 1. ნავიგაცია და სესხის გვერდზე გადასვლა
        loanSteps.navigateToConsumerLoanPage();

        // 2. პირობების ნახვა და ღილაკის შემოწმება
        loanSteps.openAndVerifyLoanConditions();

        // 3. განაცხადის დაწყება და ახალი ფანჯრის მიღება
        Page tbcCreditPage = loanSteps.initiateLoanApplication();

        // 4. ახალ გვერდზე კონტენტის ვალიდაცია
        loanSteps.verifyTbcCreditPageContent(tbcCreditPage);
    }
}