package suites;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import game.GameTest;

@Suite
@SelectClasses({

		GameTest.class

})

public class GameSuites {

}
