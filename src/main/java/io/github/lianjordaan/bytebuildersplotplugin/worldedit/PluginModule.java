package io.github.lianjordaan.bytebuildersplotplugin.worldedit;

import com.google.inject.AbstractModule;

public class PluginModule extends AbstractModule {
    @Override
    protected void configure() {
        // Bind your classes here
        bind(WorldEditLimitListener.class).asEagerSingleton();
        // You can bind other classes as needed
    }
}
