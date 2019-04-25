package States

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import Game.Game
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g2d.TextureRegion
import scala.collection.mutable.Buffer
import Units._
import Objects._
import Misc._
import scala.util.Random
import com.badlogic.gdx.audio._

/*This state was used for testing. You can upgrade turrets, bring characters and cast enemy and ally fire support without limitation.
 * Doesn't differ much from other state classes so explanations of the methods are in the playstate class.
 * Code quality haven't been paid attention for here.
 */
class TestState(gsm: GameStateManager) extends State(gsm) {
  private var exception: Option[Exception] = None

  private var backgroundImage: Option[Texture] = None
  private var cloudLayer1: Option[Texture] = None
  private var cloudLayer2: Option[Texture] = None
  private var panels: Option[Texture] = None
  private var healthbarRed: Option[Texture] = None
  private var healthbarGreen: Option[Texture] = None
  private var cooldown: Option[Texture] = None
  
  private var castles = Array[Tower]()
  private var allButtons: Array[Button] = Array[Button]()
  
  private val allCharacters = Buffer[Character]()
  private val allProjectiles = Buffer[Projectile]()
  private val allyTurrets = Array.ofDim[Turret](2)
  
  try{
    backgroundImage = Some(new Texture("bg.png"))
    cloudLayer1 = Some(new Texture("fastclouds.png"))
    cloudLayer2 = Some(new Texture("slowclouds.png"))
    panels = Some(new Texture("panels.png"))
    healthbarRed = Some(new Texture("blankRed.png"))
    healthbarGreen = Some(new Texture("blankGreen.png"))
    cooldown = Some(new Texture("cooldown.png"))
    castles = Array[Tower](new Tower(false, allCharacters, allProjectiles), new Tower(true, allCharacters, allProjectiles))
    allButtons = Array(new swordmanButton(0,0,45,45,allCharacters,allProjectiles),new MarksmanButton(0,0, 45,45, allCharacters, allProjectiles),new KnightButton(0,0, 45,45,allCharacters, allProjectiles),new CannonButton(0,0, 45,45,allCharacters,allyTurrets, allProjectiles))
  }catch{
    case e: Exception => exception = Some(e)
  }
  
  private val healthbarWidth: Int = 40
  private val healthbarHeigth: Int = 3
  private val random = new Random
  
  // Next 2 cloudCoordinates variables contain the x- coordinates of the place where the cloudLayer textures will be drawn.
  private var cloudCoordinates1: Float = 0.0f
  private var cloudCoordinates2: Float = 0.0f
  private var timePassed: Float = 0.0f
  private var i = 0
  private var coins = 999999999
  private val clickCoordinates: Vector3 = new Vector3(0,0,0)
  
  cam.setToOrtho(false, Game.VIEWPORT_WIDTH, Game.VIEWPORT_HEIGHT)
  
  
  private def cannonBarrage: Unit ={
    try{
        for(i <- 0 until 10){
          this.allProjectiles += new SpecialHit(300 + i*150, Game.GAME_WORLD_HEIGHT*3 + i * 200, false)
          this.allProjectiles += new SpecialHit(300 + i*150, (Game.GAME_WORLD_HEIGHT*3 + i * 200) * 3, false) 
        }
    }catch{
      case e: Exception => gsm.set(new ExceptionState(gsm,e))
    }
  }
  
  private def enemySpecial(defense: Boolean): Unit ={
    try{
      if(defense){
          for(i <- 0 until 30){
          this.allProjectiles += new  SpecialHit(Game.GAME_WORLD_WIDTH/2 + random.nextInt(800), Game.GAME_WORLD_HEIGHT*3 + random.nextInt(Game.GAME_WORLD_HEIGHT*2), true)
        }
      }else{
        for(i <- 0 until 90){
          this.allProjectiles += new  SpecialHit(185 + random.nextInt(Game.GAME_WORLD_WIDTH - 425), Game.GAME_WORLD_HEIGHT*3 + random.nextInt(Game.GAME_WORLD_HEIGHT*2), true)
        }
      }
    }catch{
      case e:Exception => gsm.set(new ExceptionState(gsm,e))
    }
  }
  
  private def addExplosion(projectile: Projectile)={
    try{
      for(i <- 0 until 3){
        this.allProjectiles += new Explosion(projectile.getPos.x, 50, projectile.fromEnemy)
      }
    }catch{
      case e: Exception => gsm.set(new ExceptionState(gsm,e))
    }
  }
  
  
  
  /* Handles users input. If you click and hold the first quarter of the screen, camera will go to left.
   * If you click and hold the last quarter of the screen, camera will go to left.
   * */
  protected def handleInput(): Unit = {
    // Next 2 lines transform click coordinates into game world coordinates.
    clickCoordinates.set(Gdx.input.getX, Gdx.input.getY, 0)
    cam.unproject(clickCoordinates)
    
    // Checks for user clicks on buttons
    if(Gdx.input.justTouched()){
      for(button <- this.allButtons){
        if(button.getHitbox.contains(clickCoordinates.x, clickCoordinates.y)){
          try{
            if(this.coins >= button.unitPrice){
              this.coins -= button.unitPrice
              button.press
            }else{
              println("no")
            }
          }catch{
            case e: Exception => exception = Some(e)
          }
        }
      }
    }
    
    try{
      if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)){
        this.allCharacters += new Swordsman(true, allCharacters, allProjectiles)
      }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)){
        this.allCharacters += new Marksman(true, allCharacters, allProjectiles)
      }else if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)){
        this.allCharacters += new Knight(true, allCharacters, allProjectiles)
      }else if(Gdx.input.isKeyJustPressed(Input.Keys.R)){
        this.allCharacters.clear()
      }else if(Gdx.input.isKeyJustPressed(Input.Keys.M)){
        this.cannonBarrage
      }else if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
        gsm.push(new MenuState(gsm))
      }else if(Gdx.input.isKeyJustPressed(Input.Keys.N)){
        this.enemySpecial(false)
      }
    }catch{
      case e: Exception => gsm.set(new ExceptionState(gsm,e))
    }

    
    if(Gdx.input.isTouched()){
      if(clickCoordinates.x <= cam.position.x - cam.viewportWidth/4 && cam.position.x > cam.viewportWidth/2 && clickCoordinates.y < Game.GAME_WORLD_HEIGHT * 0.875f){
        cam.translate(-15,0,0)
      }else if(clickCoordinates.x >= cam.position.x + cam.viewportWidth/4 && cam.position.x < Game.GAME_WORLD_WIDTH - cam.viewportWidth/2 && clickCoordinates.y < Game.GAME_WORLD_HEIGHT * 0.875f){
        cam.translate(15,0,0)
      }
    }
  }

  
  
  
  // Main loop. All it does now is check for users input and move the clouds which are drawn onto the screen.
  def uptade(dt: Float): Unit = {
    this.handleInput();
    cam.update();
    
    //Position of the buttons are updated relative to the camera's viewport.
    i=1
    for(j <- this.allButtons.indices){
      this.allButtons.reverse(j).getHitbox.setPosition(cam.position.x + Game.VIEWPORT_WIDTH/2 - 75 * i, Game.GAME_WORLD_HEIGHT * 0.875f)
      i += 1
    }
  
    this.castles.foreach(_.update(dt))
    this.allProjectiles.foreach(_.update(dt))
    try{
      this.allCharacters.foreach(_.update(dt))
      this.allyTurrets.filterNot(_ == null).foreach(_.update(dt))
    }catch{
      case e: Exception => gsm.set(new ExceptionState(gsm,e))
    }
    
    
    //Removes characters from game when their hitpoints hit 0.
    i=0
    while(i < this.allCharacters.size){
      val character = this.allCharacters(i)
      if(character.getHitpoints <= 0){
        this.allCharacters.remove(i)
        if(character.isEnemy){
          this.coins += character.getKillReward
        }
      }
      i += 1
    }
    
    //Removes projectiles from the game when they hit the ground.
    i=0
    while(i < this.allProjectiles.size){
      val projectile = this.allProjectiles(i)
      if(projectile.getPos.y < 45){
        if(projectile.canExplode){
          this.addExplosion(projectile)
        }
        allProjectiles.remove(i)
      }
      i += 1
    }
    
    this.cloudCoordinates1 -= 0.75f
    this.cloudCoordinates2 -= 0.25f
    if(this.cloudCoordinates1 <= -Game.GAME_WORLD_WIDTH){
      this.cloudCoordinates1 = 0
    }else if(this.cloudCoordinates2 <= -Game.GAME_WORLD_WIDTH){
      this.cloudCoordinates2 = 0
    }
  }
  
  
  
  // Paints the screen.
  def render(sb:SpriteBatch,bf: BitmapFont): Unit = {
    if(this.exception.isDefined){
      gsm.set(new ExceptionState(gsm,exception.get))
    }else{
      sb.setProjectionMatrix(cam.combined);
      this.timePassed += Gdx.graphics.getDeltaTime
      bf.setColor(255,215,0,255)
      sb.begin();
      sb.draw(this.backgroundImage.get, 0,0);
      
      
      for(p <- this.allProjectiles){
        sb.draw(p.getTexture, p.getPos.x, p.getPos.y)
      }
      
      for(char <- this.allCharacters){
        sb.draw(char.getAnimation._1.getKeyFrame(this.timePassed, true), char.getPosition.x - char.getAnimation._2, char.getPosition.y)
        sb.draw(this.healthbarRed.get, char.getPosition.x + char.getSize._1/4f, char.getSize._2 + 60, this.healthbarWidth, this.healthbarHeigth)
        sb.draw(this.healthbarGreen.get, char.getPosition.x + char.getSize._1/4f, char.getSize._2 + 60,((char.getHitpoints * 1.0/ char.getMaxHitpoints)* this.healthbarWidth).toFloat, this.healthbarHeigth)
      }
      
      for(tower <- this.castles){
        sb.draw(tower.getTexture, tower.getPosition.x, tower.getPosition.y, tower.getHitbox.width, tower.getHitbox.height)
      }
      for(turret <- this.allyTurrets.filterNot(_ == null)){
        sb.draw(turret.getTexture._1, turret.getPosition.x, turret.getPosition.y,turret.getTexture._1.getWidth/2, turret.getTexture._1.getHeight/2, turret.getTexture._1.getWidth, turret.getTexture._1.getHeight, 1, 1, Math.toDegrees(turret.getTexture._2).toFloat, 0, 0 , turret.getTexture._1.getWidth, turret.getTexture._1.getHeight,turret.getTexture._3, false  )
      }
  
      sb.draw(this.cloudLayer1.get, this.cloudCoordinates1,0f)
      sb.draw(this.cloudLayer1.get, this.cloudCoordinates1 + Game.GAME_WORLD_WIDTH,0)
      sb.draw(this.cloudLayer2.get, this.cloudCoordinates2,0f)
      sb.draw(this.cloudLayer2.get, this.cloudCoordinates2 + Game.GAME_WORLD_WIDTH,0)
      sb.draw(this.panels.get, cam.position.x - Game.VIEWPORT_WIDTH/2, 0)
      
      for(button <- this.allButtons){
        sb.draw(button.getTexture, button.getHitbox.x, button.getHitbox.y)
        if(button.isHovered(clickCoordinates.x, clickCoordinates.y)){
          val x = cam.position.x - Game.VIEWPORT_WIDTH/5
          bf.draw(sb, button.toString(), x, Game.VIEWPORT_HEIGHT - 20)
        }
      }
      sb.draw(this.healthbarRed.get,cam.position.x - Game.VIEWPORT_WIDTH/2 + 50,425, 120, 10)
      sb.draw(this.healthbarGreen.get,cam.position.x - Game.VIEWPORT_WIDTH/2 + 50,425,((this.castles(0).getHitpoints*1.0/this.castles(0).getMaxHitpoints) * 120).toInt , 10)
      
      sb.draw(this.healthbarRed.get,Game.GAME_WORLD_WIDTH - 25, 250, 20,150)
      sb.draw(this.healthbarGreen.get,Game.GAME_WORLD_WIDTH - 25, 250, 20, ((this.castles(1).getHitpoints *1.0/this.castles(1).getMaxHitpoints) * 150).toInt)
      
      bf.setColor(255,0,0,255)
      bf.draw(sb, this.castles(0).getHitpoints.toString,cam.position.x - Game.VIEWPORT_WIDTH/2 + 100 , 425)
      bf.draw(sb, this.castles(1).getHitpoints.toString ,Game.GAME_WORLD_WIDTH - 60, 400)
      bf.setColor(255,215,0,255)
      bf.draw(sb, ": "+this.coins +" $", cam.position.x - Game.VIEWPORT_WIDTH/2 +50, 465)
      sb.end();
    }
  }
  
  
  // Gets rid of the textures after the state is shut.
   def dispose(): Unit = {
     if(!this.exception.isDefined){
       this.backgroundImage.get.dispose()
       this.panels.get.dispose()
       this.healthbarRed.get.dispose()
       this.healthbarGreen.get.dispose()
       this.cooldown.get.dispose()
       this.cloudLayer1.get.dispose()
       this.cloudLayer2.get.dispose()
       for(char <- this.allCharacters){
         char.dispose
       }
       for(castle <- this.castles){
         castle.dispose
       }
       for(turret <- this.allyTurrets.filterNot(_ == null)){
         turret.dispose
       }
       for(button <- this.allButtons){
         button.dispose
       }
     }
   }

}