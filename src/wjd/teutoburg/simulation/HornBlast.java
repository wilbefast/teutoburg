/*
 Copyright (C) 2012 William James Dyce, Chloé Desdouits

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
package wjd.teutoburg.simulation;

import wjd.amb.control.EUpdateResult;
import wjd.amb.control.IDynamic;
import wjd.amb.view.ICanvas;
import wjd.amb.view.IVisible;
import wjd.math.V2;
import wjd.teutoburg.regiment.RegimentAgent;
import wjd.util.Timer;

/**
 *
 * @author wdyce
 * @since Dec 18, 2012
 */
public class HornBlast implements IVisible, IDynamic
{
  /* CONSTANTS */
  static final int LIFE_TIME = 1000; // milliseconds
	private static final float MAX_RADIUS = Tile.SIZE.x*10;
  private static final float MAX_THICKNESS = 10; // pixels
  
  /* ATTRIBUTES */
  
  public V2 position;
  private Timer life;
  public RegimentAgent source;
  
  /* METHDOS */
  
  // constructors
  public HornBlast(V2 position_, RegimentAgent source_)
  {
    this.position = position_;
    this.life = new Timer(LIFE_TIME);
      life.balance(0);
    this.source = source_;
  }

  /* IMPLEMENTS -- IVISIBLE */
  @Override
  public void render(ICanvas canvas)
  {
    canvas.setColour(source.getFaction().colour_tunic);
    canvas.setLineWidth(life.getEmptiness() * MAX_THICKNESS);
    canvas.circle(position, life.getFullness() * MAX_RADIUS, false);
  }

  /* IMPLEMENTS -- IDYNAMIC */
  @Override
  public EUpdateResult update(int t_delta)
  {
    return (life.update(t_delta) == EUpdateResult.FINISHED) 
           ? EUpdateResult.DELETE_ME
           : EUpdateResult.CONTINUE;
  }
  
}
