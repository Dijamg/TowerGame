package States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Input
import com.badlogic.gdx.Gdx
import Game.Game
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle

/* State which appears when game is won or lost.
 * Shows different text depending on outcome of the game and contains 2 buttons which redirrect user to new game or the main menu.
 */
class GameoverState(gsm: GameStateManager, gameWon: Boolean) extends State(gsm) {
  private var exception: Option[Exception] = None
  private val text: String = if(gameWon) "YOU WIN!" else "YOU LOST!"
  private var playBtn: Option[Texture] = None
  private var menuBtn: Option[Texture] = None 
  
  // Load button images,
  try{
    playBtn = Some(new Texture("playBtn.png"))
    menuBtn = Some(new Texture("menuBtn.png"))
  }catch{
    case e: Exception => this.exception = Some(e)
  }

  /*The buttons are textures with a rectangle on them which acts as the hitbox.
   *If button textures failed to load, we assign a new rectangle(0,0,0,0) so exception wouldn't occur by calling .get to a non defined value.
   */
   private val playBtnHitbox: Rectangle = if(!exception.isDefined) new Rectangle(Game.VIEWPORT_WIDTH/2 - playBtn.get.getWidth/2, Game.GAME_WORLD_HEIGHT * 0.5f, playBtn.get.getWidth, playBtn.get.getHeight) else new Rectangle(0,0,0,0)
   private val menuBtnHitbox: Rectangle = if(!exception.isDefined) new Rectangle(playBtnHitbox.x, playBtnHitbox.y - menuBtn.get.getHeight * 2, menuBtn.get.getWidth, menuBtn.get.getHeight) else new Rectangle(0,0,0,0)
  
   private val clickCoordinates: Vector3 = new Vector3(0,0,0)
   cam.setToOrtho(false, Game.VIEWPORT_WIDTH, Game.VIEWPORT_HEIGHT)
   cam.position.x = Game.VIEWPORT_WIDTH/2
  
   private var sound: Option[com.badlogic.gdx.audio.Sound] = None
   try{
     sound = Some(if(gameWon) Gdx.audio.newSound(Gdx.files.internal("win.mp3")) else { Gdx.audio.newSound(Gdx.files.internal("gameover.mp3")) })
   }catch{
     case e: Exception => // If exception is thrown, there will be no sound.
   }
  
   if(!Game.muted && sound.isDefined) this.sound.get.play(1f)
  
  
  
  

  // Checks for button clicks.
  protected def handleInput()={
    clickCoordinates.set(Gdx.input.getX, Gdx.input.getY, 0)
    cam.unproject(clickCoordinates)
    
    if(Gdx.input.justTouched()){
      if(playBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
        gsm.set(new PlayState(gsm))                                                // Moves to playstate.
      }else if(menuBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
        gsm.set(new MenuState(gsm))                                                // Moves to menustate.
      }
    }
  }
  
   
  
  // This is the mainloop of the state and it is called every frame. It only handles users input.
  def uptade(dt: Float): Unit ={
    cam.update()
    this.handleInput()
  }
  
  
  
  // Draws textures and text to the screen.
  def render(sb: SpriteBatch, bf: BitmapFont): Unit ={
    if(this.exception.isDefined){
      gsm.set(new ExceptionState(gsm,this.exception.get))
    }else{
      sb.setProjectionMatrix(cam.combined);
  
      bf.setColor(1, 24/255f, 0, 1)
      sb.begin()
      bf.getData.setScale(5)
      sb.draw(this.playBtn.get, this.playBtnHitbox.x, this.playBtnHitbox.y)
      sb.draw(this.menuBtn.get, this.menuBtnHitbox.x, this.menuBtnHitbox.y)
      bf.draw(sb,this.text, Game.VIEWPORT_WIDTH * 0.285f, Game.VIEWPORT_HEIGHT *0.875f)
      bf.getData.setScale(1)
      sb.end()
    }
  }
  
  
   
  // Disposes of the objects which will no longer be used.
  def dispose(): Unit ={
    if(!this.exception.isDefined){
      this.playBtn.get.dispose()
      this.menuBtn.get.dispose()
      if(this.sound.isDefined) this.sound.get.dispose()
    } 
  }
}