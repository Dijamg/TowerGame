package Objects

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.Texture

// A projectile with a horizontal path. Shot by marksman characters.
class RangedHit(xCoord: Float, yCoord: Float, fromEnemy: Boolean) extends Projectile(xCoord, yCoord,fromEnemy) {
  
  private val heigth: Int = if(!fromEnemy) { 15 } else { -5 }
  private val position: Vector3 = new Vector3(xCoord, yCoord + heigth ,0)
  private val hitbox: Rectangle = new Rectangle(position.x, position.y, 10,1)
  private val speed: Int = if(!fromEnemy){ 8 }else{ -8 }
  private val texture: Texture = if(!fromEnemy){ new Texture("arrow.png") }else{ new Texture("cannonball.png") }
  private val damage: Int = 15
  
  def update(dt: Float):Unit ={
    this.position.add(this.speed,0,0)
    this.hitbox.setPosition(this.position.x, this.position.y)
  }
     
  def getHitbox = this.hitbox
  
  def getPos = this.position
  
  def getDamage: Int = this.damage
  
  def getTexture: Texture = this.texture
  
  def canExplode: Boolean = false
  
  def dispose(): Unit = this.texture.dispose()
  
  
  
}