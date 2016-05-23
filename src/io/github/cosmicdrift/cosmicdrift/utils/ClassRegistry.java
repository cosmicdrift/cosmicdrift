/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs.

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
package io.github.cosmicdrift.cosmicdrift.utils;

import java.util.HashMap;
import java.util.Objects;

public class ClassRegistry<T> {

    private final String baseName;
    private final Class<T> baseClass;

    public ClassRegistry(String baseName, Class<T> baseClass) {
        Objects.requireNonNull(baseName);
        Objects.requireNonNull(baseClass);
        this.baseName = baseName;
        this.baseClass = baseClass;
    }

    public ClassRegistry(Class<T> baseClass) {
        this.baseName = baseClass.getSimpleName();
        this.baseClass = baseClass;
    }

    private final HashMap<String, Class<? extends T>> nameToClass = new HashMap<>();
    private final HashMap<Class<? extends T>, String> classToName = new HashMap<>();

    public void register(Class<? extends T> class_) {
        String name = class_.getSimpleName();
        if (name.startsWith(this.baseName)) {
            register(name.substring(baseName.length()), class_);
        } else {
            throw new IllegalArgumentException("Autoregistered classes must have names that start with '" + baseName + "'!");
        }
    }

    public void register(String name, Class<? extends T> class_) {
        if (nameToClass.containsKey(name) || classToName.containsKey(class_)) {
            throw new IllegalStateException(baseName + " already exists/already found: " + name);
        }
        nameToClass.put(name, class_);
        classToName.put(class_, name);
    }

    public Class<? extends T> forName(String name) {
        Class<? extends T> found = nameToClass.get(name);
        if (found == null) {
            throw new IllegalArgumentException("No such " + baseName + ": " + name);
        }
        return found;
    }

    public String forClass(Class<? extends T> class_) {
        String found = classToName.get(class_);
        if (found == null) {
            throw new IllegalArgumentException("No " + baseName + " for class: " + class_);
        }
        return found;
    }

    public Class<T> getBaseClass() {
        return baseClass;
    }
}
