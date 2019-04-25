package Units

import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.graphics.g2d.TextureRegion
import scala.collection.mutable.Buffer
import Objects.Projectile

/*This is a superclass for all the character in the game.
 *They take a buffer of all characters and projectiles in game, because they need to know the position, damage etc
 * of other characters and projectiles to detect collision etc.
 */
abstract class Character(val isEnemy: Boolean, private val allCharacters: Buffer[Character], private val allProjectiles:Buffer[Projectile]) {
  
  // Does all the things a character needs to do every frame. Called every frame from PlayState's main loop.
  def update(dt: Float): Unit

  //Some getters below
  def getPosition: Vector3
  
  def getHitbox: Rectangle
  
  def getHitpoints: Int
  
  def getMaxHitpoints: Int
  
  // The Int is just some xCoordinates the image needs to be shifted when drawn because of whitespace.
  def getAnimation: (Animation[TextureRegion], Int)
  
  def getKillReward: Int
  
  def getSize: (Int, Int)
  
  def dispose: Unit

}