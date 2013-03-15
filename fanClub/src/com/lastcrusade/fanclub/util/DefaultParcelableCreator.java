package com.lastcrusade.fanclub.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import com.lastcrusade.fanclub.model.SongMetadata;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * A default implementation of Parcelable.Creator that uses reflection to
 * figure out how to create new objects and array.  This class can therefore
 * be used in any arbitrary Parcelable implementation, unless some custom
 * functionality is required.
 * 
 * To use (NOTE: this is required for implementations of Parcelable) add the following
 * to the top of your class:
 * 
 *     public static final Parcelable.Creator<SongMetadata> CREATOR =
 *       new DefaultParcelableCreator(MyClass.class);
 * Note that it must be public static final, and must be called CREATOR.  Replace MyClass
 * with the actual name of your class.
 * 
 * @author Jesse Rosalia
 *
 * @param <T> The type of object that will be created.  This is for typechecking the constructor arg
 * and implemented functions.
 */
public class DefaultParcelableCreator<T extends Parcelable> implements Parcelable.Creator<T> {

    private static final String TAG = DefaultParcelableCreator.class.getName();
    private Class<T> parcelableClass;

    public DefaultParcelableCreator(Class<T> parcelableClass) {
        this.parcelableClass = parcelableClass;
    }

    @Override
    public T createFromParcel(Parcel in) {
        T obj = null;
        try {
            if (in != null) {
                //create the object dynamically, using the constructor
                // that takes in a parcelable object
                Constructor<T> constructor = parcelableClass.getConstructor(Parcel.class);
                obj = (T) constructor.newInstance(in);
            }
        }
        catch (Exception e) {
            Log.wtf(TAG, e);
            //fall thru to return null
        }

        return obj;
    }

    @Override
    public T[] newArray(int size) {
        return (T[]) Array.newInstance(this.parcelableClass, size);
    }
}
