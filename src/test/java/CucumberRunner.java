import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(
    plugin = {"pretty", "html:target/cucumber/bagbasics"},
    features = {"classpath:acceptance"},
    extraGlue = "com.david.poc.springboot.crud")
@RunWith(Cucumber.class)
public class CucumberRunner {

}
