package suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import engine.AxisTest;
import engine.GridTest;
import engine.ISUTest;
import engine.EntityTest;
import engine.PictureTest;

@Suite
@SelectClasses({

		AxisTest.class, GridTest.class, ISUTest.class, EntityTest.class, PictureTest.class

})

public class EngineSuites {

}