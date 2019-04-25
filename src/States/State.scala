package States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.OrthographicCamera

/* Abstract class which defines all the methods which all of the game states must have.
 * Every state must be able to handle users input, update the state (loop), paint the screen
 * and get rid of objects which are no longer being used.*/

abstract class State(val gsm: GameStateManager){
  
  // With an ortographic camera we can manuever in the game world and only see a certain part of the world.
  val cam = new OrthographicCamera()
  
  // With this method we can listen to users input and respond with something.
  protected def handleInput(): Unit
  
  // This is the mainloop of the state and it is called every frame.
  def uptade(dt: Float): Unit
  
  // Paints the screen ( paintComponent(g))
  def render(sb: SpriteBatch, bf: BitmapFont): Unit
  
  // Disposes of the objects which will no longer be used.
  def dispose(): Unit
}