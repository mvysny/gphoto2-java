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

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.gphoto2.jna.GPhoto2Native;

import java.io.Closeable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Represents a list of configuration items (called widgets by gphoto).
 * @author Martin Vysny
 */
public final class CameraWidgets implements Closeable {

    public void close() {
        if (rootWidget != null) {
            CameraUtils.checkQuietly(GPhoto2Native.INSTANCE.gp_widget_free(rootWidget), "gp_widget_free");
            rootWidget = null;
        }
        widgets.clear();
    }

    private void checkNotClosed() {
        if (rootWidget == null) {
            throw new IllegalStateException("Invalid state: closed");
        }
        if (camera.isClosed()) {
            throw new IllegalStateException("Invalid state: camera is closed");
        }
    }

    public static enum WidgetTypeEnum {

        /**
         * Window widget This is the toplevel configuration widget. It should likely contain multiple GP_WIDGET_SECTION entries.
         */
        Window(GPhoto2Native.GP_WIDGET_WINDOW, false, false, false, null),
        /**
         * Section widget (think Tab).
         */
        Section(GPhoto2Native.GP_WIDGET_SECTION, false, false, false, null),
        /**
         * Text widget.
         */
        Text(GPhoto2Native.GP_WIDGET_TEXT, true, false, true, String.class),
        /**
         * Slider widget.
         */
        Range(GPhoto2Native.GP_WIDGET_RANGE, true, false, false, Float.class),
        /**
         * Toggle widget (think check box).
         */
        Toggle(GPhoto2Native.GP_WIDGET_TOGGLE, true, false, true, Boolean.class),
        /**
         * Radio button widget.
         */
        Radio(GPhoto2Native.GP_WIDGET_RADIO, true, true, true, String.class),
        /**
         * Menu widget (same as {@link #Radio}).
         */
        Menu(GPhoto2Native.GP_WIDGET_MENU, true, true, true, String.class),
        /**
         * Button press widget.
         */
        Button(GPhoto2Native.GP_WIDGET_BUTTON, true, false, true, Void.class),
        /**
         * Date entering widget.
         */
        Date(GPhoto2Native.GP_WIDGET_DATE, true, false, false, Date.class);
        public final int cval;
        public final boolean hasValue;
        public final boolean hasChoices;
        public final boolean acceptNullValue;
        public final Class<?> valueType;

        private WidgetTypeEnum(int cval, boolean hasValue, boolean hasChoices, boolean acceptNullValue, Class<?> valueType) {
            this.cval = cval;
            this.hasValue = hasValue;
            this.hasChoices = hasChoices;
            this.valueType = valueType;
            this.acceptNullValue = acceptNullValue;
        }

        public static WidgetTypeEnum fromCVal(int cval) {
            for (WidgetTypeEnum e : WidgetTypeEnum.values()) {
                if (e.cval == cval) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Parameter cval: invalid value " + cval + ": no such widget type");
        }

        public boolean acceptsValue(Object value) {
            if (valueType == null) {
                return false;
            }
            if (value == null) {
                return acceptNullValue;
            }
            return valueType.isInstance(value);
        }
    }
    private Map<String, Pointer> widgets = new HashMap<String, Pointer>();
    private final Camera camera;
    private Pointer rootWidget;

    /**
     * Lists all configuration options for given camera.
     */
    CameraWidgets(Camera c) {
        camera = c;
        final PointerByReference ptrRoot = new PointerByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_new(WidgetTypeEnum.Window.cval, "", ptrRoot), "gp_widget_new");
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_camera_get_config(c.camera, ptrRoot, CameraList.CONTEXT), "gp_camera_get_config");
        rootWidget = ptrRoot.getValue();
        try {
            enumWidgets(rootWidget, "");
        } catch (RuntimeException ex) {
            close();
            throw ex;
        }
    }

    private void enumWidgets(Pointer widget, String name) {
        checkNotClosed();
        final IntByReference type = new IntByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_type(widget, type), "gp_widget_get_type");
        final WidgetTypeEnum t = WidgetTypeEnum.fromCVal(type.getValue());
        if (t.hasValue) {
            widgets.put(name, widget);
        }
        final int childcount = CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_count_children(widget), "gp_widget_count_children");
        for (int i = 0; i < childcount; i++) {
            final PointerByReference ptrWidget = new PointerByReference();
            CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_child(widget, i, ptrWidget), "gp_widget_get_child");
            enumWidgets(ptrWidget.getValue(), name + "/" + getBasename(ptrWidget.getValue()));
        }
    }

    /**
     * Return names of all widgets available.
     * @return a list of widgets, never null, may be empty.
     */
    public List<String> getNames() {
        checkNotClosed();
        final List<String> result = new ArrayList<String>(widgets.keySet());
        Collections.sort(result);
        return result;
    }

    private String getBasename(Pointer widget) {
        checkNotClosed();
        final PointerByReference pref = new PointerByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_name(widget, pref), "gp_widget_get_name");
        final Pointer p = pref.getValue();
        return p.getString(0);
    }

    /**
     * Returns the label of the widget.
     * @param name the widget name
     * @return widget label.
     */
    public String getLabel(String name) {
        checkNotClosed();
        final PointerByReference pref = new PointerByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_label(get(name), pref), "gp_widget_get_label");
        final Pointer p = pref.getValue();
        return p.getString(0);
    }

    /**
     * Returns the info for the widget.
     * @param name the widget name
     * @return widget info.
     */
    public String getInfo(String name) {
        checkNotClosed();
        final PointerByReference pref = new PointerByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_info(get(name), pref), "gp_widget_get_info");
        final Pointer p = pref.getValue();
        return p.getString(0);
    }

    /**
     * Returns the data type of the widget.
     * @param name the widget name
     * @return widget type, never null.
     */
    public WidgetTypeEnum getType(String name) {
        checkNotClosed();
        final IntByReference type = new IntByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_type(get(name), type), "gp_widget_get_type");
        return WidgetTypeEnum.fromCVal(type.getValue());
    }

    /**
     * Returns the value of the configuration option.
     * @param name the widget name
     * @return the value.
     */
    public Object getValue(String name) {
        checkNotClosed();
        final WidgetTypeEnum type = getType(name);
        switch (type) {
            case Text:
            case Radio:
            case Menu: {
                final PointerByReference pref = new PointerByReference();
                CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_value(get(name), pref), "gp_widget_get_value");
                final Pointer p = pref.getValue();
                return p == null ? null : p.getString(0);
            }
            case Range: {
                final FloatByReference pref = new FloatByReference();
                CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_value(get(name), pref), "gp_widget_get_value");
                return pref.getValue();
            }
            case Toggle: {
                final IntByReference pref = new IntByReference();
                CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_value(get(name), pref), "gp_widget_get_value");
                return pref.getValue() == 2 ? null : pref.getValue() == 1;
            }
            case Date: {
                final IntByReference pref = new IntByReference();
                CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_value(get(name), pref), "gp_widget_get_value");
                return new Date(((long) pref.getValue()) * 1000L);
            }
            case Button:
                return null;
            default:
                throw new IllegalArgumentException("Parameter name: invalid value " + name + ": unsupported type: " + type);
        }
    }

    /**
     * Sets the value of given property. The value must be of correct class.
     * <p></p>
     * Important: after the changes are made, the {@link #apply()} method must be called, to apply the new values.
     * @param name the property name, not null
     * @param value the value, may be null.
     */
    public void setValue(String name, Object value) {
        checkNotClosed();
        if (isReadOnly(name)) {
            throw new IllegalArgumentException("Parameter name: invalid value " + name + ": read-only");
        }
        final WidgetTypeEnum type = getType(name);
        if (!type.acceptsValue(value)) {
            throw new IllegalArgumentException("Parameter value: invalid value " + value + ": expected " + type.valueType + " but got " + (value == null ? "null" : value.getClass()));
        }
        final Pointer ptr;
        switch (type) {
            case Text:
            case Radio:
            case Menu: {
                if (value == null) {
                    ptr = null;
                } else {
                    final byte[] b;
                    try {
                        b = ((String) value).getBytes("ASCII");
                    } catch (UnsupportedEncodingException ex) {
                        throw new RuntimeException(ex);
                    }
                    // patched as shown in https://code.google.com/p/gphoto2-java/issues/detail?id=5
                    final ByteBuffer buf = ByteBuffer.allocateDirect(b.length + 1);
                    buf.put(b);
                    ptr = Native.getDirectBufferPointer(buf);
                }
            }
            break;
            case Range:
                ptr = new FloatByReference((Float) value).getPointer();
                break;
            case Toggle: {
                final int val = value == null ? 2 : (Boolean) value ? 1 : 0;
                ptr = new IntByReference(val).getPointer();
            }
            break;
            case Date:
                ptr = new IntByReference((int) (((Date) value).getTime() / 1000)).getPointer();
                break;
            case Button:
                setChanged(name, true);
                return;
            default:
                throw new IllegalArgumentException("Parameter type: invalid value " + type + ": unsupported");
        }
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_set_value(get(name), ptr), "gp_widget_set_value");
    }

    private void checkType(String name, WidgetTypeEnum... types) {
        final WidgetTypeEnum type = getType(name);
        if (!Arrays.asList(types).contains(type)) {
            throw new IllegalArgumentException("Parameter name: invalid value " + name + ": expected " + Arrays.toString(types) + " but got " + type);
        }
    }

    /**
     * Returns allowed range for {@link WidgetTypeEnum#Range} options.
     * @param name the widget name.
     * @return the range. 
     */
    public Range getRange(String name) {
        checkType(name, WidgetTypeEnum.Range);
        return new Range(get(name));
    }

    public void setChanged(String name, boolean changed) {
        checkNotClosed();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_set_changed(get(name), changed ? 1 : 0), "gp_widget_set_changed");
    }

    public boolean isChanged(String name) {
        return CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_changed(get(name)), "gp_widget_changed") == 1;
    }

    private Pointer get(String name) {
        CameraUtils.requireNotNull(name, "name");
        checkNotClosed();
        final Pointer ptr = widgets.get(name);
        if (ptr == null) {
            throw new IllegalArgumentException("Parameter name: invalid value " + name + ": the name is not known");
        }
        return ptr;
    }

    /**
     * Lists choices for given widget. Only applicable to {@link WidgetTypeEnum#Radio} and {@link WidgetTypeEnum#Menu} types.
     * @param name widget name.
     * @return list of possible choices captions.
     */
    public List<String> listChoices(String name) {
        final WidgetTypeEnum type = getType(name);
        if (!type.hasChoices) {
            throw new IllegalArgumentException("Parameter name: invalid value " + name + ": is of type " + type + " which does not have any choices.");
        }
        final Pointer widget = get(name);
        final int choiceCount = CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_count_choices(widget), "gp_widget_count_choices");
        final List<String> result = new ArrayList<String>(choiceCount);
        for (int i = 0; i < choiceCount; i++) {
            final PointerByReference pref = new PointerByReference();
            CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_choice(widget, i, pref), "gp_widget_get_choice");
            final Pointer p = pref.getValue();
            result.add(p.getString(0));
        }
        return result;
    }

    public boolean isReadOnly(String name) {
        checkNotClosed();
        final IntByReference result = new IntByReference();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_readonly(get(name), result), "gp_widget_get_readonly");
        return result.getValue() == 1;
    }

    @Override
    public String toString() {
        return "Widgets: " + getNames();
    }

    /**
     * Returns a debug description of all options, their types, descriptions, allowed values etc.
     * @return a formatted string of all options.
     */
    public String inspect() {
        checkNotClosed();
        final StringBuilder sb = new StringBuilder();
        for (final String name : getNames()) {
            final WidgetTypeEnum type = getType(name);
            sb.append(name).append(": ").append(type).append(" = ");
            final Object value = getValue(name);
            sb.append(type.valueType.getName()).append(": ").append(value);
            sb.append('\n');
            sb.append("    ").append(getLabel(name));
            final String info = getInfo(name);
            if (info != null && !info.trim().isEmpty()) {
                sb.append(" - ").append(info);
            }
            if (type.hasChoices) {
                sb.append(": ").append(listChoices(name));
            }
            if (type == WidgetTypeEnum.Range) {
                sb.append(": ").append(getRange(name));
            }
            if (isReadOnly(name)) {
                sb.append(": READ_ONLY");
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * If the settings are altered, they need to be applied to take effect.
     */
    public void apply() {
        checkNotClosed();
        CameraUtils.check(GPhoto2Native.INSTANCE.gp_camera_set_config(camera.camera, rootWidget, CameraList.CONTEXT), "gp_camera_set_config");
    }

    public static void main(String[] args) {
        final Camera c = new Camera();
        c.initialize();
        try {
            final CameraWidgets w = c.newConfiguration();
            System.out.println(w.inspect());
            w.close();
        } finally {
            CameraUtils.closeQuietly(c);
        }
    }

    /**
     * Represents a {@link WidgetTypeEnum#Range}.
     */
    public static class Range {

        /**
         * The minimum accepted value.
         */
        public final float min;
        /**
         * The maximum accepted value.
         */
        public final float max;
        /**
         * The stepping.
         */
        public final float step;

        Range(Pointer widget) {
            final FloatByReference min = new FloatByReference();
            final FloatByReference max = new FloatByReference();
            final FloatByReference step = new FloatByReference();
            CameraUtils.check(GPhoto2Native.INSTANCE.gp_widget_get_range(widget, min, max, step), "gp_widget_get_range");
            this.min = min.getValue();
            this.max = max.getValue();
            this.step = step.getValue();
        }

        @Override
        public String toString() {
            return "Range{" + min + ".." + max + ", step=" + step + '}';
        }
    }
}
