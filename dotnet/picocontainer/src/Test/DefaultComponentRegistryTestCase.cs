using System;
using NUnit.Framework;
using PicoContainer;

using PicoContainer.Defaults;
using PicoContainer.Tests.Tck;

using PicoContainer.Tests.TestModel;

namespace Test {
  /// <summary>
  /// Summary description for DefaultComponentRegistryTestCase.
  /// </summary>
  [TestFixture]
  public class DefaultComponentRegistryTestCase {
    private DefaultPicoContainer picoContainer;

    [SetUp]
    protected void setUp() {
      picoContainer = new DefaultPicoContainer();
    }
    public void testRegisterComponent() {
      IComponentAdapter componentSpecification = CreateComponentAdapter();

      picoContainer.RegisterComponent(componentSpecification);

      Assert.IsTrue(picoContainer.ComponentAdapters.Contains(componentSpecification));
    }

    public void testUnregisterComponent() {
      IComponentAdapter componentSpecification = CreateComponentAdapter();

      picoContainer.RegisterComponent(componentSpecification);

      picoContainer. UnregisterComponent(typeof(Touchable));

      Assert.IsFalse(picoContainer.ComponentAdapters.Contains(componentSpecification));
    }

    private IComponentAdapter CreateComponentAdapter() {
      return new ConstructorInjectionComponentAdapter(typeof(Touchable), typeof(SimpleTouchable));
    }

  }
}
