package ratingapp.ddey.com.testratingapp.utils.others.async;

public interface AsyncTaskListener<T> {
    void onPreExecute();
    void onPostExecute(T result);
}
