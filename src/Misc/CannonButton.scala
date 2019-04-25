package Misc

import com.badlogic.gdx.math.Rectangle
import scala.collection.mutable.Buffer
import Units._
import Objects._
import com.badlogic.gdx.graphics.Texture
/*By pressing this button a user is able to deploy and upgrade tower turrets in the game.
 * After each press the price is updated and user can press this button max 4 times during game.
 * In addition to x,y,width and heigth, this class also needs the data structures used to store
 * turrets, projectiles and characters, because you need them to add a new turret instance to game.*/

class CannonButton(x: Float, y: Float, width: Float, height: Float, allCharacters: Buffer[Character], allTurrets: Array[Turret], allProjectiles: Buffer[Projectile]) extends Button(x,y,width,height) {
  private val hitbox: Rectangle = new Rectangle(x,y,width,height)
  private val texture: Texture = new Texture("cannonButton.png")
  private var clicks = 0
  private var price: Int = 200
  
  private def updatePrice: Unit = {
    if(this.clicks == 1){
      this.price = 1000
    }else if(this.clicks == 2){
      this.price = 2500
    }else if(this.clicks == 3){
      this.price = 5000
    }else{
      this.price = 0
    }
  }
    
 
  /*First to presses bring 2 deploy a TowerCannon object on your castle. Next 2 presses upgrade them to DoubleCannons.
   * */
  def press: Unit = {
    if(this.clicks == 0){
      allTurrets(0) =  new TowerCannon(false, allCharacters, allProjectiles, 200) 
    }else if(this.clicks == 1){
      allTurrets(1) = new TowerCannon(false, allCharacters, allProjectiles, 250)
    }else if(this.clicks==2){
      allTurrets(0) = new DoubleCannon(false, allCharacters, allProjectiles, 200)
    }else if(this.clicks == 3){
      allTurrets(1) = new DoubleCannon(false, allCharacters, allProjectiles, 250)
    }
    clicks += 1
    this.updatePrice
  }
  
  def isHovered(x: Float, y: Float): Boolean = this.hitbox.contains(x,y)
  override def toString: String =if (clicks <2) this.price + "$ - Deploy a cannon" else if(clicks < 4) this.price + "$ - Upgrade a cannon" else "NO UPGRADES AVAILABLE."
  def getTexture: Texture = this.texture
  def getHitbox: Rectangle = this.hitbox
  def unitPrice: Int = this.price
  def getCooldown: Int = 0
  def dispose: Unit = this.texture.dispose
}