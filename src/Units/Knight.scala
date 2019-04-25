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

// This class does not differ much from Swordsman class. Methods are explained there.

class Knight(isEnemy: Boolean, allCharacters: Buffer[Character],allProjectiles:Buffer[Projectile]) extends Character(isEnemy, allCharacters, allProjectiles) {
  private val attackRange = if(!isEnemy){ 60 } else { -60 }
  private val maxHitpoints = 200
  private var hitpoints = maxHitpoints
  private val speed = if(!isEnemy){ 1.25f } else { -1.25f }
  private val charWidth = 55
  private val charHeight = 100
  private val xCoord = 190
  private val position = new Vector3(if(isEnemy){ Game.GAME_WORLD_WIDTH-xCoord }else{ xCoord }, 50, 0)
  private val hitbox = new Rectangle(position.x, position.y, charWidth, charHeight)
  private val killReward = 130
  private val damage = 75
  
  private val walkImage = if(!isEnemy){ new TextureAtlas("Animations/anim2.atlas") }else{ new TextureAtlas("Animations/anim5.atlas") }
  private val walkAnimation = new Animation[TextureRegion](1/7f, walkImage.getRegions)
  private val standImage = if(!isEnemy){ new TextureAtlas("Animations/stand2.atlas") }else{ new TextureAtlas("Animations/stand5.atlas") }
  private val standAnimation = new Animation[TextureRegion](1f, standImage.getRegions)
  private val hitImage = if(!isEnemy){ new TextureAtlas("hit2.atlas") }else{ new TextureAtlas("hit5.atlas") }
  private val hitAnimation = new Animation[TextureRegion](1/6f, hitImage.getRegions)
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
    this.advance()
    this.hitbox.setPosition(this.position.x, this.position.y)
    if(inCombat){
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
      this.isStanding = (allCharacters.exists(a => (a.getPosition.x - this.position.x > 0) && (a.getPosition.x - this.position.x < this.attackRange))) || this.position.x > Game.GAME_WORLD_WIDTH - 175 - this.attackRange
      this.inCombat = (allCharacters.exists(a => (a.getPosition.x - this.position.x > 0) && (a.getPosition.x - this.position.x < this.attackRange) && a.isEnemy)) || this.position.x > Game.GAME_WORLD_WIDTH - 175 - this.attackRange
      !this.isStanding
    }else{
      this.isStanding = (allCharacters.exists(a => (a.getPosition.x - this.position.x < 0) && (a.getPosition.x - this.position.x > this.attackRange ))) || this.position.x < 220 - this.attackRange
      this.inCombat = (allCharacters.exists(a => (a.getPosition.x - this.position.x < 0) && (a.getPosition.x - this.position.x > this.attackRange) && !a.isEnemy)) || this.position.x < 220 - this.attackRange
      !this.isStanding
    }
  }
  
  private def hitEnemy(): Unit ={
    this.timePassed += Gdx.graphics.getDeltaTime
    if(this.timePassed >= 4/6f){
      this.allProjectiles += new MeleeHit(this.position.x + this.attackRange, this.position.y, this.isEnemy, this.damage)
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
    if(this.inCombat){
      if(this.isEnemy){
        (hitAnimation,30)
      }else{
        (hitAnimation,0)
      }
    }else if(this.isStanding && !this.inCombat){
      (standAnimation,0)
    }else{
      (walkAnimation,0)
    }
  }
  
  def getHitpoints: Int = this.hitpoints
  
  def getMaxHitpoints: Int = this.maxHitpoints
  
  def getKillReward: Int = this.killReward
  
  def getSize: (Int, Int) = if(!this.isEnemy){ (this.charWidth - 20, this.charHeight) } else { (55, this.charHeight) }
  
   def dispose: Unit = {
      this.walkImage.dispose()
      this.standImage.dispose()
      this.hitImage.dispose()
      if(this.sound.isDefined) this.sound.get.dispose()
    }
  }