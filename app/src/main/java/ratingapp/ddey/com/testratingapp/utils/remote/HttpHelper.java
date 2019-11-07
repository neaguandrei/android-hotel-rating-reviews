package ratingapp.ddey.com.testratingapp.utils.remote;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpHelper extends AsyncTask<String, Void, String> {
    private URL url;
    private HttpURLConnection connection;
    private InputStream is;
    private InputStreamReader isr;
    private BufferedReader br;

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder builder = new StringBuilder();

        try {
            url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            is = connection.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            String line;

            while ((line = br.readLine()) != null) {
                builder.append(line);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return builder.toString();
    }
}
