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

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import java.io.Closeable;
import java.util.regex.Pattern;
import org.gphoto2.jna.GPhoto2Native;

/**
 * Lists connected cameras.
 * @author Martin Vysny
 */
public class CameraList implements Closeable {

    public static final Pointer CONTEXT;

    static {
	CONTEXT = GPhoto2Native.INSTANCE.gp_context_new();
	if (CONTEXT == null) {
	    throw new RuntimeException("Failed to get context");
	}
    }
    private final Pointer list;

    /**
     * Enumerates connected cameras.
     */
    public CameraList() {
	list = newList();
	populateList();
    }

    private static Pointer newList() {
	final PointerByReference ref = new PointerByReference();
	CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_new(ref), "gp_list_new");
	return ref.getValue();
    }
    private static final Pattern USB_MATCH = Pattern.compile("usb:\\d+,\\d+");

    private void populateList() {
	final Pointer tempList = newList();
	try {
	    final PointerByReference ref = new PointerByReference();
	    CameraUtils.check(GPhoto2Native.INSTANCE.gp_abilities_list_new(ref), "gp_abilities_list_new");
	    final Pointer cameraAbilitiesList = ref.getValue();
	    try {
		final PointerByReference ref2 = new PointerByReference();
		CameraUtils.check(GPhoto2Native.INSTANCE.gp_port_info_list_new(ref2), "gp_port_info_list_new");
		final Pointer portInfoList = ref2.getValue();
		try {
		    CameraUtils.check(GPhoto2Native.INSTANCE.gp_port_info_list_load(portInfoList), "gp_port_info_list_load");
		    CameraUtils.check(GPhoto2Native.INSTANCE.gp_abilities_list_load(cameraAbilitiesList, CONTEXT), "gp_abilities_list_load");
		    CameraUtils.check(GPhoto2Native.INSTANCE.gp_abilities_list_detect(cameraAbilitiesList, portInfoList, tempList, CONTEXT), "gp_abilities_list_detect");
		    final int count = CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_count(tempList), "gp_list_count");
		    for (int i = 0; i < count; i++) {
			final PointerByReference pmodel = new PointerByReference();
			CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_get_name(tempList, i, pmodel), "gp_list_get_name");
			final String model = pmodel.getValue().getString(0);
			final PointerByReference pvalue = new PointerByReference();
			CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_get_value(tempList, i, pvalue), "gp_list_get_value");
			final String path = pvalue.getValue().getString(0);
			if (USB_MATCH.matcher(path).matches()) {
			    CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_append(list, model, path), "gp_list_append");
			}
		    }
		} finally {
		    GPhoto2Native.INSTANCE.gp_port_info_list_free(portInfoList);
		}
	    } finally {
		GPhoto2Native.INSTANCE.gp_abilities_list_free(cameraAbilitiesList);
	    }
	} finally {
	    GPhoto2Native.INSTANCE.gp_list_free(tempList);
	}
    }

    /**
     * Returns a displayable name of the camera.
     * @param i the camera index, must be 0 .. {@link #getCount()} - 1.
     * @return the displayable camera name, never null, for example Canon EOS 1000D
     */
    public String getModel(int i) {
	final PointerByReference pmodel = new PointerByReference();
	CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_get_name(list, i, pmodel), "gp_list_get_name");
	return pmodel.getValue().getString(0);
    }

    /**
     * Returns a displayable name of the port to which the camera is connected.
     * @param i the camera index, must be 0 .. {@link #getCount()} - 1.
     * @return the displayable camera name, never null, for example usb:002,019
     */
    public String getPort(int i) {
	final PointerByReference pvalue = new PointerByReference();
	CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_get_value(list, i, pvalue), "gp_list_get_value");
	return pvalue.getValue().getString(0);
    }

    /**
     * Returns connected camera count.
     * @return connected camera count.
     */
    public int getCount() {
	return CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_count(list), "gp_list_count");
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	sb.append("CameraList[");
	for (int i = 0; i < getCount(); i++) {
	    sb.append(getModel(i)).append(':').append(getPort(i)).append(", ");
	}
	sb.append("]");
	return sb.toString();
    }

    public void close() {
	CameraUtils.check(GPhoto2Native.INSTANCE.gp_list_free(list), "gp_list_free");
    }
    
    public Pointer getPortInfo(int index) {
        final PointerByReference result = new PointerByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_port_info_list_get_info(list, index, result), "gp_port_info_list_get_info");
        return result.getValue();
    }
}
