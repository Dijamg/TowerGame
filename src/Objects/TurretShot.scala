package Objects

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Texture

// Projectiles shot from castle turrets. They have a diagonal path.

class TurretShot(xCoord: Float, yCoord: Float, fromEnemy: Boolean, angle: Float, singleCannon: Boolean) extends Projectile(xCoord, yCoord, fromEnemy) {
  
  private val position: Vector3 = new Vector3(xCoord, yCoord ,0)
  private val hitbox: Rectangle = new Rectangle(position.x, position.y, 5,5)
  private val damage: Int = 10
  // Every frame the projectile moves 8 units horizontally and tan(angle)*8 units diagonally.
  private val speed: Vector3 = if(!fromEnemy){ new Vector3(8, (8 * Math.tan(angle)).toFloat, 0) }else{ new Vector3(-8, (8 * Math.tan(-angle)).toFloat, 0) }
  private val texture: Texture = if(singleCannon){new Texture("cannonball.png")}else{new Texture("bullet.png") }
  
  def getHitbox = this.hitbox
  def getPos = this.position
  
  def update(dt: Float):Unit ={
    this.position.add(this.speed)
    this.hitbox.setPosition(this.position.x, this.position.y)
  }
  
  def getDamage:Int = this.damage
  
  def getTexture: Texture = this.texture
  
  def canExplode: Boolean = false
  
  def dispose(): Unit = this.texture.dispose()
}