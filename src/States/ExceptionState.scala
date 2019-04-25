package States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.math.Vector3
import Game.Game

class ExceptionState(gsm: GameStateManager, e: Exception) extends State(gsm) {
  private val text = "An exception has occured. \n Occured exception:\n " + e
  private val clickCoordinates: Vector3 = new Vector3(0,0,0)
  cam.setToOrtho(false, Game.VIEWPORT_WIDTH, Game.VIEWPORT_HEIGHT)
  cam.position.x = Game.VIEWPORT_WIDTH/2
  
  protected def handleInput(): Unit ={
    // Does nothing.
  }
  
  // This is the mainloop of the state and it is called every frame.
  def uptade(dt: Float): Unit = {
    // Does nothing
  }
  
  // Paints the screen ( paintComponent(g))
  def render(sb: SpriteBatch, bf: BitmapFont): Unit = {
    sb.setProjectionMatrix(cam.combined);
    sb.begin()
    bf.draw(sb, this.text, Game.VIEWPORT_WIDTH * 0.15f, Game.VIEWPORT_HEIGHT * 0.75f)
    sb.end()
  }
  
  // Disposes of the objects which will no longer be used.
  def dispose(): Unit ={
    //nothing
  }
}