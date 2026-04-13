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
    // აქ მოგვიანებით დავამატებთ Steps კლასებს

    @BeforeClass
    public static void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(Constants.SLOW_MO_DEFAULT)
                .setArgs(List.of("--start-maximized")));

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
    public static void tearDown() {
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }
    // აქ მოხდება Steps-ების ინიციალიზაცია, მაგალითად: homeSteps = new HomePageSteps(page);
//ბეისტესტი მობაილისთვის თუ შემიძლია აქვე ჩავსვავ როცა ბოლო სთეფზე მივალ

