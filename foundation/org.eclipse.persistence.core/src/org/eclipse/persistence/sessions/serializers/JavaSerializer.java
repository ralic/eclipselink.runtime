/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Oracle - initial impl
 ******************************************************************************/
package org.eclipse.persistence.sessions.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.persistence.internal.helper.CustomObjectInputStream;
import org.eclipse.persistence.sessions.Session;

/**
 * Plain old Java serialization.
 * @author James Sutherland
 */
public class JavaSerializer extends AbstractSerializer {

    public static final JavaSerializer instance = new JavaSerializer();

    @Override
    public Object serialize(Object object, Session session) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(object);
            objectOut.flush();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return byteOut.toByteArray();
    }

    @Override
    public Class getType() {
        return byte[].class;
    }

    @Override
    public Object deserialize(Object bytes, Session session) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream((byte[])bytes);
        try (ObjectInputStream objectIn = session == null
                ? new ObjectInputStream(byteIn)
                : new CustomObjectInputStream(byteIn, session)) {
            return objectIn.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
