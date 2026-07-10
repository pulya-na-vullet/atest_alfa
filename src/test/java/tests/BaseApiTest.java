package tests;

import org.junit.jupiter.api.BeforeAll;
import ru.alfabank.auth.clients.AuthClient;

public abstract class BaseApiTest {

    protected static String token;

    @BeforeAll
    static void setUp() {
        token = new AuthClient().getToken();
    }
}
