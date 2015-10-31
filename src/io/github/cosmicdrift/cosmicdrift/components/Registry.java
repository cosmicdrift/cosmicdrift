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
package io.github.cosmicdrift.cosmicdrift.components;

import java.util.HashMap;

public class Registry {
    private static final HashMap<String, Class<? extends Component>> nameToClass = new HashMap<>();
    private static final HashMap<Class<? extends Component>, String> classToName = new HashMap<>();

    static {
        register(ComponentActivationSynchronizer.class);
        register(ComponentAirExchanger.class);
        register(ComponentDispenser.class);
        register(ComponentEnergyCost.class);
        register(ComponentGravityGenerator.class);
        register(ComponentLuaComputer.class);
        register(ComponentMarkerWall.class);
        register(ComponentMedkit.class);
        register(ComponentNetworkData.class);
        register(ComponentNetworkDataEndpoint.class);
        register(ComponentNetworkFluid.class);
        register(ComponentNetworkPower.class);
        register(ComponentPowerConsumer.class);
        register(ComponentPowerGenerator.class);
        register(ComponentRendererDirectioned.class);
        register(ComponentRendererDual.class);
        register(ComponentRendererSimple.class);
        register(ComponentToggleButton.class);
    }

    public static void register(Class<? extends Component> componentClass) {
        String name = componentClass.getSimpleName();
        if (name.startsWith("Component")) {
            register(name.substring("Component".length()), componentClass);
        } else {
            throw new IllegalArgumentException("Autoregistered classes must have names that start with 'Component'!");
        }
    }

    public static void register(String name, Class<? extends Component> componentClass) {
        if (nameToClass.containsKey(name) || classToName.containsKey(componentClass)) {
            throw new IllegalStateException("Component already exists/already found: " + name);
        }
        nameToClass.put(name, componentClass);
        classToName.put(componentClass, name);
    }

    public static Class<? extends Component> forName(String name) {
        Class<? extends Component> found = nameToClass.get(name);
        if (found == null) {
            throw new IllegalArgumentException("No such component: " + name);
        }
        return found;
    }

    public static String forClass(Class<? extends Component> class_) {
        String found = classToName.get(class_);
        if (found == null) {
            throw new IllegalArgumentException("No component for class: " + class_);
        }
        return found;
    }
}
