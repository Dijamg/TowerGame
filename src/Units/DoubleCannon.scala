
package Units

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import scala.collection.mutable.Buffer
import com.badlogic.gdx.Gdx
import Game.Game
import Objects._

/* Does not differ much from TowerCannon class, so methods are explained there.
 * Stronger turret that shoots 2 projectiles each shot.
 */
class DoubleCannon(enemy: Boolean, allCharacters: Buffer[Character], allProjectiles:Buffer[Projectile], yCoord: Float) extends Turret(enemy, allCharacters, allProjectiles, yCoord) {
  
  private val texture: Texture = new Texture("turret2.png")
  private val xCoord: Float = if(!enemy){ 125 }else{ Game.GAME_WORLD_WIDTH - 140 }
  private val position: Vector2 = new Vector2(xCoord, yCoord)
  private val attackRange = 550
  
  private var inCombat: Boolean = false
  private var timePassed: Float = 0f
  private var angle: Float = 0f
  
  private var sound: Option[com.badlogic.gdx.audio.Sound] = None
  try{
    sound = Some(Gdx.audio.newSound(Gdx.files.internal("bang.mp3")))
  }catch{
    case e: Exception => // If exception is thrown, there will be no sound.
  }
  
  def update(dt: Float): Unit={
    this.timePassed += Gdx.graphics.getDeltaTime
    if(this.enemyFound){
      this.hitEnemy()
    }
  }
  
  private def hitEnemy(): Unit ={
    this.timePassed += Gdx.graphics.getDeltaTime
    val shootingHeigth = this.position.y + this.texture.getHeight/2
    val shootingWidth = if(!enemy){ this.position.x + this.texture.getWidth/2 }else{ this.position.x + this.texture.getWidth/2 }
    if(timePassed >= 1.5){
      if(!Game.muted && this.sound.isDefined) this.sound.get.play(0.05f)
      this.allProjectiles += new TurretShot(shootingWidth, shootingHeigth - 5, this.enemy, this.angle, false)
      this.allProjectiles += new TurretShot(shootingWidth, shootingHeigth + 3, this.enemy, this.angle, false)
      this.timePassed = 0f
    }
  }
  
  private def enemyFound(): Boolean ={
    if(!enemy){
      this.inCombat = this.allCharacters.exists(a => (a.getPosition.x < this.position.x + this.attackRange) && a.isEnemy)
      if(inCombat){
        this.angle = -Math.atan((this.position.y - 60) / (this.allCharacters.filter(_.isEnemy).head.getPosition.x - this.position.x)).toFloat
      }
      this.inCombat
    }else{
      this.inCombat = this.allCharacters.exists(a => (a.getPosition.x > this.position.x - this.attackRange) && !a.isEnemy)
      if(inCombat){
        this.angle = Math.atan((this.position.y - 110) / (this.position.x - this.allCharacters.filter(!_.isEnemy).head.getPosition.x)).toFloat
      }
      this.inCombat
    }
  }
  
  def getPosition: Vector2 = this.position
  
  def getTexture: (Texture, Float, Boolean) = {
    (this.texture, this.angle, this.enemy)
  }
  
  def dispose(): Unit = {
    this.texture.dispose()
    if(this.sound.isDefined) this.sound.get.dispose()
  }
  
  
}