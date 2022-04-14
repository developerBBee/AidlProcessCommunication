// IBookManager.aidl
package jp.developer.bbee.aidlprocesscommunication;

// Declare any non-default types here with import statements
import jp.developer.bbee.aidlprocesscommunication.Book;
import jp.developer.bbee.aidlprocesscommunication.OnNewBookAddListener;

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);

    void registerListener(OnNewBookAddListener listener);

    void unregisterListener(OnNewBookAddListener listener);
}