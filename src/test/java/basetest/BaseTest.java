package basetest;
import com.microsoft.playwright.*;
import org.example.data.Constants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.List;


public class BaseTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext context;
    protected static Page page;
    protected APIRequestContext request; //ახალი ცვლადი API-სთვის
    // აქ მოგვიანებით დავამატებთ Steps კლასებს

    @BeforeClass
    public void setUp() {
        // 1. ვქმნით Playwright-ის ობიექტს
        playwright = Playwright.create();

        // 2. [UI ინიციალიზაცია] - ბრაუზერის და გვერდის გაშვება (გაერთიანებული)
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(Constants.SLOW_MO_DEFAULT)
                .setArgs(List.of("--start-maximized"))
        );

        // 3. [API ინიციალიზაცია] - API კონტექსტის შექმნა
        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
        );
    }

    @BeforeMethod
    public void createContextAndPage(Method method) {

        // თუ ტესტის სახელი არის openMobVersion, იძახებს მობაილის მეთოდს
        if (method.getName().equals("openMobVersion")) {
            setupMobile();
        }
        // სხვა ყველა დანარჩენი ტესტისთვის ხსნის ჩვეულებრივ ვების ზომას
        else {
            context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));
            page = context.newPage();
        }
    }

    // -------- მეთოდი სპეციალურად მობაილისთვის ----------
    public void setupMobile() {
        // ვაერთიანებთ მობაილის ზომებსა და ლოკაციას ერთ კონტექსტში
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(Constants.MOB_WIDTH, Constants.MOB_HEIGHT)
                .setIsMobile(true)
                .setUserAgent(Constants.IPHONE_USER_AGENT)
                .setPermissions(List.of("geolocation")) // ვაძლევთ ნებართვას
                .setGeolocation(41.7151, 44.8271));     // ვუთითებთ თბილისის კოორდინატებს

        page = context.newPage();
    }

    @AfterMethod
    public void closeContext() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterClass
    public void tearDown() {
        if (request != null) {
            request.dispose();

            // 1. 🟢 [API დასუფთავება] - მეხსიერების გათავისუფლება
            if (browser != null) {
                browser.close();
            }

            // 2. [UI დასუფთავება] - ბრაუზერის და გვერდის დახურვა
            if (page != null) {
                page.close();
            }
            if (browser != null) {
                browser.close();
            }
        }
    }
}
    // აქ მოხდება Steps-ების ინიციალიზაცია, მაგალითად: homeSteps = new HomePageSteps(page);
//ბეისტესტი მობაილისთვის თუ შემიძლია აქვე ჩავსვავ როცა ბოლო სთეფზე მივალ

