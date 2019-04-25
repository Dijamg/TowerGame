package Game;

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import States.GameStateManager
import States.MenuState

// This is the main class of the program. 

class Game extends ApplicationAdapter {
  private var sb: Option[SpriteBatch] = None      // A SpriteBatch is a graphic component like Graphics2D in JavaFx
  private var bf: Option[BitmapFont] = None       // Used for writing text on the screen.
  private var gsm: Option[GameStateManager] = None
  
  // Initializes used objects. Called when program starts. Compare to init in JavaFx etc.
  override def create() {
    sb = Some(new SpriteBatch)
    bf = Some(new BitmapFont)
    gsm = Some(new GameStateManager)
    Gdx.gl.glClearColor(124/255f,124/255f,121/255f,1)
    gsm.get.push(new MenuState(gsm.get))
  }
  
  // Paint the screen. Compare to paintComponent(g) in swing etc.
  override def render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    gsm.get.update(Gdx.graphics.getDeltaTime())
    gsm.get.render(sb.get,bf.get)
  }
}

/*
 * This companion object contains the necessary constants which are needed in other classes.
 * Viewport size is the size of the world the user sees.
 */
object Game {
  val GAME_WORLD_HEIGHT = 480
  val GAME_WORLD_WIDTH = 2000
  val VIEWPORT_WIDTH = 800
  val VIEWPORT_HEIGHT = 480
  val TITLE = "Game"

  var muted = false
}