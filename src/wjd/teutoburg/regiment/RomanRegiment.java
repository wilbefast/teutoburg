/*
 Copyright (C) 2012 William James Dyce

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package wjd.teutoburg.regiment;

import wjd.amb.control.EUpdateResult;
import wjd.math.M;
import wjd.math.V2;
import wjd.teutoburg.regiment.RegimentAgent.State;
import wjd.teutoburg.simulation.Tile;

/**
 *
 * @author wdyce
 * @since Dec 15, 2012
 */
public class RomanRegiment extends RegimentAgent
{
  /* CONSTANTS */
  private static final int REGIMENT_SIZE = 6*6;
  
  // combat
  private static final double BLOCK_CHANCE_TURTLE = 0.7;
  private static final double BLOCK_CHANCE_RABBLE = 0.3;
  private static final double ATTACK_CHANCE = 0.5;
  private static final int FLANK_MIN_ANGLE = 135;
  
  // movement
  private static final float SPEED_FACTOR = 0.6f;
  private static final float MAX_TURN_TURTLE 
                        = 40.0f * (float)Math.PI / 180.0f / 1000.0f, 
                          // 40 degrees per second
                            MAX_TURN_RABBLE
                        = 90.0f * (float)Math.PI / 180.0f / 1000.0f; 
                          // 90 degrees per second
  
  /* ATTRIBUTES */
  
  /* METHODS */

  // constructors
  
  public RomanRegiment(V2 position, Tile t, Faction faction)
  {
    super(position, REGIMENT_SIZE, t, faction);
  }

  // accessors

  // mutators
  
  /* IMPLEMENTS -- REGIMENTAGENT */
  
  @Override
  protected boolean canSee(RegimentAgent a)
  {
	  if(state == State.WAITING && !a.tile.forest_amount.isEmpty())
		  return false;
	  else
		  return true;
  }

  
  // artificial intelligence
  @Override
  protected EUpdateResult ai(int t_delta, Iterable<Tile> percepts)
  {
	  V2 escape_direction = getCircle().centre.clone().add(0, -10);
    
    
	  if(state == State.FIGHTING)
	  {
		  if(nearestEnemy == null)
			  state = State.WAITING;
	  }
	  else if(state == State.WAITING)
	  {
		  if(nearestEnemy != null)
		  {
			  state = State.CHARGING;
		  }
		  else
		  {
			  V2 new_direction = escape_direction.clone(), tmp;
			  int nbPossibleNeigh = 1;
			  for(Tile t : percepts)
			  {
				  if(	t != tile 
						&& t.pixel_position.y >= tile.pixel_position.y
						&& t.pixel_position.x != tile.pixel_position.x)
				  {
					  nbPossibleNeigh++;
					  if(!(t.forest_amount.isEmpty()))
					  {
						  tmp = new V2(t.pixel_position, c.centre);
						  tmp.normalise();
						  tmp.scale(t.forest_amount.balance());
						  new_direction.add(tmp);
						  nbPossibleNeigh--;
					  }
				  }
			  }
			  if(nbPossibleNeigh == 1)
				  turnTowardsGradually(escape_direction, getMaxTurn());
			  else
				  turnTowardsGradually(new_direction, getMaxTurn());
			  if(advance(SPEED_FACTOR*t_delta) == EUpdateResult.DELETE_ME)
				  return EUpdateResult.DELETE_ME;
		  }
	  }
    
    
	  else if(state == State.CHARGING)
	  {
		  if(nearestEnemy != null)
		  {
			  turnTowardsGradually(nearestEnemy.getCircle().centre, getMaxTurn());
			  if(V2.coline(direction, new V2(c.centre, nearestEnemy.getCircle().centre)))
			  {
				  float nearestEnemyDist = (float)Math.sqrt(nearestEnemyDist2);
				  float min = Math.min(SPEED_FACTOR * t_delta, nearestEnemyDist);
				  advance(min);
			  }
		  }
		  else
		  {
			  state = State.WAITING;
		  }
	  }
	  return EUpdateResult.CONTINUE;
  }
  
  
  // parameters
  @Override
  protected double chanceToBlock(RegimentAgent attacker)
  {
    if(isFormedUp())
    {
      // deform if flank-attack
      if((V2.angleBetween(getDirection(), attacker.getDirection())*180.0/2.0) 
            < FLANK_MIN_ANGLE)
      {
        setFormedUp(false);
      }
      else
        return BLOCK_CHANCE_TURTLE;
    }
    return BLOCK_CHANCE_RABBLE;
  }
  
  @Override
  protected double chanceToHit(RegimentAgent defender)
  {
    return ATTACK_CHANCE;
  }
  
  @Override
  protected boolean isEnemy(RegimentAgent other)
  {
    return (other.state == State.DEAD) 
          ? false
          : (other instanceof BarbarianRegiment);
  }
  
  @Override
  protected boolean isAlly(RegimentAgent other)
  {
    return (other.state == State.DEAD) 
          ? false
          : (other instanceof RomanRegiment);
  }
  
  
  /* SUBROUTINES */

  private float getMaxTurn()
  {
    return ((isFormedUp()) ? MAX_TURN_TURTLE : MAX_TURN_RABBLE); 
  }
}
