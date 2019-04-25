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
import java.io.BufferedReader
import java.io.FileReader
import scala.util.Random
import com.badlogic.gdx.audio._

/*Play state of the program. This state contains the game loop etc. 
 */

class PlayState(gsm: GameStateManager) extends State(gsm){
  // If an exception occurs in the try block below, the exception will be saved here to be handles somewhere else.
  private var exception: Option[Exception] = None
  // Variables for image and music files. They will be loaded in a try block due to possible exceptions.
  private var music: Option[Music] = None
  private var backgroundImage: Option[Texture] = None
  private var cloudLayer1: Option[Texture] = None
  private var cloudLayer2: Option[Texture] = None
  private var panels: Option[Texture] = None
  private var healthbarRed: Option[Texture] = None
  private var healthbarGreen: Option[Texture] = None
  private var cooldown: Option[Texture] = None
  
  // Containers for all the units in the game.
  private var castles = Array[Tower]()
  private val allCharacters = Buffer[Character]()
  private val allProjectiles = Buffer[Projectile]()
  private val allyTurrets = Array.ofDim[Turret](2)
  private val enemyTurrets = Array.ofDim[Turret](1)
  private var charButtons: Array[Button] = Array[Button]()
  private var supportButton: Option[Button] = None
  
  /*Files will be loaded from try block, since they can throw an exception
   * if the file is missing.
   */
   try{
    backgroundImage = Some(new Texture("bg.png"))
    cloudLayer1 = Some(new Texture("fastclouds.png"))
    cloudLayer2 = Some(new Texture("slowclouds.png"))
    panels = Some(new Texture("panels.png"))
    healthbarRed = Some(new Texture("blankRed.png"))
    healthbarGreen = Some(new Texture("blankGreen.png"))
    cooldown = Some(new Texture("cooldown.png"))
    supportButton = Some(new SupportButton(0,0,80,50))
    castles = Array[Tower](new Tower(false, allCharacters, allProjectiles), new Tower(true, allCharacters, allProjectiles))
    charButtons = Array(new swordmanButton(0,0,45,45,allCharacters,allProjectiles),new MarksmanButton(0,0, 45,45, allCharacters, allProjectiles),new KnightButton(0,0, 45,45,allCharacters, allProjectiles),new CannonButton(0,0, 45,45,allCharacters,allyTurrets, allProjectiles))
  }catch{
    case e: Exception => exception = Some(e)  // Exception will be handled in the render method.
  }
  
  try{
    music = Some(Gdx.audio.newMusic(Gdx.files.internal("music.mp3")))
  }catch{
    case e: Exception =>  // Music wont be played if it is missing. Throws same exception as missing image, thats why 2 separate try-catch blocks.
  }
  
  private val healthbarWidth: Int = 40
  private val healthbarHeigth: Int = 3
  private val random = new Random()
  
  // Next 2 cloudCoordinates variables contain the x- coordinates of the place where the cloudLayer textures will be drawn.
  private var cloudCoordinates1: Float = 0.0f
  private var cloudCoordinates2: Float = 0.0f
  private var timePassed: Float = 0.0f
  private var i: Int = 0                            // Used in while loops.
  private var coins: Int = 175
  private var CBcooldown: Float = 0                 // Cooldown timer of fire support ability.
  private var enemySpecialCooldown: Float = 0       // Same but enemies.
  private var charCooldown: Float = 0               // How long till you can bring in a new character.

  private val clickCoordinates: Vector3 = new Vector3(0,0,0)
  cam.setToOrtho(false, Game.VIEWPORT_WIDTH, Game.VIEWPORT_HEIGHT)
  if(this.music.isDefined){
    this.music.get.setLooping(true)
    this.music.get.setVolume(0.25f)
    if(!Game.muted) this.music.get.play()
  }
  
  /* Next Buffers are the probabilities of a certain character type the enemy will bring into the game during current wave.
   * Probabilities are read from a file, but if the file is invalid, the default buffer will be used */
  private var probabilities: Buffer[String] = Buffer[String]()
  private val probabilitiesDefault = Buffer("50,50,0", "50,50,0", "40,40,20", "30,30,40")
  private val probabilitiesFile = Buffer[String]()           
  private var level = 0
  private var frequency = 5             // used to determine how often enemy character is brought to the game.
  private var enemyTimer: Float = 1.0f  // Time when the next enemy is brought into the game
  private var enemyWaveSize: Int = 8    // How many enemies will be brought until we take values from next line.
  private var enemiesBrought: Int = 0   // How many enemies are brought so far.
  private var currentRow = 0            // Index of current element we are reading from the Buffer of probabilities
  
  /* Reads a text file, which contains probabilities of enemy character types. If the block throws an exception or
   * the file is invalid such as sum of probabilities differ from 100 or number of values differ from amount of character types,
   * the file will be ignored and the game will use default values.
   * Had to put it in a method so it could be tested by an unit test.
   *  */
  def readFile(filename: String): Unit = {
    try{
      val in = new BufferedReader(new FileReader(filename))
      var str = ""
      
      str = in.readLine()
      while(str != null){
        this.probabilitiesFile += str
        str = in.readLine()
      }
      val values = (this.probabilitiesFile.map(a => a.split(","))).map(a => a.map(_.toInt))
      val isValid = values.forall(a => a.size == 3 && a.sum == 100)
      if(isValid){
        this.probabilities = this.probabilitiesFile
      }else{
        this.probabilities = this.probabilitiesDefault
      }
    }catch{
      case e: Exception => this.probabilities = this.probabilitiesDefault  // We do the same thing no matter what exception it will throw.
    }
  }
  
  this.readFile("data.txt")

  
  
  
  
  /* Next 2 methods bring the fire support of the enemy and ally team.
   * They can throw an exception if the image file of their class is missing.
   */
   private def cannonBarrage: Unit ={
     try{
      for(i <- 0 until 11){
        val x = 300 + i*150
        val y = Game.GAME_WORLD_HEIGHT*3 + i * 200
        this.allProjectiles += new SpecialHit(x, y, false)
        this.allProjectiles += new SpecialHit(x, y*3, false) 
      }
     }catch{
       case e: Exception => gsm.set(new ExceptionState(gsm,e))
     }
  }
   
   
   private def enemySpecial(defense: Boolean): Unit ={
     try{
        if(defense){
          for(i <- 0 until 60){
            val x = Game.GAME_WORLD_WIDTH/2 + random.nextInt(800)
            val y = Game.GAME_WORLD_HEIGHT*3 + random.nextInt(Game.GAME_WORLD_HEIGHT*2)
            this.allProjectiles += new  SpecialHit(x, y, true)
          }
        }else{
          for(i <- 0 until 90){
            val x = 185 + random.nextInt(Game.GAME_WORLD_WIDTH - 425)
            val y = Game.GAME_WORLD_HEIGHT*3 + random.nextInt(Game.GAME_WORLD_HEIGHT*2)
            this.allProjectiles += new  SpecialHit(x, y, true)
          }
        }
     }catch{
       case e: Exception => gsm.set(new ExceptionState(gsm,e))
     }
  }
   
   // When an explosive projectile hits something, this method will be called.
   private def addExplosion(projectile: Projectile)={
     try{
      for(i <- 0 until 3){
        this.allProjectiles += new Explosion(projectile.getPos.x, 50, projectile.fromEnemy)
       }
     }catch{
       case e: Exception => gsm.set(new ExceptionState(gsm,e))
     }
  }
  
  
  
  
  // Handles users input, such as clicks on buttons etc.
  protected def handleInput(): Unit = {
    // Next 2 lines transform click coordinates into game world coordinates.
    clickCoordinates.set(Gdx.input.getX, Gdx.input.getY, 0)
    cam.unproject(clickCoordinates)
    if(Game.muted && this.music.isDefined) this.music.get.pause()
    
    // Checks for user clicks on buttons
    if(Gdx.input.justTouched()){
      /* Brings character, reduces cost from coins, sets cooldown
       * Throws and exception if the files of the character the button brings are missing.
       */
      for(button <- charButtons){
        if(button.getHitbox.contains(clickCoordinates.x, clickCoordinates.y) && this.charCooldown == 0){
          try{
            if(coins >= button.unitPrice){
              this.coins -= button.unitPrice
              this.charCooldown = button.getCooldown
              button.press
            }else{
              println("Not enough money")
            }
          }catch{
            case e: Exception => gsm.set(new ExceptionState(gsm,e))
          }
        }
      }
      // Brings fire support when pressed and sets button on cooldown.
       if(this.supportButton.get.getHitbox.contains(clickCoordinates.x, clickCoordinates.y) && CBcooldown == 0){
          this.cannonBarrage
          this.CBcooldown = this.supportButton.get.getCooldown
       }
    }    
    // Moves camera if user clicks and hold left or right quarter.
    if(Gdx.input.isTouched()){
      if(clickCoordinates.x <= cam.position.x - cam.viewportWidth/4 && cam.position.x > cam.viewportWidth/2 && clickCoordinates.y < Game.GAME_WORLD_HEIGHT * 0.875f){
        cam.translate(-15,0,0)
      }else if(clickCoordinates.x >= cam.position.x + cam.viewportWidth/4 && cam.position.x < Game.GAME_WORLD_WIDTH - cam.viewportWidth/2 && clickCoordinates.y < Game.GAME_WORLD_HEIGHT * 0.875f){
        cam.translate(15,0,0)
      }
    }
    // Pauses game when ESC is pressed.
    if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
      gsm.push(new PauseState(gsm, music))                  
    }
  }

  
  
  
  
  // Main loop.
  def uptade(dt: Float): Unit = {
    
    /* This block of code brings enemies into the game if there are less than the maximum amount of enemies there can be.
     * This rolls a random number between 0-99. If number is in range of [0 to (first probability of the array) - 1], we bring a swordsman
     * if it is in range of [(first probability of array) to (second probability - 1)] we bring a Marksman and so forth.
     * Throws an exception if the files of the enemy characters are missing.
     */
    if(this.timePassed >= this.enemyTimer && this.allCharacters.filter(_.isEnemy).size < this.enemyWaveSize){
      this.enemyTimer += 1 + random.nextInt(this.frequency)               // Earliest time next enemy can be brought.
      val rng = random.nextInt(100)
      val row = this.probabilities(this.currentRow).split(",")
      try{
        if(rng >= 0 && rng < row(0).toInt){
          this.allCharacters += new Swordsman(true, this.allCharacters, this.allProjectiles)
        }else if(rng >= row(0).toInt && rng < (row(0).toInt + row(1).toInt)){
          this.allCharacters += new Marksman(true, this.allCharacters, this.allProjectiles)
        }else{
          this.allCharacters += new Knight(true, this.allCharacters, this.allProjectiles)
        }
      }catch{
        case e: Exception => gsm.set(new ExceptionState(gsm,e))
      }
      /* If a whole wave of enemies are brought with the current line, we switch to next line.
       * If current line is the last line, we switch back to the first line. */
      this.enemiesBrought += 1
      if(this.enemiesBrought % this.enemyWaveSize == 0){
        this.currentRow += 1
        if(this.currentRow == this.probabilities.size){
          this.currentRow = 0
          if(this.level > 0) this.level += 1
        }
      }
    }
    
    /* Enemy deploys a turret when they have brought 6 enemies and upgrades it at 96 enemies brought.
     * Throws an exception if the files of the turret classes are missing. */
    try{
      if(enemiesBrought == 6){
        this.enemyTurrets(0) = new TowerCannon(true, this.allCharacters, this.allProjectiles, 200)
      }else if(enemiesBrought == 96){
        this.enemyTurrets(0) = new DoubleCannon(true, this.allCharacters, this.allProjectiles, 200)
      }
    }catch{
      case e: Exception => gsm.set(new ExceptionState(gsm,e)) 
    }
    
    // If user has 8 or more characters in enemy's side of map, enemy will call for fire support.
    if(this.allCharacters.filter(a => !a.isEnemy && a.getPosition.x >= Game.GAME_WORLD_WIDTH/2).size >= 8){
      this.enemySpecial(true)
      this.enemySpecialCooldown = 5
    }
    // Enemy calls for firesupport the first time its castle's hitpoints hit 450.
    if(this.castles.filter(_.isEnemy)(0).getHitpoints <= 450 && level == 0){
      this.enemySpecial(true)
      this.enemySpecialCooldown = 5
      this.level += 1
    }
    if(this.level % 6 == 1 || this.level % 6 == 2) this.enemyWaveSize = 12 else this.enemyWaveSize = 8      // Enemy can bring more units when level%6 is 1 or 2.
    if(this.level % 6 == 1 || this.level % 6 == 2) this.frequency = 3 else this.frequency = 5               // They also bring them more frequently.
    if((this.level % 6 == 1 || this.level % 6 == 2) && this.enemySpecialCooldown == 0){                     // They also call for firesupport every 3 seconds.
      this.enemySpecial(false)
      this.enemySpecialCooldown = 3
    }
    
    this.handleInput();
    cam.update();
    
    //Position of the buttons are updated relative to the camera's viewport.
    this.i = 1
    for(j <- this.charButtons.indices){
      this.charButtons.reverse(j).getHitbox.setPosition(cam.position.x + Game.VIEWPORT_WIDTH/2 - 75 * i, Game.GAME_WORLD_HEIGHT * 0.875f)
      this.i += 1
    }
    this.supportButton.get.getHitbox.setPosition(cam.position.x + Game.VIEWPORT_WIDTH/2 - 300, Game.GAME_WORLD_HEIGHT * 0.75f)
    
    /*Update method of all units in the game is called.
     * Character and turret objects's update methods will throw and exception if the
     * files of the projectiles they shoot are missing.  */
    this.castles.foreach(_.update(dt)) 
    this.allProjectiles.foreach(_.update(dt))
    try{
      this.allCharacters.foreach(_.update(dt))
      this.allyTurrets.filterNot(_ == null).foreach(_.update(dt))
      this.enemyTurrets.filterNot(_ == null).foreach(_.update(dt))
    }catch{
      case e: Exception => gsm.set(new ExceptionState(gsm,e))
    }
    
    // Win or lose depending on which castle hits 0 hitpoints first.
    if(this.castles.exists(_.getHitpoints <= 0)){
      val destroyedCastle = this.castles.find(_.getHitpoints <= 0).get
      val isEnemy = destroyedCastle.isEnemy
      gsm.set(new GameoverState(gsm, isEnemy))
    }
    
    //Removes characters from game when their hitpoints hit 0 and adds kill reward to coins if it is a enemy who died.
    this.i = 0
    while(i < this.allCharacters.size){
      val character = allCharacters(i)
      if(character.getHitpoints <= 0){
        this.allCharacters.remove(i)
        if(character.isEnemy){
          this.coins += character.getKillReward
        }
      }
      this.i += 1
    }
    //Removes projectiles from the game when they hit the ground. Adds explosion, if it is an explosive projectile.
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
    // Moves background clouds and replaces them if they move out of screen.
    this.cloudCoordinates1 -= 0.75f
    this.cloudCoordinates2 -= 0.25f
    if(this.cloudCoordinates1 <= -Game.GAME_WORLD_WIDTH){
      this.cloudCoordinates1 = 0
    }else if(cloudCoordinates2 <= -Game.GAME_WORLD_WIDTH){
      this.cloudCoordinates2 = 0
    }
  }
  
  
  
  
  
  /* Renders all the texture onto the screen.
   * First we check if an exception was thrown while loading the image files.
   * If yes, program will redirrect to an exception state, else images will be rendered normally.
   */
  def render(sb:SpriteBatch,bf: BitmapFont): Unit = {
    if(exception.isDefined){
      gsm.set(new ExceptionState(gsm,exception.get))
    }else{
      sb.setProjectionMatrix(cam.combined);
      this.timePassed += Gdx.graphics.getDeltaTime
      this.charCooldown = Math.max(0, this.charCooldown - Gdx.graphics.getDeltaTime)
      this.CBcooldown = Math.max(0, this.CBcooldown - Gdx.graphics.getDeltaTime)
      this.enemySpecialCooldown = Math.max(0, this.enemySpecialCooldown - Gdx.graphics.getDeltaTime)
      bf.setColor(255,215,0,255)
      sb.begin();
      sb.draw(this.backgroundImage.get, 0,0);
      
      
      for(p <- this.allProjectiles){
        sb.draw(p.getTexture, p.getPos.x, p.getPos.y)
      }
      // Draws characters and their healthbars.
      for(char <- this.allCharacters){
        val hpX = char.getPosition.x + char.getSize._1/4f
        val hpY = char.getSize._2 + 60
        val healthSize = ((char.getHitpoints * 1.0/ char.getMaxHitpoints)* this.healthbarWidth).toFloat
        sb.draw(char.getAnimation._1.getKeyFrame(timePassed, true), char.getPosition.x - char.getAnimation._2, char.getPosition.y)
        sb.draw(this.healthbarRed.get, hpX, hpY, this.healthbarWidth, this.healthbarHeigth)
        sb.draw(this.healthbarGreen.get, hpX, hpY, healthSize, this.healthbarHeigth)
      }
      
      for(tower <- this.castles){
        sb.draw(tower.getTexture, tower.getPosition.x, tower.getPosition.y, tower.getHitbox.width, tower.getHitbox.height)
      }
      // 2 next for loops draws all the castle turrets. They take more parameters because, they also rotate so they can always face the nearest enemy.
      for(turret <- this.allyTurrets.filterNot(_==null)){
        sb.draw(turret.getTexture._1, turret.getPosition.x, turret.getPosition.y,turret.getTexture._1.getWidth/2, turret.getTexture._1.getHeight/2, turret.getTexture._1.getWidth, turret.getTexture._1.getHeight, 1, 1, Math.toDegrees(turret.getTexture._2).toFloat, 0, 0 , turret.getTexture._1.getWidth, turret.getTexture._1.getHeight,turret.getTexture._3, false  )
      }
      for(turret <- this.enemyTurrets.filterNot(_==null)){
        sb.draw(turret.getTexture._1, turret.getPosition.x, turret.getPosition.y,turret.getTexture._1.getWidth/2, turret.getTexture._1.getHeight/2, turret.getTexture._1.getWidth, turret.getTexture._1.getHeight, 1, 1, Math.toDegrees(turret.getTexture._2).toFloat, 0, 0 , turret.getTexture._1.getWidth, turret.getTexture._1.getHeight,turret.getTexture._3, false  )
      }
  
      sb.draw(this.cloudLayer1.get, this.cloudCoordinates1,0f)
      sb.draw(this.cloudLayer1.get, this.cloudCoordinates1 + Game.GAME_WORLD_WIDTH,0)
      sb.draw(this.cloudLayer2.get, this.cloudCoordinates2,0f)
      sb.draw(this.cloudLayer2.get, this.cloudCoordinates2 + Game.GAME_WORLD_WIDTH,0)
      sb.draw(this.panels.get, cam.position.x - Game.VIEWPORT_WIDTH/2, 0)
      
      // Draws buttons and draws their description on the screen when hovered. Draws grey layer on top indicating cooldown length.
      for(button <- this.charButtons){
        sb.draw(button.getTexture, button.getHitbox.x, button.getHitbox.y)
        sb.draw(this.cooldown.get, button.getHitbox.x, button.getHitbox.y, this.charCooldown * button.getTexture.getWidth, button.getTexture.getHeight)
        if(button.isHovered(clickCoordinates.x, clickCoordinates.y)){
          val x = cam.position.x - Game.VIEWPORT_WIDTH/5
          bf.draw(sb, button.toString(), x, Game.VIEWPORT_HEIGHT - 20)
        }
      }
      val cooldownSize = this.CBcooldown/this.supportButton.get.getCooldown * this.supportButton.get.getTexture.getWidth
      sb.draw(this.supportButton.get.getTexture, this.supportButton.get.getHitbox.x, this.supportButton.get.getHitbox.y)
      sb.draw(this.cooldown.get, this.supportButton.get.getHitbox.x, this.supportButton.get.getHitbox.y, cooldownSize  ,this.supportButton.get.getTexture.getHeight)
      if(this.supportButton.get.isHovered(clickCoordinates.x, clickCoordinates.y)){
        bf.draw(sb, this.supportButton.get.toString(),cam.position.x - Game.VIEWPORT_WIDTH/5,Game.VIEWPORT_HEIGHT - 20)
      }
      
      //draws enemy and ally castle's health bars.
      val allyHealth = ((this.castles(0).getHitpoints*1.0/this.castles(0).getMaxHitpoints) * 120).toInt
      val enemyHealth = ((this.castles(1).getHitpoints *1.0/this.castles(1).getMaxHitpoints) * 150).toInt
      sb.draw(this.healthbarRed.get,cam.position.x - Game.VIEWPORT_WIDTH/2 + 50,425, 120, 10)
      sb.draw(this.healthbarGreen.get,cam.position.x - Game.VIEWPORT_WIDTH/2 + 50,425, allyHealth , 10)
      sb.draw(this.healthbarRed.get,Game.GAME_WORLD_WIDTH - 25, 250, 20,150)
      sb.draw(this.healthbarGreen.get,Game.GAME_WORLD_WIDTH - 25, 250, 20, enemyHealth)
      //2 next draw methods draw castle's hitpoint values near the health bar.
      bf.setColor(255,0,0,255)
      bf.draw(sb, this.castles(0).getHitpoints.toString,cam.position.x - Game.VIEWPORT_WIDTH/2 + 100 , 425)
      bf.draw(sb, this.castles(1).getHitpoints.toString ,Game.GAME_WORLD_WIDTH - 60, 400)
      bf.setColor(255,215,0,255)
      //Draw the amount of coins to the screen.
      bf.draw(sb, ": "+this.coins +" $", cam.position.x - Game.VIEWPORT_WIDTH/2 +50, 465)
      sb.end();
    }
  }
    
  
  
  
  
  
  // Gets rid of the textures after the state is shut.
   def dispose(): Unit = {
     if(!this.exception.isDefined){
       this.backgroundImage.get.dispose()
       this.cloudLayer1.get.dispose()
       this.cloudLayer2.get.dispose()
       this.panels.get.dispose()
       this.healthbarGreen.get.dispose()
       this.healthbarRed.get.dispose()
       this.cooldown.get.dispose
       for(char <- this.allCharacters){
         char.dispose
       }
       for(castle <- this.castles){
         castle.dispose
       }
       for(turret <- this.allyTurrets.filterNot(_ == null)){
         turret.dispose
       }
       for(button <- this.charButtons){
         button.dispose
       }
       this.supportButton.get.dispose
       if(music.isDefined){
         this.music.get.stop()
         this.music.get.dispose()
       }
     }
   }
}
