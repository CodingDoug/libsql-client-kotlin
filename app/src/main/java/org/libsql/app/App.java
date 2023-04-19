package org.libsql.app;

import org.libsql.client.Client;
import org.libsql.client.ResultSet;

import java.util.List;

public class App {
    public static void main(String args[]) {
        String env_url = System.getenv("LIBSQL_DB_URL");
        String env_token = System.getenv("LIBSQL_AUTH_TOKEN");
//        Client _client = newKotlinBuilder(env_url, env_token);
        Client _client = newJavaBuilder(env_url, env_token);

        try (Client client = _client) {
            List<ResultSet> rss = client.batchBlocking(new String[]{"select * from users"});
            System.out.println(rss);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static Client newKotlinBuilder(String url, String authToken) {
        Client.Builder builder = new Client.Builder();
        builder.setUrl(url);
        builder.setAuthToken(authToken);
        return builder.build();
    }

    private static Client newJavaBuilder(String url, String authToken) {
        return new Client.BuilderJ()
            .url(url)
            .authToken(authToken)
            .build();
    }
}
