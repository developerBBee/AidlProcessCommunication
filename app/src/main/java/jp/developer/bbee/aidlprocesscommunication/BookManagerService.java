package jp.developer.bbee.aidlprocesscommunication;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";
    private List<Book> mBookList = new ArrayList<>();
    private static List<OnNewBookAddListener> mAddListenerList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        mBookList.add(new Book("Android Standard"));
        mBookList.add(new Book("Java Expert"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("jp.developer.bbee.aidlprocesscommunication.permission.LOCAL");
        if (check == PackageManager.PERMISSION_DENIED) {
            return null;
        }

        BookFactory factory = new BookFactory();
        factory.execute(5);
        return mBinder;
    }

    private final Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(OnNewBookAddListener listener) throws RemoteException {
            if (!mAddListenerList.contains(listener)) {
                mAddListenerList.add(listener);
            }
            Log.i(TAG, "registerListener: " + mAddListenerList.size());
        }

        @Override
        public void unregisterListener(OnNewBookAddListener listener) throws RemoteException {
            mAddListenerList.remove(listener);
        }
    };

    private static class BookFactory extends AsyncTask<Integer, Book, Book> {

        @Override
        protected Book doInBackground(Integer... integers) {
            int i = 0;
            Book mBook = null;
            while (i < integers[0]) {
                mBook = new Book("New arrival " + (i+1));
                try {
                    Thread.sleep(2000);
                    publishProgress(mBook);
                    i++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return mBook;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Book... values) {
            super.onProgressUpdate(values);

            for (int i = 0; i < mAddListenerList.size(); i++) {
                OnNewBookAddListener listener = mAddListenerList.get(i);
                try {
                    if (listener != null) {
                        listener.onNewBookAdd(values[0]);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Book book) {
            super.onPostExecute(book);

            for (int i = 0; i < mAddListenerList.size(); i++) {
                OnNewBookAddListener listener = mAddListenerList.get(i);
                try {
                    if (listener != null) {
                        listener.onAllBook();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
