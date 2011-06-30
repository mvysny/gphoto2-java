/**
 * Java bindings for the libgphoto2 library.
 * Copyright (C) 2011 Innovatrics s.r.o.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.gphoto2;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gphoto2.jna.GPhoto2Native;

/**
 * @author Martin Vysny
 */
public class CameraUtils {

    private CameraUtils() {
	throw new AssertionError();
    }

    public static void closeQuietly(Closeable c) {
	try {
	    c.close();
	} catch (Throwable t) {
	    log.log(Level.WARNING, "Failed to close Closeable " + c.getClass().getName(), t);
	}
    }
    private final static Logger log = Logger.getLogger(CameraUtils.class.getName());

    public static int check(int result, String methodName) {
	if (result < 0) {
	    throw new GPhotoException(methodName + " failed with #" + result + ": " + GPhoto2Native.INSTANCE.gp_result_as_string(result), result);
	}
	return result;
    }
    public static void checkQuietly(int result, String methodName) {
        try {
            check(result, methodName);
        } catch (Exception ex) {
            log.log(Level.WARNING, "Failed to invoke " + methodName + ": " + ex, ex);
        }
    }

    public static String toString(char[] array) {
	for (int i = 0; i < array.length; i++) {
	    if (array[i] == 0) {
		return new String(array, 0, i);
	    }
	}
	return new String(array);
    }

    public static String toString(byte[] array) {
        try {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == 0) {
                    return new String(array, 0, i, "ASCII");
                }
            }
            return new String(array, "ASCII");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
