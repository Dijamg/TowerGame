package States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont

/*Class which allows us to handle the game states. With an instance of this class,
* we will be able to add and remove states, paint the state and update the state.
* Only one instance of this class exists in the program and it will be passed to
* other states when it is needed.*/

class GameStateManager{
  
  //The gamestates are stored in a list which works like a stack. 
  private var states = List[State]();
  
  // Pushes new State to the top. (Pausing the game)
  def push(state: State): Unit = {
    states = List[State](state) ++ states
  }
  
  // Discards the top stack. (Leaving pausestate)
  def pop(state: State): Unit = {
    states = states.tail
  }
  
  // Sets a new state on top and discards the current top (Menustate to playstate)
  def set(state: State): Unit = {
    val toBePopped = states.head
    this.pop(state)
    toBePopped.dispose()
    this.push(state)
  }
  
  // Gets rid of all the current states.
  def clear: Unit = {
    states.foreach(_.dispose())
    states = List[State](states.head)
  }
  
  def update(dt: Float): Unit = {
    states.head.uptade(dt)
  }
  
  def render(sb: SpriteBatch, bf: BitmapFont): Unit = {
    states.head.render(sb, bf)
  }
}