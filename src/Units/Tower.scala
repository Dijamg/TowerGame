package Units

import Objects._
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Texture
import scala.collection.mutable.Buffer
import Game.Game
import com.badlogic.gdx.Gdx

// Tower classes. Implementation is almost same as characters except they cannot move and attack.
class Tower(val isEnemy: Boolean,val allCharacters: Buffer[Character], val allProjectiles:Buffer[Projectile]){
  private var hitpoints = 500
  private val width = if(!isEnemy){ 220 } else { 175 }
  private val height = if(!isEnemy){ 270 } else { 225 }
  private val xCoord = if(!isEnemy){0}else{Game.GAME_WORLD_WIDTH - 140}
  private val hitbox = new Rectangle(xCoord, 40, width, height)
  private val position = new Vector3(hitbox.x, hitbox.y, 0)
  private val castleImage = if(!isEnemy){ new Texture("castle1.png") }else{ new Texture("castle2.png") }

  
  def update(dt: Float){
     this.checkIfHit()
  }
  
  // Next 2 methods makes the tower detect attacks on it and take damage.
  private def getHit(projectile: Projectile){
    this.hitpoints -= projectile.getDamage
    this.allProjectiles -= projectile
  }
  
  private def checkIfHit(): Unit ={
    val isHit = this.allProjectiles.exists(a => a.getHitbox.overlaps(this.getHitbox) && (a.fromEnemy != this.isEnemy) && !a.isInstanceOf[SpecialHit])
    if(isHit){
      val hitBy = this.allProjectiles.find(a => a.getHitbox.overlaps(this.getHitbox) && a.fromEnemy != this.isEnemy && !a.isInstanceOf[SpecialHit]).get
      this.getHit(hitBy)
    }
  }
  
  def getPosition: Vector3 = this.position
  
  def getHitbox: Rectangle = this.hitbox
  
  def getHitpoints: Int = this.hitpoints
  
  def getMaxHitpoints: Int = 500
  
  def getTexture: Texture = this.castleImage
  
  def dispose(): Unit = this.castleImage.dispose()

}