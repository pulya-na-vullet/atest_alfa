package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.alfabank.contract.ContractRequestFactory;
import ru.alfabank.contract.clients.ContractClient;
import ru.alfabank.contract.dto.ContractRequest;

@Epic("epic des")
@Feature("feature des")
@Story("story des")
public class CreateContractTests extends BaseApiTest {

    private final ContractClient contractClient = new ContractClient();

    @Test
    @DisplayName("display des")
    @Description("description test des")
    public void createContract_programCode_ACCOUNT_and_status_201() {
        ContractRequest request = ContractRequestFactory.defaultRequest(1);
        request.setProgramCode("ACCOUNT");
        Response response = contractClient.createContract(token, request);
        System.out.println("status code: " + response.getStatusCode());
    }
}
