package suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import engine.AxisTest;
import engine.EntityTest;
import engine.GridTest;
import engine.ISUTest;
import engine.PictureTest;
import game.GameTest;

@Suite
@SelectClasses({

		AxisTest.class, GameTest.class, GridTest.class, ISUTest.class, EntityTest.class, PictureTest.class

})

public class AllSuites {
}
