package States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Input
import com.badlogic.gdx.Gdx
import Game.Game
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.audio._

// Pausestate of the program. Takes music as construction parameter so this state can pause or unpause the music.
class PauseState(gsm: GameStateManager, music: Option[Music]) extends State(gsm) {
  private var exception: Option[Exception] = None
  
  private var muteBtn: Option[Texture] = None
  private var unmuteBtn: Option[Texture] = None
  private var backBtn: Option[Texture] = None
  private var menuBtn: Option[Texture] = None
  private var bg: Option[Texture] = None
  
  // Load image files.
  try{
    muteBtn = Some(new Texture("muteBtn.png"))
    unmuteBtn = Some(new Texture("unmuteBtn.png"))
    backBtn = Some(new Texture("backBtn.png"))
    menuBtn = Some(new Texture("menuBtn.png"))
    bg = Some(new Texture("paused.png"))
  }catch{
    case e: Exception => this.exception = Some(e)
  }
  
  /*The buttons are textures with a rectangle on them which acts as the hitbox.
   *If button textures failed to load, we assign a new rectangle(0,0,0,0) so exception wouldn't occur by calling .get to a non defined value.
   */
  private val backBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(Game.VIEWPORT_WIDTH/2 - this.backBtn.get.getWidth/2, Game.VIEWPORT_HEIGHT * 0.60f, this.backBtn.get.getWidth, this.backBtn.get.getHeight) else new Rectangle(0,0,0,0)
  private val menuBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(this.backBtnHitbox.x, this.backBtnHitbox.y - this.menuBtn.get.getHeight * 2, this.menuBtn.get.getWidth, this.menuBtn.get.getHeight) else new Rectangle(0,0,0,0)
  private val muteBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(this.menuBtnHitbox.x + this.menuBtn.get.getWidth/2 - this.muteBtn.get.getWidth/2, this.menuBtnHitbox.y - this.muteBtn.get.getHeight*2, this.muteBtn.get.getWidth, this.muteBtn.get.getHeight) else new Rectangle(0,0,0,0)
  
  private val clickCoordinates: Vector3 = new Vector3(0,0,0)
  cam.setToOrtho(false, Game.VIEWPORT_WIDTH, Game.VIEWPORT_HEIGHT)
  cam.position.x = Game.VIEWPORT_WIDTH/2
  
  
  
  
  
  // Checks if buttons have been clicked.
  protected def handleInput(): Unit = {
    clickCoordinates.set(Gdx.input.getX, Gdx.input.getY, 0)
    cam.unproject(clickCoordinates)
    
    if(Gdx.input.justTouched()){
      if(this.backBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
        gsm.pop(this)                                                                // Leaves pausestate.
      }else if(this.menuBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
        gsm.clear
        gsm.push(new MenuState(gsm))                                                 // Goes to menustate.
      }else if(this.muteBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
        if(Game.muted){
          Game.muted = false
          if(this.music.isDefined) this.music.get.play()
          }else{
          Game.muted = true
          if(this.music.isDefined) this.music.get.pause()
        }
        
      }
    }
  }
  
  
  
  // Main loop. Only checks for users input.
  def uptade(dt: Float): Unit = {
    cam.update()
    this.handleInput() 
  }
  
  
  
  /* Renders all the texture onto the screen.
   * First we check if an exception was thrown while loading the image files.
   * If yes, program will redirrect to an exception state, else images will be rendered normally.
   */
  def render(sb: SpriteBatch, bf: BitmapFont): Unit = {
    if(this.exception.isDefined){
      gsm.set(new ExceptionState(gsm, this.exception.get))
    }else{
      sb.setProjectionMatrix(cam.combined);
      
      sb.begin()
      sb.draw(this.bg.get, 0, 0)
      sb.draw(this.backBtn.get, this.backBtnHitbox.x, this.backBtnHitbox.y)
      sb.draw(this.menuBtn.get, this.menuBtnHitbox.x, this.menuBtnHitbox.y)
      val mute = if(Game.muted) this.unmuteBtn else this.muteBtn
      sb.draw(mute.get, this.muteBtnHitbox.x, this.muteBtnHitbox.y)
      sb.end()
    }
  }
  
  
  
  // Gets rid of textures after the state is shut.
  def dispose(): Unit = {
    if(!this.exception.isDefined){
      this.backBtn.get.dispose()
      this.menuBtn.get.dispose()
      this.bg.get.dispose()
      this.muteBtn.get.dispose()
      this.unmuteBtn.get.dispose()
    }
  }
}