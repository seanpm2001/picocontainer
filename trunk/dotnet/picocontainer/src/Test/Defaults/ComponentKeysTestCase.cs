using NUnit.Framework;
using PicoContainer;
using PicoContainer.Defaults;
using PicoContainer.Tests.TestModel;

namespace Test.Defaults
{
	/// <summary>
	/// Summary description for ComponentKeysTestCase.
	/// </summary>
	[TestFixture]
	public class ComponentKeysTestCase
	{
		[Test]
		public void testComponensRegisteredWithClassKeyTakePrecedenceOverOthersWhenThereAreMultipleImplementations()
		{
			DefaultPicoContainer pico = new DefaultPicoContainer();
			pico.RegisterComponentImplementation("default", typeof (SimpleTouchable));

			/*
       * By using a class as key, this should take precedence over the other Touchable (Simmpe)
       */
			pico.RegisterComponentImplementation(typeof (ITouchable), typeof (DecoratedTouchable), new IParameter[]
				{
					new ComponentParameter("default")
				});

			ITouchable touchable = (ITouchable) pico.GetComponentInstanceOfType(typeof (ITouchable));
			Assert.AreEqual(typeof (DecoratedTouchable), touchable.GetType());
		}

		[Test]
		public void testIComponentAdapterResolutionIsFirstLookedForByClassKeyToTheTopOfTheContainerHierarchy()
		{
			DefaultPicoContainer pico = new DefaultPicoContainer();
			pico.RegisterComponentImplementation("default", typeof (SimpleTouchable));

			pico.RegisterComponentImplementation(typeof (ITouchable), typeof (DecoratedTouchable), new IParameter[]
				{
					new ComponentParameter("default")
				});

			DefaultPicoContainer grandChild = new DefaultPicoContainer(new DefaultPicoContainer(new DefaultPicoContainer(pico)));

			ITouchable touchable = (ITouchable) grandChild.GetComponentInstanceOfType(typeof (ITouchable));
			Assert.AreEqual(typeof (DecoratedTouchable), touchable.GetType());

		}
	}
}