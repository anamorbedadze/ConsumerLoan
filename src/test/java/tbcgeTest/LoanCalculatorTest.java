package tbcgeTest;

import basetest.BaseTest;
import org.example.data.Constants;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import steps.LoanCalculatorSteps;

public class LoanCalculatorTest extends BaseTest {
    private LoanCalculatorSteps calcSteps;

    @BeforeMethod
    public void setupTest() {
        calcSteps = new LoanCalculatorSteps(page);
    }

    @Test
    public void testLoanCalculatorFlow() {
        // ნაბიჯი 1: საიტზე შესვლა და კალკულატორის ჩატვირთვის შემოწმება
        calcSteps.navigateToCalculator();

        // ნაბიჯი 2 & 3: საწყისი მონაცემების შეყვანა (3000 GEL, 48 თვე) და შედეგის შენახვა
        String initialPayment = calcSteps.calculateLoan(Constants.INITIAL_LOAN_AMOUNT, Constants.INITIAL_LOAN_PERIOD);

        // ნაბიჯი 4: მონაცემების შეცვლა და ახალი შედეგის მიღება
        calcSteps.calculateLoan(Constants.UPDATED_LOAN_AMOUNT, Constants.UPDATED_LOAN_PERIOD);

        // ნაბიჯი 5: ვალიდაცია, რომ მონაცემები შეიცვალა და შედეგი განახლდა
        calcSteps.verifyCalculatorUpdate(Constants.UPDATED_LOAN_AMOUNT, Constants.UPDATED_LOAN_PERIOD, initialPayment);

        System.out.println("ტესტი წარმატებით დასრულდა. კალკულატორი დინამიურად ახლდება.");
    }
}