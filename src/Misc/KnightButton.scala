package Misc

import com.badlogic.gdx.math.Rectangle
import scala.collection.mutable.Buffer
import Units._
import Objects._
import com.badlogic.gdx.graphics.Texture

/*User can bring a Knight unit to the game by pressing this button.
 * */
class KnightButton(x: Float, y: Float, width: Float, height: Float, allCharacters: Buffer[Character], allProjectiles: Buffer[Projectile]) extends Button(x,y,width,height) {
  private val hitbox: Rectangle = new Rectangle(x,y,width,height)
  private val texture: Texture = new Texture("knightButton.png")
  private val price: Int = 100
  
  def press: Unit = this.allCharacters += new Knight(false, allCharacters, allProjectiles)
  def isHovered(x: Float, y: Float): Boolean = this.hitbox.contains(x,y)
  override def toString: String = this.price + "$ - Knight"
  def getTexture: Texture = this.texture
  def getHitbox: Rectangle = this.hitbox
  def unitPrice: Int = this.price
  def getCooldown: Int = 3
  def dispose: Unit = this.texture.dispose
}