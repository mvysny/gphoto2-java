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

/**
 * Thrown by the GPhoto Java bindings.
 * @author Martin Vysny
 */
public class GPhotoException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    public final int result;

    public GPhotoException(String message, Throwable cause, int result) {
	super(message, cause);
	this.result = result;
    }

    public GPhotoException(String message, int result) {
	super(message);
	this.result = result;
    }
}
