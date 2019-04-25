package Units

import Objects._
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import scala.collection.mutable.Buffer
import com.badlogic.gdx.Gdx
import Game.Game

// Swordsman is a weak melee character.
class Swordsman(isEnemy: Boolean, allCharacters: Buffer[Character],allProjectiles:Buffer[Projectile]) extends Character(isEnemy, allCharacters, allProjectiles) {
  private val attackRange = if(!isEnemy){ 60 } else { -60 }
  private var hitpoints = 100
  private val speed = if(!isEnemy){ 1.25f } else { -1.25f }
  private val charWidth = 50
  private val charHeight = 90
  private val xCoord = 190
  private val position = new Vector3(if(isEnemy){ Game.GAME_WORLD_WIDTH-xCoord }else{ xCoord }, 50, 0)
  private val hitbox = new Rectangle(position.x, position.y, charWidth, charHeight)
  private val killReward = 20
  
  private val walkImage = if(!isEnemy){ new TextureAtlas("Animations/anim1.atlas") }else{ new TextureAtlas("Animations/anim4.atlas") }
  private val walkAnimation = new Animation[TextureRegion](1/9f, walkImage.getRegions)
  private val standImage = if(!isEnemy){ new TextureAtlas("Animations/stand1.atlas") }else{ new TextureAtlas("Animations/stand4.atlas") }
  private val standAnimation = new Animation[TextureRegion](1f, standImage.getRegions)
  private val hitImage = if(!isEnemy){ new TextureAtlas("hit1.atlas") }else{ new TextureAtlas("hit4.atlas") }
  private val hitAnimation = new Animation[TextureRegion](1/6f, hitImage.getRegions)
  private var isStanding = false
  private var inCombat = false
  
  private var sound: Option[com.badlogic.gdx.audio.Sound] = None
  try{
    sound = Some(Gdx.audio.newSound(Gdx.files.internal("hit.mp3")))
  }catch{
    case e: Exception => // If exception is thrown, there will be no sound.
  }
  
  // A second timer which is used to time the attacks of the character.
  private var timePassed = 0f
  
  /* This method is called every frame by the PlayState's main loop.
   * Every frame, the character checks its front. If nobody is there, character advances,
   *otherwise stands still. If enemy is in from character joins combat. Lastly it checks if it has been hit.
   */
  def update(dt: Float): Unit = {
    this.advance()
    this.hitbox.setPosition(this.position.x, this.position.y)
    if(this.inCombat){
      this.hitEnemy()
    }
    this.checkIfHit()
  }
  
  
  private def advance(): Unit = {
    if(this.canMove){
      this.position.add(this.speed,0, 0)
    }
  }
  
  
  
  /*Check the coordinates of other characters and returns true if no character or opposing castle is in front.
   *Sets value of inCombat as true if enemy or opposing castle is in front.
   */
  private def canMove: Boolean = {
    if(!isEnemy) {
      this.isStanding = (this.allCharacters.exists(a => (a.getPosition.x - this.position.x > 0) && (a.getPosition.x - this.position.x < this.attackRange))) || this.position.x > Game.GAME_WORLD_WIDTH - 175 - this.attackRange
      this.inCombat = (this.allCharacters.exists(a => (a.getPosition.x - this.position.x > 0) && (a.getPosition.x - this.position.x < this.attackRange) && a.isEnemy)) || this.position.x > Game.GAME_WORLD_WIDTH - 175 - this.attackRange
      !this.isStanding
    }else{
      this.isStanding = (this.allCharacters.exists(a => (a.getPosition.x - this.position.x < 0) && (a.getPosition.x - this.position.x > this.attackRange -1))) || this.position.x < 220 - this.attackRange
      this.inCombat = (this.allCharacters.exists(a => (a.getPosition.x - this.position.x < 0) && (a.getPosition.x - this.position.x > this.attackRange - 1) && !a.isEnemy)) || this.position.x < 220 - this.attackRange
      !this.isStanding
    }
  }
  
  // Hits the enemy in front of the character. This character inflicts damage every 0.66 seconds.
  private def hitEnemy(): Unit ={
    this.timePassed += Gdx.graphics.getDeltaTime
    if(this.timePassed >= 4/6f){
      this.allProjectiles += new MeleeHit(this.position.x + this.attackRange, this.position.y, this.isEnemy,25)
      this.timePassed = 0f
    }
  }
  
  /*Takes damage when a projectile collides with it and removed the projectile.
   * If projectile is an explosive one, 3 new explosion projectiles are brought to the game.
   */
  private def getHit(projectile: Projectile){
    this.hitpoints -= projectile.getDamage
    if(!Game.muted && this.sound.isDefined) this.sound.get.play(1f)
    
    if(projectile.canExplode){
      for(i <- 0 until 3){
        this.allProjectiles += new Explosion(projectile.getPos.x, projectile.getPos.y, projectile.fromEnemy)
      }
    }
    this.allProjectiles -= projectile
  }
  
  // Checks if any projectile is inside its hitbox.
  private def checkIfHit(): Unit ={
    val isHit = this.allProjectiles.exists(a => a.getHitbox.overlaps(this.getHitbox) && (a.fromEnemy != this.isEnemy))
    if(isHit){
      val hitBy = this.allProjectiles.find(a => a.getHitbox.overlaps(this.getHitbox) && (a.fromEnemy != this.isEnemy)).get
      this.getHit(hitBy)
    }
  }
  
  def getPosition: Vector3 = this.position
  
  def getHitbox: Rectangle = this.hitbox
  
  /* Returns the correct image to be drawn onto the screen.
   * Some images need to be shifted by few pixels due to whitespace,
   * thats why it returns the Int in the tuple.
   */
  def getAnimation: (Animation[TextureRegion],Int) = {
    if(inCombat){
      if(isEnemy){
        (hitAnimation,25)
      }else{
        (hitAnimation,0)
      }
    }else if(isStanding && !inCombat){
      (standAnimation,0)
    }else{
      (walkAnimation,0)
    }
  }

  def getHitpoints: Int = this.hitpoints
  
  def getMaxHitpoints: Int = 100
  
  def getKillReward: Int = this.killReward
  
  def getSize: (Int, Int) = if(!this.isEnemy){ (this.charWidth - 20, this.charHeight) } else { (-5, this.charHeight) }
  
  // Gets rid of all the textures when they are no longer needed.
  def dispose: Unit = {
    this.walkImage.dispose()
    this.standImage.dispose()
    this.hitImage.dispose()
    if(this.sound.isDefined) this.sound.get.dispose()
  }
}