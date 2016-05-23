/*
    Cosmic Drift is a computer game about building simulated space stations.
    Copyright (C) 2014-2015 Cel Skeggs and Christopher Quisling.

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Utils {

    public static <T> Iterable<T> joinIterables(final Iterable<T>... iterables) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                Iterator<T>[] iterators = new Iterator[iterables.length];
                for (int i = 0; i < iterators.length; i++) {
                    iterators[i] = iterables[i].iterator();
                }
                return joinIterators(iterators);
            }
        };
    }

    public static <T> Iterator<T> joinIterators(final Iterator<T>... iterators) {
        return new Iterator<T>() {
            private int next = 0;

            @Override
            public boolean hasNext() {
                while (next < iterators.length && !iterators[next].hasNext()) {
                    next++;
                }
                return next < iterators.length;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return iterators[next].next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not implemented for joined iterators.");
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getGenericClass(T t) {
        return (Class<T>) t.getClass();
    }

    public static <T> List<T> loadGsonList(Gson g, Reader r, final Class<T> element) {
        return g.fromJson(r, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{element};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        });
    }
}
