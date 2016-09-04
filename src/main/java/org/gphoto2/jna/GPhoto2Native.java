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
package org.gphoto2.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;

/**
 * Native binding for GPhoto2.
 *
 * @author Martin Vysny
 */
public interface GPhoto2Native extends Library {

    /**
     * Corrupted data received
     *
     * Data is corrupt. This error is reported by camera drivers if corrupted
     * data has been received that can not be automatically handled. Normally,
     * drivers will do everything possible to automatically recover from this
     * error.
     *
     */
    int GP_ERROR_CORRUPTED_DATA = -102;
    /**
     * File already exists
     *
     * An operation failed because a file existed. This error is reported for
     * example when the user tries to create a file that already exists.
     *
     */
    int GP_ERROR_FILE_EXISTS = -103;
    /**
     * Specified camera model was not found
     *
     * The specified model could not be found. This error is reported when the
     * user specified a model that does not seem to be supported by any driver.
     *
     */
    int GP_ERROR_MODEL_NOT_FOUND = -105;
    /**
     * Specified directory was not found
     *
     * The specified directory could not be found. This error is reported when
     * the user specified a directory that is non-existent.
     *
     */
    int GP_ERROR_DIRECTORY_NOT_FOUND = -107;
    /**
     * Specified file was not found
     *
     * The specified file could not be found. This error is reported when the
     * user wants to access a file that is non-existent.
     *
     */
    int GP_ERROR_FILE_NOT_FOUND = -108;
    /**
     * Specified directory already exists
     *
     * The specified directory already exists. This error is reported for
     * example when the user wants to create a directory that already exists.
     *
     */
    int GP_ERROR_DIRECTORY_EXISTS = -109;
    /**
     * The camera is already busy
     *
     * Camera I/O or a command is in progress.
     *
     */
    int GP_ERROR_CAMERA_BUSY = -110;
    /**
     * Path is not absolute
     *
     * The specified path is not absolute. This error is reported when the user
     * specifies paths that are not absolute, i.e. paths like
     * "path/to/directory". As a rule of thumb, in gphoto2, there is nothing
     * like relative paths.
     *
     */
    int GP_ERROR_PATH_NOT_ABSOLUTE = -111;
    /**
     * Cancellation successful.
     *
     * A cancellation requestion by the frontend via progress callback and
     * GP_CONTEXT_FEEDBACK_CANCEL was successful and the transfer has been
     * aborted.
     */
    int GP_ERROR_CANCEL = -112;
    /**
     * Unspecified camera error
     *
     * The camera reported some kind of error. This can be either a photographic
     * error, such as failure to autofocus, underexposure, or violating storage
     * permission, anything else that stops the camera from performing the
     * operation.
     */
    int GP_ERROR_CAMERA_ERROR = -113;
    /**
     * Unspecified failure of the operating system
     *
     * There was some sort of OS error in communicating with the camera, e.g.
     * lack of permission for an operation.
     */
    int GP_ERROR_OS_FAILURE = -114;
    /**
     * \brief Everything is OK
     *
     * Note that this is also the value 0, and every error is negative (lower).
     */
    int GP_OK = 0;
    /**
     * \brief Generic Error
     */
    int GP_ERROR = -1;
    /**
     * \brief Bad parameters passed
     */
    int GP_ERROR_BAD_PARAMETERS = -2;
    /**
     * \brief Out of memory
     */
    int GP_ERROR_NO_MEMORY = -3;
    /**
     * \brief Error in the camera driver
     */
    int GP_ERROR_LIBRARY = -4;
    /**
     * \brief Unknown libgphoto2 port passed
     */
    int GP_ERROR_UNKNOWN_PORT = -5;
    /**
     * \brief Functionality not supported
     */
    int GP_ERROR_NOT_SUPPORTED = -6;
    /**
     * \brief Generic I/O error
     */
    int GP_ERROR_IO = -7;
    /**
     * \brief Buffer overflow of internal structure
     */
    int GP_ERROR_FIXED_LIMIT_EXCEEDED = -8;
    /**
     * \brief Operation timed out
     */
    int GP_ERROR_TIMEOUT = -10;
    /**
     * \brief Serial ports not supported
     */
    int GP_ERROR_IO_SUPPORTED_SERIAL = -20;
    /**
     * \brief USB ports not supported
     */
    int GP_ERROR_IO_SUPPORTED_USB = -21;
    /**
     * \brief Error initialising I/O
     */
    int GP_ERROR_IO_INIT = -31;
    /**
     * \brief I/O during read
     */
    int GP_ERROR_IO_READ = -34;
    /**
     * \brief I/O during write
     */
    int GP_ERROR_IO_WRITE = -35;
    /**
     * \brief I/O during update of settings
     */
    int GP_ERROR_IO_UPDATE = -37;
    /**
     * \brief Specified serial speed not possible.
     */
    int GP_ERROR_IO_SERIAL_SPEED = -41;
    /**
     * \brief Error during USB Clear HALT
     */
    int GP_ERROR_IO_USB_CLEAR_HALT = -51;
    /**
     * \brief Error when trying to find USB device
     */
    int GP_ERROR_IO_USB_FIND = -52;
    /**
     * \brief Error when trying to claim the USB device
     */
    int GP_ERROR_IO_USB_CLAIM = -53;
    /**
     * \brief Error when trying to lock the device
     */
    int GP_ERROR_IO_LOCK = -60;
    /**
     * \brief Unspecified error when talking to HAL
     */
    int GP_ERROR_HAL = -70;
    int GP_CAPTURE_IMAGE = 0;
    int GP_CAPTURE_MOVIE = 1;
    int GP_CAPTURE_SOUND = 2;
    int GP_FILE_TYPE_NORMAL = 1;
    int GP_VERSION_SHORT = 0;
    int GP_VERSION_VERBOSE = 1;
    GPhoto2Native INSTANCE = (GPhoto2Native) Native.loadLibrary("gphoto2", GPhoto2Native.class);

    int gp_camera_new(PointerByReference pcamera);

    int gp_camera_init(Pointer pcamera, Pointer gpcontext);

    int gp_camera_exit(Pointer pcamera, Pointer gpcontext);

    int gp_camera_free(Pointer pcamera);

    Pointer gp_context_new();

    String gp_result_as_string(int result);

    int gp_file_new(PointerByReference p);

    int gp_file_free(Pointer cf);

    int gp_camera_capture_preview(Pointer camera, Pointer cf, Pointer context);

    int gp_file_save(Pointer cf, String filename);

    int gp_camera_capture(Pointer camera, int GP_CAPTURE_IMAGE, CameraFilePath path, Pointer context);

    int gp_camera_file_get(Pointer cam, String path, String filename, int GP_FILE_TYPE_NORMAL, Pointer cf, Pointer context);

    int gp_camera_ref(Pointer camera);

    int gp_camera_unref(Pointer camera);

    int gp_camera_get_config(Pointer camera, PointerByReference widget, Pointer context);

    int gp_camera_set_config(Pointer camera, Pointer widget, Pointer context);

    int gp_file_ref(Pointer cf);

    int gp_file_unref(Pointer cf);

    String[] gp_library_version(int GP_VERSION_VERBOSE);

    int gp_list_new(PointerByReference ref);

    int gp_list_free(Pointer list);

    int gp_port_info_list_new(PointerByReference ref);

    int gp_port_info_list_load(Pointer list);

    int gp_port_info_list_count(Pointer list);

    int gp_abilities_list_new(PointerByReference ref);

    int gp_abilities_list_load(Pointer ptr, Pointer ctx);

    int gp_abilities_list_detect(Pointer cameraAbilitiesList, Pointer portInfoList, Pointer list, Pointer context);

    int gp_list_count(Pointer list);

    int gp_list_get_name(Pointer list, int i, PointerByReference pmodel);

    int gp_list_append(Pointer list, String model, String path);

    int gp_list_get_value(Pointer tempList, int i, PointerByReference pvalue);

    void gp_abilities_list_free(Pointer cameraAbilitiesList);

    void gp_port_info_list_free(Pointer portInfoList);

    int gp_port_info_list_get_info(Pointer portInfoList, int n, PointerByReference portInfo);

    int gp_camera_set_port_info(Pointer camera, Pointer portInfo);

    int gp_file_clean(Pointer cf);

    /**
     * A structure created by the capture operation.
     *
     * A structure containing the folder and filename of an object after a
     * successful capture and is passed as reference to the gp_camera_capture()
     * function.
     */
    class CameraFilePath extends Structure {
        {
            // must not call with JNA 3.5.0 or higher.
//            setFieldOrder(new String[] { "name", "folder" });
        }
        /**
         * Name of the captured file.
         */
        public byte[] name = new byte[128];
        /**
         * Name of the folder of the captured file.
         */
        public byte[] folder = new byte[1024];

        public List getFieldOrder() {
            // fixes compatibility with JNA 3.5.0 and higher.
            // see https://github.com/mvysny/gphoto2-java/issues/10 for details.
            return Arrays.asList("name", "folder");
        }

        public static class ByReference extends CameraFilePath implements Structure.ByReference {
        };
    }
    int GP_WIDGET_WINDOW = 0;//  # Window widget This is the toplevel configuration widget. It should likely contain multiple GP_WIDGET_SECTION entries.
    int GP_WIDGET_SECTION = 1;// # Section widget (think Tab).
    int GP_WIDGET_TEXT = 2;//    # Text widget.
    int GP_WIDGET_RANGE = 3;//   # Slider widget.
    int GP_WIDGET_TOGGLE = 4;//  # Toggle widget (think check box).
    int GP_WIDGET_RADIO = 5;//   # Radio button widget.
    int GP_WIDGET_MENU = 6;//    # Menu widget (same as RADIO).
    int GP_WIDGET_BUTTON = 7;//  # Button press widget.
    int GP_WIDGET_DATE = 8;//    # Date entering widget.

    int gp_widget_new(int type, String label, PointerByReference widget);

    int gp_widget_free(Pointer widget);

    int gp_widget_ref(Pointer widget);

    int gp_widget_unref(Pointer widget);

    int gp_widget_append(Pointer widget, Pointer child);

    int gp_widget_prepend(Pointer widget, Pointer child);

    int gp_widget_count_children(Pointer widget);

    int gp_widget_get_child(Pointer widget, int child_number, PointerByReference child);

    /* Retrieve Widgets */
    int gp_widget_get_child_by_label(Pointer widget, String label, PointerByReference child);

    int gp_widget_get_child_by_id(Pointer widget, int id, PointerByReference child);

    int gp_widget_get_child_by_name(Pointer widget, String name, PointerByReference child);

    int gp_widget_get_root(Pointer widget, PointerByReference root);

    int gp_widget_get_parent(Pointer widget, PointerByReference parent);

    int gp_widget_set_value(Pointer widget, Pointer value);

    int gp_widget_get_value(Pointer widget, ByReference value);

    int gp_widget_set_name(Pointer widget, String name);

    int gp_widget_get_name(Pointer widget, PointerByReference name);

    int gp_widget_set_info(Pointer widget, String info);

    int gp_widget_get_info(Pointer widget, PointerByReference /**
                     * char ** *
                     */
                    info);

    int gp_widget_get_id(Pointer widget, IntByReference id);

    int gp_widget_get_type(Pointer widget, IntByReference type);

    int gp_widget_get_label(Pointer widget, PointerByReference /**
                     * char ** *
                     */
                    label);

    int gp_widget_set_range(Pointer range, float low, float high, float increment);

    int gp_widget_get_range(Pointer range, FloatByReference min, FloatByReference max, FloatByReference increment);

    int gp_widget_add_choice(Pointer widget, String choice);

    int gp_widget_count_choices(Pointer widget);

    int gp_widget_get_choice(Pointer widget, int choice_number, PointerByReference /**
                     * char ** *
                     */
                    choice);

    int gp_widget_changed(Pointer widget);

    int gp_widget_set_changed(Pointer widget, int changed);

    int gp_widget_set_readonly(Pointer widget, int readonly);

    int gp_widget_get_readonly(Pointer widget, IntByReference readonly);
}
