
package Units

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import scala.collection.mutable.Buffer
import com.badlogic.gdx.Gdx
import Game.Game
import Objects._

// A weak cannon which can fire single shots.
class TowerCannon(enemy: Boolean, allCharacters: Buffer[Character], allProjectiles:Buffer[Projectile], yCoord: Float) extends Turret(enemy, allCharacters, allProjectiles, yCoord) {
  
  private val texture: Texture = new Texture("turret1.png")
  private val xCoord: Float = if(!enemy){ 125 }else{ Game.GAME_WORLD_WIDTH - 140 }
  private val position: Vector2 = new Vector2(xCoord, yCoord)
  private val attackRange = 550
  
  private var inCombat: Boolean = false
  private var timePassed: Float = 0f
  
  // Angle between the turret and the enemy in range.
  private var angle: Float = 0f                
  
  private var sound: Option[com.badlogic.gdx.audio.Sound] = None
  try{
    sound = Some(Gdx.audio.newSound(Gdx.files.internal("woosh.wav")))
  }catch{
    case e: Exception => // If exception is thrown, there will be no sound.
  }
  
  //Every frame, it checks if an enemy can be found inside attack range and attacks it if found.
  def update(dt: Float): Unit={
    this.timePassed += Gdx.graphics.getDeltaTime
    if(this.enemyFound){
      this.hitEnemy()
    }
  }
  
  // Shoots the enemy
  private def hitEnemy(): Unit ={
    this.timePassed += Gdx.graphics.getDeltaTime
    if(this.timePassed >= 1.5){
      if(!Game.muted && this.sound.isDefined) this.sound.get.play(1f)
      this.allProjectiles += new TurretShot(this.position.x + this.texture.getWidth/2 , this.position.y + texture.getHeight/2, this.enemy, this.angle,true)
      this.timePassed = 0f
    }
  }
  
  /* Scans in front and checks if enemies are found inside attack range.
   * If found, it calculates the angle between it self and the first enemy in range.
   */
  private def enemyFound(): Boolean ={
    if(!enemy){
      this.inCombat = this.allCharacters.exists(a => (a.getPosition.x < this.position.x + this.attackRange) && a.isEnemy)
      if(inCombat){
        this.angle = -Math.atan((this.position.y - 60) / (this.allCharacters.filter(_.isEnemy).sortBy(_.getPosition.x).head.getPosition.x - this.position.x)).toFloat
      }
      this.inCombat
    }else{
      this.inCombat = this.allCharacters.exists(a => (a.getPosition.x > this.position.x - this.attackRange) && !a.isEnemy)
      if(inCombat){
        this.angle = Math.atan((this.position.y - 100) / (this.position.x - this.allCharacters.filter(!_.isEnemy).head.getPosition.x)).toFloat
      }
      this.inCombat
    }
  }
  
  def getPosition: Vector2 = this.position
  
  /* The draw render method in the states need the texture, angle and the boolean indicating if it is a enemy turret.
   * Angle is needed to draw the image in right angle so it faces the enemy it shoots and boolean is needed because
   * the enemy turret texture needs to be mirrored before it is drawn.
   */
  def getTexture: (Texture, Float, Boolean) = {
    (this.texture, this.angle, this.enemy)
  }
  
  def dispose(): Unit = {
    this.texture.dispose()
    if(this.sound.isDefined) this.sound.get.dispose
  }
  
  
}