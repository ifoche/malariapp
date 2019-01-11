package org.eyeseetea.malariacare.data.remote.api;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.PullApiParsingException;
import org.json.JSONObject;

import java.io.IOException;

public class OkHttpClientDataSource {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String TAG = ".PullDhisApiDataSource";

    /**
     * Call to DHIS Server
     * @param data
     * @param method
     * @param url
     */
    public static Response executeCall(BasicAuthenticator basicAuthenticator, JSONObject data, String url, String method) throws IOException {

        Log.d(TAG, "executeCall Url" + url + "");
        OkHttpClient client= UnsafeOkHttpsClientFactory.getUnsafeOkHttpClient(basicAuthenticator);

        Request.Builder builder = new Request.Builder()
                .header(basicAuthenticator.AUTHORIZATION_HEADER, basicAuthenticator.getCredentials())
                .url(url);

        switch (method){
            case "POST":
                RequestBody postBody = RequestBody.create(JSON, data.toString());
                builder.post(postBody);
                break;
            case "PUT":
                RequestBody putBody = RequestBody.create(JSON, data.toString());
                builder.put(putBody);
                break;
            case "PATCH":
                RequestBody patchBody = RequestBody.create(JSON, data.toString());
                builder.patch(patchBody);
                break;
            case "GET":
                builder.get();
                break;
        }

        Request request = builder.build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            Log.e(TAG, "pushData (" + response.code() + "): " + response.body().string());
            throw new IOException(response.message());
        }
        return response;
    }

    /**
     * Call to DHIS Server
     * @param url
     * @param method
     */
    public static Response executeCall(String url, String method) throws IOException {
        final String DHIS_URL=PreferencesState.getInstance().getServer().getUrl() + url.replace(" ", "%20");
        return executeCall(new BasicAuthenticator(PreferencesState.getInstance().getCreedentials()), null, DHIS_URL + url, method);
    }

    public static Response executeCall(BasicAuthenticator basicAuthenticator, String url, String method) throws IOException {
        return executeCall(basicAuthenticator, null, url, method);
    }

    public static JsonNode parseResponse(String responseData)throws Exception{
        try{
            JSONObject jsonResponse=new JSONObject(responseData);
            Log.i("JsonCommonParser", "parseResponse: " + jsonResponse);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = jsonResponse.toString();
            try {
                return objectMapper.readValue(jsonString, JsonNode.class);
            }catch(Exception ex){
                throw new PullApiParsingException();
            }
        }catch(Exception ex){
            throw new PullApiParsingException();
        }
    }
}
