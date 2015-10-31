/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Colby Skeggs.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package io.github.cosmicdrift.cosmicdrift.entities;

import java.util.HashMap;

public class EntRegistry {
    // TODO: refactor this with CompRegistry

    private static final HashMap<String, Class<? extends Entity>> nameToClass = new HashMap<>();
    private static final HashMap<Class<? extends Entity>, String> classToName = new HashMap<>();

    static {
        register(EntityItem.class);
        register(EntityPlayer.class);
    }

    public static void register(Class<? extends Entity> entityClass) {
        String name = entityClass.getSimpleName();
        if (name.startsWith("Entity")) {
            register(name.substring("Entity".length()), entityClass);
        } else {
            throw new IllegalArgumentException("Autoregistered classes must have names that start with 'Entity'!");
        }
    }

    public static void register(String name, Class<? extends Entity> entityClass) {
        if (nameToClass.containsKey(name) || classToName.containsKey(entityClass)) {
            throw new IllegalStateException("Entity already exists/already found: " + name);
        }
        nameToClass.put(name, entityClass);
        classToName.put(entityClass, name);
    }

    public static Class<? extends Entity> forName(String name) {
        Class<? extends Entity> found = nameToClass.get(name);
        if (found == null) {
            throw new IllegalArgumentException("No such entity: " + name);
        }
        return found;
    }

    public static String forClass(Class<? extends Entity> class_) {
        String found = classToName.get(class_);
        if (found == null) {
            throw new IllegalArgumentException("No entity for class: " + class_);
        }
        return found;
    }
}
