package Units

import Objects._
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import scala.collection.mutable.Buffer
import Game.Game
import com.badlogic.gdx.Gdx

// This class does not differ much from Assasin class. Method are explained there.

class Marksman(isEnemy: Boolean, allCharacters: Buffer[Character],allProjectiles:Buffer[Projectile]) extends Character(isEnemy, allCharacters, allProjectiles) {
  private val attackRange = 250
  private var hitpoints = 100
  private val speed = if(!isEnemy){ 1.25f } else { -1.25f }
  private val charWidth = 50
  private val charHeight = 90
  private val xCoord = 190
  private val position = new Vector3(if(isEnemy){ Game.GAME_WORLD_WIDTH-xCoord }else{ xCoord }, 50, 0)
  private val hitbox = new Rectangle(position.x, position.y, charWidth, charHeight)
  private val killReward = 25
  
  private val walkImage = if(!isEnemy){ new TextureAtlas("Animations/anim3.atlas") }else{ new TextureAtlas("Animations/anim6.atlas") }
  private val walkAnimation = new Animation[TextureRegion](1/8f, walkImage.getRegions)
  private val standImage = if(!isEnemy){ new TextureAtlas("Animations/stand3.atlas") }else{ new TextureAtlas("Animations/stand6.atlas") }
  private val standAnimation = new Animation[TextureRegion](1f, standImage.getRegions)
  private val hitImage = if(!isEnemy){ new TextureAtlas("hit3.atlas") }else{ new TextureAtlas("hit6.atlas") }
  private val hitAnimation = new Animation[TextureRegion](1/8f, hitImage.getRegions)
  private var isStanding = false
  private var inCombat = false
  
  private var sound: Option[com.badlogic.gdx.audio.Sound] = None
  try{
    sound = Some(Gdx.audio.newSound(Gdx.files.internal("hit.mp3")))
  }catch{
    case e: Exception => // If exception is thrown, there will be no sound.
  }
  
  private var timePassed = 0f
  
  
  
  def update(dt: Float): Unit = {
    this.timePassed += Gdx.graphics.getDeltaTime
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
  
  
  
  // Check the coordinates of other characters and returns true if nobody is in front.
  private def canMove: Boolean = {
    if(!isEnemy) {
      this.isStanding = this.allCharacters.exists(a => (a.getPosition.x - this.position.x > 0) && (a.getPosition.x - this.position.x < 60)) || this.position.x > Game.GAME_WORLD_WIDTH - 175 - 60
      this.inCombat = this.allCharacters.exists(a => (a.getPosition.x - this.position.x > 0) && (a.getPosition.x - this.position.x < this.attackRange) && a.isEnemy) || this.position.x > Game.GAME_WORLD_WIDTH - 175 - this.attackRange
      !this.isStanding
    }else{
      this.isStanding = this.allCharacters.exists(a => (a.getPosition.x - this.position.x < 0) && (a.getPosition.x - this.position.x > -60)) || this.position.x < 220 + 60
      this.inCombat = this.allCharacters.exists(a => (a.getPosition.x - this.position.x < 0) && (a.getPosition.x - this.position.x > - this.attackRange) && !a.isEnemy) || this.position.x < 220 + this.attackRange
      !this.isStanding
    }
  }
  
  private def hitEnemy(): Unit ={
    this.timePassed += Gdx.graphics.getDeltaTime
    if(this.timePassed >= 2){
      val shootingPos = if(!isEnemy){ this.position.x + this.hitbox.getWidth }else{ this.position.x }
      this.allProjectiles += new RangedHit(shootingPos, (this.position.y) + (this.hitbox.getHeight/2), this.isEnemy)
      this.timePassed = 0f
    }
  }
  
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
  
  private def checkIfHit(): Unit ={
    val isHit = this.allProjectiles.exists(a => a.getHitbox.overlaps(this.getHitbox) && (a.fromEnemy != this.isEnemy))
    if(isHit){
      val hitBy = this.allProjectiles.find(a => a.getHitbox.overlaps(this.getHitbox) && (a.fromEnemy != this.isEnemy)).get
      this.getHit(hitBy)
    }
  }
  
  
  
  
  def getPosition: Vector3 = this.position
  
  def getHitbox: Rectangle = this.hitbox
  
  def getAnimation: (Animation[TextureRegion],Int) = {
    if(this.inCombat && !this.isStanding){
      if(this.isEnemy){
        (hitAnimation,0)
      }else{
        (hitAnimation,-5)
      }
    }else if(this.isStanding){
      (standAnimation,0)
    }else{
      (walkAnimation,0)
    }
  }
  
  def getHitpoints: Int = this.hitpoints
  
  def getMaxHitpoints: Int = 100
  
  def getKillReward: Int = this.killReward
  
  def getSize: (Int, Int) = if(!this.isEnemy){ (this.charWidth, this.charHeight) } else { (25, this.charHeight) }
 
  def dispose: Unit = {
    this.walkImage.dispose()
    this.standImage.dispose()
    this.hitImage.dispose()
    if(this.sound.isDefined) this.sound.get.dispose()
  }
}