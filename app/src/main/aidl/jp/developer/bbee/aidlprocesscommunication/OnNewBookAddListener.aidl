// OnNewBookAddListener.aidl
package jp.developer.bbee.aidlprocesscommunication;

// Declare any non-default types here with import statements
import jp.developer.bbee.aidlprocesscommunication.Book;

interface OnNewBookAddListener {

    void onNewBookAdd(in Book book);

    void onAllBook();
}