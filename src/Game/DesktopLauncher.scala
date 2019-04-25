package Game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

/*
 * Main object of the project. First the width,height and title is set and then the program starts.
 */
object DesktopLauncher extends App {
  	val config = new LwjglApplicationConfiguration();
  	config.width = Game.VIEWPORT_WIDTH
  	config.height = Game.GAME_WORLD_HEIGHT
  	config.title = Game.TITLE
		new LwjglApplication(new Game(), config);

}
