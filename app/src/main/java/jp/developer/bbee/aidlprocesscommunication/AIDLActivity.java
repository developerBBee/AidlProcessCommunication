package jp.developer.bbee.aidlprocesscommunication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AIDLActivity extends AppCompatActivity {
    private final String TAG = "AIDLActivity";
    private Intent mIntent;
    private IBookManager mIBookManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        mIntent = new Intent(AIDLActivity.this, BookManagerService.class);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bindService:
                bindService(mIntent, mConnection, BIND_AUTO_CREATE);
                break;
            case R.id.unbindService:
                unbindService(mConnection);
                break;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mIBookManager = IBookManager.Stub.asInterface(service);
                mIBookManager.addBook(new Book("Android Connected"));
                List<Book> bookList = mIBookManager.getBookList();
                mIBookManager.registerListener(sListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        try {
            mIBookManager.unregisterListener(sListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private OnNewBookAddListener sListener = new OnNewBookAddListener.Stub() {
        @Override
        public void onNewBookAdd(Book book) throws RemoteException {
            mIBookManager.addBook(book);
            Log.i(TAG, "onNewBookAdd: " + book.toString());
        }

        @Override
        public void onAllBook() throws RemoteException {
            List<Book> bookList = mIBookManager.getBookList();
            Log.i(TAG, "onAllBook: " + bookList.toString());
        }
    };
}
