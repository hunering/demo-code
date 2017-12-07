package com.xmo.k8s.api;

import java.io.IOException;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.auth.ApiKeyAuth;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

public class ApiClientApp {
    public static void main(String[] args) throws IOException, ApiException {
        ApiClient client = Config.defaultClient();
        client.setVerifyingSsl(false);
        client.setDebugging(true);
        client.setBasePath("https://192.168.1.134:6443");
        ApiKeyAuth BearerToken = (ApiKeyAuth) client.getAuthentication("BearerToken");
        BearerToken.setApiKey(
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJrdWJlLXN5c3RlbSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJoZWxtLXRva2VuLXF4NThsIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6ImhlbG0iLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI5NzdmZjEzOS1jZThmLTExZTctOTA5OC0wMDI1OTA2YjQ0OTYiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6a3ViZS1zeXN0ZW06aGVsbSJ9.BijdBKBNB0EsK26W-SYdWvgPfEj2pkUzA_DcnRKdiMEqHTK5nb0Qv18AWdmX0YvLw8Q6KbX6hAeI2opn9MGG-v1jkZThPKI0-xzMnl9k106LTCzDXqg9UtzrdOijS78V_8ojtU_PfWNOJ6RrpoLSuuHMIRZxqnVUh6kxLLa2my1yUP-Bm_gY0NMejp0PywdUXVpmh5Go4sUiB-RMhgbK9FeBZ02DskIze04DIGiePVV_jKX765j-bmPn7LVIcMt52YB4uvGEK7AtWJwYwHgBrbEQ4aQL0ygXTSl7jhcZl074MOQY_R2eevIdDuFrI8nSvu4gNsfkT1XZe41_2ADi6w");
        BearerToken.setApiKeyPrefix("Bearer");
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();

        // V1PodList list = api.listPodForAllNamespaces(null, null, null, null,
        // null, null, null, null, null);
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }
}
