package com.avvero.carx.web

import com.avvero.carx.App
import com.avvero.carx.conf.LocationRepositoryTestConfiguration
import com.avvero.carx.dao.jpa.CustomerRepository
import com.avvero.carx.dao.mongo.CustomerDataRepository
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spark.utils.IOUtils
import spock.lang.Specification

import static com.avvero.carx.utils.ApplicationUtils.dataToJson
import static org.junit.Assert.fail

/**
 * @author Avvero
 */
@ContextConfiguration(classes = [App, LocationRepositoryTestConfiguration], loader = SpringApplicationContextLoader)
@ActiveProfiles("test")
class WebConfigTests extends Specification {

    @Autowired
    CustomerDataRepository customerDataRepository;
    @Autowired
    CustomerRepository customerRepository;

    def cleanup() {
        customerRepository.deleteAll()
        customerDataRepository.deleteAll()
    }


    def "first"() {
        when:
            def response = request("POST", "/customer/1/data", [money: 100, country: "RUS"])
        then:
            response.status == 200
    }

    private TestResponse request(String method, String path, Object body) {
        try {
            URL url = new URL("http://localhost:4567" + path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            if (body != null) {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream());
                wr.write(dataToJson(body));
                wr.close();
            }
            connection.connect();
            String responseBody = IOUtils.toString(connection.getInputStream());
            return new TestResponse(connection.getResponseCode(), responseBody);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Sending request failed: " + e.getMessage());
            return null;
        }
    }

    private static class TestResponse {

        public final String body;
        public final int status;

        public TestResponse(int status, String body) {
            this.status = status;
            this.body = body;
        }

        public Map<String,String> json() {
            return new Gson().fromJson(body, HashMap.class);
        }
    }

}
