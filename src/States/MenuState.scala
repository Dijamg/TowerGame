package States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Input
import com.badlogic.gdx.Gdx
import Game.Game
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle

/*This is the menu state of the program. This is the first state which will be shown upon
* start of the program.
*/

class MenuState(gsm: GameStateManager) extends State(gsm){
  private var exception: Option[Exception] = None
  // Textures which will be drawn to the screen.
  private var cloudLayer1: Option[Texture] = None
  private var cloudLayer2: Option[Texture] = None
  private var backgroundImage: Option[Texture] = None
  private var playBtn: Option[Texture] = None
  private var settingsBtn: Option[Texture] = None
  private var muteBtn: Option[Texture] = None
  private var unmuteBtn: Option[Texture] = None
  private var backBtn: Option[Texture] = None
  private var testBtn: Option[Texture] = None
  
  // Loading textures.
  try{
    cloudLayer1 = Some(new Texture("fastclouds.png"))
    cloudLayer2 = Some(new Texture("slowclouds.png"))
    backgroundImage = Some(new Texture("bg.png"))
    playBtn = Some(new Texture("playBtn.png"))
    settingsBtn = Some(new Texture("settingsBtn.png"))
    muteBtn = Some(new Texture("muteBtn.png"))
    unmuteBtn = Some(new Texture("unmuteBtn.png"))
    backBtn = Some(new Texture("backBtn.png"))
    testBtn = Some(new Texture("testBtn.png"))
  }catch{
    case e: Exception => this.exception = Some(e)
  }
  
  // Coordinates of the moving clouds in the background.
  private var cloudCoordinates1: Float = 0.0f
  private var cloudCoordinates2: Float = 0.0f
  
  private val clickCoordinates: Vector3 = new Vector3(0,0,0)
  cam.setToOrtho(false, Game.VIEWPORT_WIDTH, Game.VIEWPORT_HEIGHT)
  cam.position.x = 760
  
  /*The buttons are textures with a rectangle on them which acts as the hitbox.
   *If button textures failed to load, we assign a new rectangle(0,0,0,0) so exception wouldn't occur by calling .get to a non defined value.
   */
  private val playBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(cam.position.x - this.playBtn.get.getWidth/2, Game.GAME_WORLD_HEIGHT * 0.66f, this.playBtn.get.getWidth, this.playBtn.get.getHeight) else new Rectangle(0,0,0,0)
  private val settingsBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(this.playBtnHitbox.x, this.playBtnHitbox.y - this.settingsBtn.get.getHeight * 2, this.settingsBtn.get.getWidth, this.settingsBtn.get.getHeight) else new Rectangle(0,0,0,0)
  private val backBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(this.settingsBtnHitbox.x, this.settingsBtnHitbox.y, this.settingsBtnHitbox.width, this.settingsBtnHitbox.height) else new Rectangle(0,0,0,0)
  private val muteBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(this.backBtnHitbox.x + this.backBtn.get.getWidth/2 - this.muteBtn.get.getWidth/2, this.backBtnHitbox.y - this.muteBtn.get.getHeight*2, this.muteBtn.get.getWidth, this.muteBtn.get.getHeight) else new Rectangle(0,0,0,0)
  private val testBtnHitbox: Rectangle = if(!this.exception.isDefined) new Rectangle(this.playBtnHitbox.x, this.playBtnHitbox.y, this.playBtnHitbox.width, this.playBtnHitbox.height) else new Rectangle(0,0,0,0)
  // Different buttons are drawn depending on this value.
  private var settingsChosen = false
 
  
  
  
  
  // This method handles users input, such as clicks on buttons.
  protected def handleInput(): Unit = {
    //Next 2 lines convert click coordinates into game world coordinates.
    clickCoordinates.set(Gdx.input.getX, Gdx.input.getY, 0)
    cam.unproject(clickCoordinates)
    
    // Listens to user's clicks and checks if the click was on a button.
    if(Gdx.input.justTouched()){
      if(!this.settingsChosen){
        if(this.playBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
          gsm.set(new PlayState(gsm))                                                       // Moves to playstate. 
        }else if(this.settingsBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
          this.settingsChosen = true
        }
    }else{
        if(this.backBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
          settingsChosen = false
        }else if(this.muteBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
          if(Game.muted) Game.muted = false else Game.muted = true
        }else if(this.testBtnHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
          gsm.set(new TestState(gsm))                                                        // Moves to teststate.
        }
      } 
    }
  }
  
  
  
  // Main loop of this state. All it does is handle user's input and move clouds in the background.
  def uptade(dt: Float): Unit = {
    this.handleInput()
    cam.update()
    
    this.cloudCoordinates1 -= 0.75f
    this.cloudCoordinates2 -= 0.25f
    if(this.cloudCoordinates1 <= -Game.GAME_WORLD_WIDTH){
      this.cloudCoordinates1 = 0
    }else if(this.cloudCoordinates2 <= -Game.GAME_WORLD_WIDTH){
      this.cloudCoordinates2 = 0
    }
  }
  
  
  
  /* Renders all the textures onto the screen.
   * First we check if an exception was thrown while loading the image files.
   * If yes, program will redirrect to an exception state, else images will be rendered normally.
   */
  def render(sb:SpriteBatch,bf: BitmapFont): Unit = {
    if(this.exception.isDefined){
      gsm.set(new ExceptionState(gsm, this.exception.get))
    }else{
      sb.setProjectionMatrix(cam.combined);
      
      sb.begin()
      sb.draw(this.backgroundImage.get,0,0)
      sb.draw(this.cloudLayer1.get, this.cloudCoordinates1,0f)
      sb.draw(this.cloudLayer1.get, this.cloudCoordinates1 + Game.GAME_WORLD_WIDTH,0)
      sb.draw(this.cloudLayer2.get, this.cloudCoordinates2,0f)
      sb.draw(this.cloudLayer2.get, this.cloudCoordinates2 + Game.GAME_WORLD_WIDTH,0)
      if(!this.settingsChosen){
        sb.draw(this.playBtn.get, this.playBtnHitbox.x, this.playBtnHitbox.y)
        sb.draw(this.settingsBtn.get, this.settingsBtnHitbox.x, this.settingsBtnHitbox.y)
      }else{
        sb.draw(this.backBtn.get, this.backBtnHitbox.x, this.backBtnHitbox.y)
        sb.draw(this.testBtn.get, this.testBtnHitbox.x, this.testBtnHitbox.y)
        val mute = if(Game.muted) unmuteBtn else muteBtn
        sb.draw(mute.get, this.muteBtnHitbox.x, this.muteBtnHitbox.y)
      }
      sb.end()
    }
  }
  
  
  
  // Gets rid of textures after this state is shut.
   def dispose(): Unit = {
     if(!this.exception.isDefined){
       this.cloudLayer1.get.dispose()
       this.cloudLayer2.get.dispose()
       this.playBtn.get.dispose()
       this.settingsBtn.get.dispose()
       this.muteBtn.get.dispose()
       this.unmuteBtn.get.dispose()
       this.backBtn.get.dispose()
       this.backgroundImage.get.dispose()
     }
   }

}