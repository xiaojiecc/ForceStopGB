package android.app;

public interface INotificationManager {

    public int getPriority(String pkg, int uid) throws android.os.RemoteException;

    public static abstract class Stub {

        public static android.app.INotificationManager asInterface(android.os.IBinder obj) {
            throw new UnsupportedOperationException();
        }

    }

}
