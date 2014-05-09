/**
 * Java bindings for the libgphoto2 library. Copyright (C) 2011 Innovatrics
 * s.r.o.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.gphoto2;

import org.gphoto2.jna.GPhoto2Native;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        if (result < GPhoto2Native.GP_OK) {
            String constantName = ERROR_CONSTANTS.get(result);
            if (constantName == null) {
                constantName = "unknown error";
            }
            throw new GPhotoException(methodName + " failed with " + constantName + " #" + result + ": " + GPhoto2Native.INSTANCE.gp_result_as_string(result), result);
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
    private static final Map<Integer, String> ERROR_CONSTANTS = new HashMap<Integer, String>();

    static {
        final Map<Integer, String> m = ERROR_CONSTANTS;
        m.put(GPhoto2Native.GP_ERROR_CORRUPTED_DATA, "GP_ERROR_CORRUPTED_DATA");
        m.put(GPhoto2Native.GP_ERROR_FILE_EXISTS, "GP_ERROR_FILE_EXISTS");
        m.put(GPhoto2Native.GP_ERROR_MODEL_NOT_FOUND, "GP_ERROR_MODEL_NOT_FOUND");
        m.put(GPhoto2Native.GP_ERROR_DIRECTORY_NOT_FOUND, "GP_ERROR_DIRECTORY_NOT_FOUND");
        m.put(GPhoto2Native.GP_ERROR_FILE_NOT_FOUND, "GP_ERROR_FILE_NOT_FOUND");
        m.put(GPhoto2Native.GP_ERROR_DIRECTORY_EXISTS, "GP_ERROR_DIRECTORY_EXISTS");
        m.put(GPhoto2Native.GP_ERROR_CAMERA_BUSY, "GP_ERROR_CAMERA_BUSY");
        m.put(GPhoto2Native.GP_ERROR_PATH_NOT_ABSOLUTE, "GP_ERROR_PATH_NOT_ABSOLUTE");
        m.put(GPhoto2Native.GP_ERROR_CANCEL, "GP_ERROR_CANCEL");
        m.put(GPhoto2Native.GP_ERROR_CAMERA_ERROR, "GP_ERROR_CAMERA_ERROR");
        m.put(GPhoto2Native.GP_ERROR_OS_FAILURE, "GP_ERROR_OS_FAILURE");
        m.put(GPhoto2Native.GP_OK, "GP_OK");
        m.put(GPhoto2Native.GP_ERROR, "GP_ERROR");
        m.put(GPhoto2Native.GP_ERROR_BAD_PARAMETERS, "GP_ERROR_BAD_PARAMETERS");
        m.put(GPhoto2Native.GP_ERROR_NO_MEMORY, "GP_ERROR_NO_MEMORY");
        m.put(GPhoto2Native.GP_ERROR_LIBRARY, "GP_ERROR_LIBRARY");
        m.put(GPhoto2Native.GP_ERROR_UNKNOWN_PORT, "GP_ERROR_UNKNOWN_PORT");
        m.put(GPhoto2Native.GP_ERROR_NOT_SUPPORTED, "GP_ERROR_NOT_SUPPORTED");
        m.put(GPhoto2Native.GP_ERROR_IO, "GP_ERROR_IO");
        m.put(GPhoto2Native.GP_ERROR_FIXED_LIMIT_EXCEEDED, "GP_ERROR_FIXED_LIMIT_EXCEEDED");
        m.put(GPhoto2Native.GP_ERROR_TIMEOUT, "GP_ERROR_TIMEOUT");
        m.put(GPhoto2Native.GP_ERROR_IO_SUPPORTED_SERIAL, "GP_ERROR_IO_SUPPORTED_SERIAL");
        m.put(GPhoto2Native.GP_ERROR_IO_SUPPORTED_USB, "GP_ERROR_IO_SUPPORTED_USB");
        m.put(GPhoto2Native.GP_ERROR_IO_INIT, "GP_ERROR_IO_INIT");
        m.put(GPhoto2Native.GP_ERROR_IO_READ, "GP_ERROR_IO_READ");
        m.put(GPhoto2Native.GP_ERROR_IO_WRITE, "GP_ERROR_IO_WRITE");
        m.put(GPhoto2Native.GP_ERROR_IO_UPDATE, "GP_ERROR_IO_UPDATE");
        m.put(GPhoto2Native.GP_ERROR_IO_SERIAL_SPEED, "GP_ERROR_IO_SERIAL_SPEED");
        m.put(GPhoto2Native.GP_ERROR_IO_USB_CLEAR_HALT, "GP_ERROR_IO_USB_CLEAR_HALT");
        m.put(GPhoto2Native.GP_ERROR_IO_USB_FIND, "GP_ERROR_IO_USB_FIND");
        m.put(GPhoto2Native.GP_ERROR_IO_USB_CLAIM, "GP_ERROR_IO_USB_CLAIM");
        m.put(GPhoto2Native.GP_ERROR_IO_LOCK, "GP_ERROR_IO_LOCK");
        m.put(GPhoto2Native.GP_ERROR_HAL, "GP_ERROR_HAL");
    }

    public static <T> T requireNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
    public static <T> T requireNotNull(T obj, String name) {
        if (obj == null) {
            throw new NullPointerException(name);
        }
        return obj;
    }
}
