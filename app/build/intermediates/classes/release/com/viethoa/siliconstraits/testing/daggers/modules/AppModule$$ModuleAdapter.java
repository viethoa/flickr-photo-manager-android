// Code generated by dagger-compiler.  Do not edit.
package com.viethoa.siliconstraits.testing.daggers.modules;

import dagger.internal.BindingsGroup;
import dagger.internal.ModuleAdapter;
import dagger.internal.ProvidesBinding;
import javax.inject.Provider;

/**
 * A manager of modules and provides adapters allowing for proper linking and
 * instance provision of types served by {@code @Provides} methods.
 */
public final class AppModule$$ModuleAdapter extends ModuleAdapter<AppModule> {
  private static final String[] INJECTS = { "members/com.viethoa.siliconstraits.testing.MyApplication", };
  private static final Class<?>[] STATIC_INJECTIONS = { };
  private static final Class<?>[] INCLUDES = { };

  public AppModule$$ModuleAdapter() {
    super(com.viethoa.siliconstraits.testing.daggers.modules.AppModule.class, INJECTS, STATIC_INJECTIONS, false /*overrides*/, INCLUDES, false /*complete*/, true /*library*/);
  }

  /**
   * Used internally obtain dependency information, such as for cyclical
   * graph detection.
   */
  @Override
  public void getBindings(BindingsGroup bindings, AppModule module) {
    bindings.contributeProvidesBinding("android.content.Context", new ProvideApplicationContextProvidesAdapter(module));
    bindings.contributeProvidesBinding("com.lorem_ipsum.managers.UserSessionDataManager", new ProvidesUserManagerProvidesAdapter(module));
    bindings.contributeProvidesBinding("com.google.gson.Gson", new ProvidesJsonConverterProvidesAdapter(module));
  }

  /**
   * A {@code Binding<android.content.Context>} implementation which satisfies
   * Dagger's infrastructure requirements including:
   *
   * Being a {@code Provider<android.content.Context>} and handling creation and
   * preparation of object instances.
   */
  public static final class ProvideApplicationContextProvidesAdapter extends ProvidesBinding<android.content.Context>
      implements Provider<android.content.Context> {
    private final AppModule module;

    public ProvideApplicationContextProvidesAdapter(AppModule module) {
      super("android.content.Context", IS_SINGLETON, "com.viethoa.siliconstraits.testing.daggers.modules.AppModule", "provideApplicationContext");
      this.module = module;
      setLibrary(true);
    }

    /**
     * Returns the fully provisioned instance satisfying the contract for
     * {@code Provider<android.content.Context>}.
     */
    @Override
    public android.content.Context get() {
      return module.provideApplicationContext();
    }
  }

  /**
   * A {@code Binding<com.lorem_ipsum.managers.UserSessionDataManager>} implementation which satisfies
   * Dagger's infrastructure requirements including:
   *
   * Being a {@code Provider<com.lorem_ipsum.managers.UserSessionDataManager>} and handling creation and
   * preparation of object instances.
   */
  public static final class ProvidesUserManagerProvidesAdapter extends ProvidesBinding<com.lorem_ipsum.managers.UserSessionDataManager>
      implements Provider<com.lorem_ipsum.managers.UserSessionDataManager> {
    private final AppModule module;

    public ProvidesUserManagerProvidesAdapter(AppModule module) {
      super("com.lorem_ipsum.managers.UserSessionDataManager", IS_SINGLETON, "com.viethoa.siliconstraits.testing.daggers.modules.AppModule", "providesUserManager");
      this.module = module;
      setLibrary(true);
    }

    /**
     * Returns the fully provisioned instance satisfying the contract for
     * {@code Provider<com.lorem_ipsum.managers.UserSessionDataManager>}.
     */
    @Override
    public com.lorem_ipsum.managers.UserSessionDataManager get() {
      return module.providesUserManager();
    }
  }

  /**
   * A {@code Binding<com.google.gson.Gson>} implementation which satisfies
   * Dagger's infrastructure requirements including:
   *
   * Being a {@code Provider<com.google.gson.Gson>} and handling creation and
   * preparation of object instances.
   */
  public static final class ProvidesJsonConverterProvidesAdapter extends ProvidesBinding<com.google.gson.Gson>
      implements Provider<com.google.gson.Gson> {
    private final AppModule module;

    public ProvidesJsonConverterProvidesAdapter(AppModule module) {
      super("com.google.gson.Gson", IS_SINGLETON, "com.viethoa.siliconstraits.testing.daggers.modules.AppModule", "providesJsonConverter");
      this.module = module;
      setLibrary(true);
    }

    /**
     * Returns the fully provisioned instance satisfying the contract for
     * {@code Provider<com.google.gson.Gson>}.
     */
    @Override
    public com.google.gson.Gson get() {
      return module.providesJsonConverter();
    }
  }
}